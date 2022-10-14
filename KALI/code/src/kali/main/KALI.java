package kali.main;

import static org.junit.Assert.assertTrue;

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

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.generator.util.Utility;
import ctwedge.util.ModelUtils;
import ctwedge.util.Pair;
import kali.safeelements.ExtendedSemaphore;
import kali.safeelements.SafeQueue;
import kali.safeelements.TestContext;
import kali.threads.TestBuilder;
import kali.threads.TupleFiller;
import kali.util.Operations;
import kali.util.Order;

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
	
	@Option(name = "-sort", usage = "activate sort optimization (if not specified, sort is not activated)") boolean SORT = false;
	
	@Option(name = "-order", usage = "parameter ordering during tuple generation [IN_ORDER_SIZE_DESC, IN_ORDER_SIZE_ASC, RANDOM, AS_DECLARED] (if not specified, IN_ORDER_SIZE_DESC is used)")
	private String ORDER_STR = "IN_ORDER_SIZE_DESC";	
	public static Order ORDER = Order.IN_ORDER_SIZE_DESC;
	
	@Option(name = "-solver", usage = "solver to be used in test context [MATHSAT, SMTINTERPOL, Z3, PRINCESS, BOOLECTOR, CVC4, YICES2] (if not specified, SMTINTERPOL is used)")
	private String SOLVER = "SMTINTERPOL";	

	// receives other command line parameters than options
	@Argument
	private List<String> arguments = new ArrayList<String>();

	public static void main(String[] args) throws IOException, InterruptedException {
		new KALI().doMain(args);
	}

	@SuppressWarnings("deprecation")
	public void doMain(String[] args) throws IOException, InterruptedException {
		
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
				// TODO: We should check that the strength is lower than the number of parameters of the model
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
			return;
		}
		String fileName = arguments.get(1);
		// Read the combinatorial model and get the MDD representing the model without constraints
		CitModel m = Utility.loadModelFromPath(fileName);

		int nCovered = 0;
		int totTuples = 0;
		
		// Set parameter ordering strategy
		ORDER = Order.valueOf(ORDER_STR);
		
		// Set SMT solver to be used
		TestContext.SMTSolver = Solvers.valueOf(SOLVER);

		// Get current time
		long start = System.currentTimeMillis();

		// Compute the position of each parameter
		Map<String, Integer> paramPosition = new HashMap<String, Integer>();
		paramPosition = Operations.setParamPosition(m);

		// Shared object between producer and consumer
		SafeQueue tuples = new SafeQueue();

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
		
		ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
		Vector<TestContext> tcList = new Vector<TestContext>();
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

		// Compute the summary values
		System.out.println("-----TEST SUITE-----");
		
		// First row -> parameter names
		String header = "";
		for (Parameter param : m.getParameters()) {
			header += param.getName() + ";";
		}
		System.out.println(header.substring(0, header.length()-1));
		
		HashSet<String> tests = new HashSet<String>();
		
		// Remove empty contexts
		tcList.removeIf(x -> x.getNCovered() == 0);
		
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
		tests.forEach(x -> {System.out.println(x);});

		// Print the tests
		if (verbose) {
			System.out.println("Covered: " + nCovered + " tuples");
			System.out.println("Uncovered: " + (totTuples - nCovered) + " tuples");
			System.out.println("Total number of tuples: " + totTuples + " tuples");
			System.out.println(
					"Time required for test suite generation: " + (System.currentTimeMillis() - start) + " ms");
			System.out.println("Generated " + tcList.size() + " tests");
		} else {
			FileWriter fw = new FileWriter(OUTPUT_TXT, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(fileName + ";" + tests.size() + ";" + (System.currentTimeMillis() - start) + ";" + SORT + ";"
					+ ORDER.toString() + ";" + nThreads + ";" + TestContext.SMTSolver);
			bw.newLine();
			bw.close();
		}

		// Join the tuple filler thread
		tFillerThread.join();
	}

}
