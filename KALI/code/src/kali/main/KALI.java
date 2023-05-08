package kali.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.generator.util.ParameterElementsGetterAsStrings;
import ctwedge.generator.util.Utility;
import ctwedge.util.ModelUtils;
import ctwedge.util.Pair;
import ctwedge.util.TestSuite;
import kali.safeelements.ExtendedSemaphore;
import kali.safeelements.SafeQueue;
import kali.safeelements.TestContext;
import kali.threads.TestBuilder;
import kali.threads.TupleFiller;
import kali.util.Operations;
import kali.util.Order;
import scala.util.Random;

public class KALI {

	/**
	 * The file in which the output should be saved
	 */
	static String OUTPUT_TXT = "output.txt";

	public static Boolean PRINT_DEBUG = false;

	/**
	 * The number of threads to be used
	 */
	@Option(name = "-n", usage = "use n threads (if not specified or -1, use the number of threads of the architecture")
	private int nThreads = -1;

	// if verbose print in the console otherwise print the data onfile
	@Option(name = "-verbose", usage = "verbose output on the console")
	private boolean verbose = false;

	@Option(name = "-sort", usage = "activate sort optimization (if not specified, sort is not activated)")
	boolean SORT = false;

	@Option(name = "-order", usage = "parameter ordering during tuple generation [IN_ORDER_SIZE_DESC, IN_ORDER_SIZE_ASC, RANDOM, AS_DECLARED] (if not specified, IN_ORDER_SIZE_DESC is used)")
	private String ORDER_STR = "IN_ORDER_SIZE_DESC";
	public static Order ORDER = Order.IN_ORDER_SIZE_DESC;

	@Option(name = "-solver", usage = "solver to be used in test context [MATHSAT, SMTINTERPOL, Z3, PRINCESS, BOOLECTOR, CVC4, YICES2] (if not specified, SMTINTERPOL is used)")
	private String SOLVER = "SMTINTERPOL";

	@Option(name = "-r", usage = "randomize: start the test generation with a random test suite and then complete/fix it")
	private boolean randomize = false;

	// receives other command line parameters than options
	@Argument
	private List<String> arguments = new ArrayList<String>();

	public static void main(String[] args) throws IOException, InterruptedException {
		new KALI().doMain(args);
	}

	@SuppressWarnings("deprecation")
	public TestSuite doMain(String[] args) throws IOException, InterruptedException {
		CmdLineParser parser = new CmdLineParser(this);
		Integer strength = null;
		try {
			// Parse the arguments.
			parser.parseArgument(args);
			// After parsing arguments, you should check if enough arguments are given.
			if (arguments.isEmpty())
				throw new CmdLineException(parser, "No argument is given");
			// Check the arguments
			try {
				strength = Integer.parseInt(arguments.get(0));
				if (strength < 2)
					throw new CmdLineException(parser, "strength cannot be less than 2");
			} catch (NumberFormatException nf) {
				throw new CmdLineException(parser, "strength must be a number >= 2 " + nf.getLocalizedMessage());
			}
			// Check that the file containing the model actually exists
			if (!(Files.exists(Paths.get(arguments.get(1))))) {
				throw new CmdLineException(parser, "file " + arguments.get(1) + " not found");
			}
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("kali [options...] strength file.ctw");
			// Print the list of available options
			parser.printUsage(System.err);
			System.err.println();
			return null;
		}
		String fileName = arguments.get(1);
		// Read the combinatorial model and get the MDD representing the model without
		// constraints
		CitModel m = Utility.loadModelFromPath(fileName);
		// The chosen strength must be lower or equal to the number of parameters
		if (m.getParameters().size() < strength) {
			System.err.println("strength cannot be higher than the number of parameters");
			return null;
		}

		// Set parameter ordering strategy
		ORDER = Order.valueOf(ORDER_STR);

		// Set SMT solver to be used
		TestContext.SMTSolver = Solvers.valueOf(SOLVER);

		return testGeneration(strength, fileName, m);
	}

	
	TestSuite testGeneration(Integer strength, String fileName, CitModel m)
			throws InterruptedException, IOException {
		// Get current time
		long start = System.currentTimeMillis();

		// Compute the position of each parameter
		Map<String, Integer> paramPosition = Operations.setParamPosition(m);

		// Shared object between producer and consumer
		SafeQueue tuples = new SafeQueue();

		// If the randomization has to be used, then generate random tests and
		// preprocess them as seeds
		Vector<TestContext> tcSeedsList = new Vector<TestContext>();
		if (randomize) {
			HashSet<String> testSeeds = new HashSet<String>();
			try {
				testSeeds = generateRandomTests(m);
				tcSeedsList = preprocessTestSeeds(m, paramPosition, testSeeds);
			} catch (InterruptedException | SolverException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}

		// Combination generator
		Map<String, List<Object>> elements = Operations.getElementsMap(m, ORDER);
		Iterator<List<Pair<String, Object>>> tg = ModelUtils.getAllKWiseCombination(elements, strength);

		// Start the filler thread
		TupleFiller tFiller = new TupleFiller(tg, tuples);
		Thread tFillerThread = new Thread(tFiller);
		tFillerThread.start();

		// Start all the TestBuilder threads
		if (nThreads == -1) {
			nThreads = Runtime.getRuntime().availableProcessors();
			if (KALI.PRINT_DEBUG)
				System.out.println("using " + nThreads + " threads");
		}

		// Generate test cases
		Vector<TestContext> tcList = testGeneration(m, paramPosition, tuples, tFillerThread, tcSeedsList);
		// Remove empty contexts
		tcList.removeIf(x -> x.getNCovered() == 0);

		// print the test suite and convert to test suite as ctwedge object
		// Compute the summary values
		HashSet<String> tests = new HashSet<String>();
		int nCovered = 0;
		int totTuples = 0;
		System.out.println("-----TEST SUITE-----");
		// First row -> parameter names
		String header = "";
		for (Parameter param : m.getParameters()) {
			header += param.getName() + ";";
		}
		System.out.println(header.substring(0, header.length() - 1));

		for (TestContext tc : tcList) {
			nCovered += tc.getNCovered();
			try {
				tests.add(tc.getTest(false));
			} catch (InterruptedException | SolverException e) {
				System.out.println(e.getMessage());
			}
			// Close the context
			tc.close();
		}
		totTuples = tuples.getNTuples();

		// Print the tests
		tests.forEach(x -> {
			System.out.println(x);
		});

		// Print the tests
		long generationTime = System.currentTimeMillis() - start;
		if (verbose) {
			System.out.println("Covered: " + nCovered + " tuples");
			System.out.println("Uncovered: " + (totTuples - nCovered) + " tuples");
			System.out.println("Total number of tuples: " + totTuples + " tuples");
			System.out.println("Time required for test suite generation: " + generationTime + " ms");
			System.out.println("Generated " + tcList.size() + " tests");
		} else {
			FileWriter fw = new FileWriter(OUTPUT_TXT, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(fileName + ";" + tests.size() + ";" + generationTime + ";" + SORT + ";" + ORDER.toString() + ";"
					+ nThreads + ";" + TestContext.SMTSolver);
			bw.newLine();
			bw.close();
		}
		// Create and return the test suite
	 String tsAsCSV = header.substring(0, header.length() - 1) + "\n";
		for (String t : tests) {
			tsAsCSV += t + "\n";
		}
		TestSuite testSuite = new TestSuite(tsAsCSV, m);
		testSuite.setStrength(strength);
		testSuite.setGeneratorTime(generationTime);

		return testSuite;
	}

	private Vector<TestContext> testGeneration(CitModel m, Map<String, Integer> paramPosition, SafeQueue tuples,
			Thread tFillerThread, Vector<TestContext> tcSeedsList) throws InterruptedException {
		ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
		Vector<TestContext> tcList = new Vector<TestContext>();
		// If seeds have been used, append them to the test context list
		tcList.addAll(tcSeedsList);
		
		int nParams = m.getParameters().size();
		ArrayList<Thread> testBuilderThreads = new ArrayList<Thread>();
		for (int i = 0; i < nThreads; i++) {
			Thread tBuilder = new Thread(
					new TestBuilder(tuples, tcList, SORT, nParams, nParams > 0, testContextsMutex, paramPosition, m));
			testBuilderThreads.add(tBuilder);
			tBuilder.start();
		}

		// Join all the test builder threads
		for (int i = 0; i < nThreads; i++) {
			testBuilderThreads.get(i).join();
		}

		// Join the tuple filler thread
		tFillerThread.join();
		return tcList;
	}

	private Vector<TestContext> preprocessTestSeeds(CitModel m, Map<String, Integer> paramPosition,
			HashSet<String> testSeeds) throws InterruptedException, SolverException, InvalidConfigurationException {
		Vector<TestContext> tcList = new Vector<TestContext>();
		int nParams = m.getParameters().size();
		boolean useConstraints = m.getConstraints().size() > 0;
		// Fetch all the old tests and create new test contexts
		for (String oldTest : testSeeds) {
			// New test context
			TestContext tc = new TestContext(nParams, useConstraints, paramPosition, m);
			String[] values = oldTest.split(";");

			// Creating the tuple related to the current iteration: we need to create a
			// tuple because the method that verify
			// the constraints accepts only the type Vector<Pair<String, Object>>
			Vector<Pair<String, Object>> tupleNew = new Vector<Pair<String, Object>>();

			EList<Parameter> parameters = m.getParameters();
			for (int tupleIndex = 0; tupleIndex < parameters.size(); tupleIndex++) {
				Parameter param = parameters.get(tupleIndex);
				// If the parameter of the new model is in the old test suite,
				// its value is added in the corresponding position in the current tuple
				String testParamValue;

				if ((testParamValue = values[paramPosition.get(param.getName())]) != null) {
					if (testParamValue != "") {
						tupleNew.add(new Pair<String, Object>(param.getName(), testParamValue));

						// Verify assignment per assignment
						if (!tupleNew.isEmpty()) {
							if (tc.isCoverable(tupleNew)) {
								tc.addTuple(tupleNew);
							}
						}
					}
				}
			}
			tc.resetCovered();

			// If we added at least one parameter test value to the tuple, then the new
			// test context can be kept
			if (!tupleNew.isEmpty())
				tcList.add(tc);

		}

		if (verbose) {
			System.out.println("Created " + tcList.size() + " test contexts from test seeds");
		}

		return tcList;
	}
	

	private HashSet<String> generateRandomTests(CitModel m) {
		HashSet<String> resList = new HashSet<String>();
		List<List<String>> paramValues = new ArrayList<List<String>>();
		int NTEST = 1000;
		Random r = new Random();
		
		// Visit the possible values
		for (Parameter p : m.getParameters()) {
			// Get all values
			List<String> valuesList = ParameterElementsGetterAsStrings.instance.doSwitch(p);
			paramValues.add(valuesList);
		}
		
		for (int i=0; i<NTEST; i++) {
			String thisTest = "";
			for (int j=0; j<paramValues.size(); j++) {
				// Get all values
				List<String> valuesList = paramValues.get(j);
				
				// Extract a random value
				thisTest += valuesList.get(r.nextInt(valuesList.size())) + ";";
			}
			resList.add(thisTest);			
		}
		return resList;
	}

}
