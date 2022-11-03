package pMedici.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.colomoto.mddlib.MDDManager;
import org.eclipse.emf.common.util.EList;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.generator.util.ParameterElementsGetterAsStrings;
import ctwedge.generator.util.Utility;
import ctwedge.util.TestSuite;
import pMedici.combinations.TupleGenerator;
import pMedici.experiments.pMEDICIExperimenter;
import pMedici.experiments.pMEDICIPlusMTExperimenter;
import pMedici.importer.CSVImporter;
import pMedici.safeelements.ExtendedSemaphore;
import pMedici.safeelements.SafeQueue;
import pMedici.safeelements.TestContext;
import pMedici.threads.TestBuilder;
import pMedici.threads.TupleFiller;
import pMedici.util.ModelToMDDConverter;
import pMedici.util.Operations;
import pMedici.util.Pair;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Main class to call pMedici
 */
public class PMedici implements Callable<Integer> {

	@Parameters(index = "0", description = "The strength for test generation.")
	int strength = 2;

	@Parameters(index = "1", description = "The name of the file containing the model in CTW format.")
	String fileName = "";

	@Option(names = "-n", description = "Number of threads to be used for test building. Do not specify (or set to 0) if the one of the system architecture has to be used.")
	private int nThreads = Runtime.getRuntime().availableProcessors();

	/** Use the verbose mode */
	@Option(names = "-verb", description = "Use the verbose mode.")
	boolean verb;

	/** Load a previous test suite */
	@Option(names = "-old", description = "CSV file containing the old test suite, with commas and header in the first row")
	private String oldTs = "";

	/**
	 * Variable used to share the size of the generated test suite with the class
	 * {@link pMEDICIExperimenter}
	 */
	public static int testSuiteSize = -1;

	/**
	 * Variable used to share the size of the reduced generated test suite (no
	 * duplicated tests) with the class {@link pMEDICIPlusMTExperimenter}
	 */
	public static int reducedTestSuiteSize = -1;

	/**
	 * Variable used to share the number of thread used in generation with the class
	 * {@link pMEDICIExperimenter}
	 */
	public static int threadsNum = -1;

	/** the model for the generation */
	CitModel model;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException          Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		PMedici pMedici = new PMedici();
		int exitCode = new CommandLine(pMedici).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() throws Exception {
		generateTests(fileName, strength, nThreads);
		return 0;
	}

	/**
	 * Covert from ctwedge to medici and returns the model as string
	 * 
	 * @param model the CTWedge model
	 * @return the MEDICI model as string
	 * @throws IOException
	 */
	public static String buildMediciModel(CitModel model) throws IOException {
		MediciCITGenerator gen = new MediciCITGenerator();
		MediciCITGenerator.OUTPUT_ON_STD_OUT_DURING_TRANSLATION = false;
		// If no constraints are present into the model, then we can ignore constraints
		return gen.translateModel(model, model.getConstraints().size() == 0);
	}

	/**
	 * Generate tests.
	 *
	 * @param fileName the file name containing the cit model in CTWEDGE format!
	 * @param strength the strength
	 * @param nThreads the number of threads to be used
	 * @return the test suite
	 * @throws IOException          Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public TestSuite generateTests(String fileName, int strength, int nThreads)
			throws IOException, InterruptedException {
		// Get current time
		long start = System.currentTimeMillis();
		CitModel model = null;

		// Read the model in CTWedge format
		if (!fileName.equals("")) {
			model = Utility.loadModelFromPath(fileName);
		} else {
			assert false : "You must specify the name of the file containing the CTWedge model";
		}

		ModelToMDDConverter mc = new ModelToMDDConverter(model);
		MDDManager manager = mc.getMDD();
		int baseMDD = mc.getStartingNode();
		int nCovered = 0;
		int totTuples = 0;

		// Add to the baseNode the constraints
		baseMDD = Operations.updateMDDWithConstraints(manager, model, baseMDD);

		// Shared object between producer and consumer
		SafeQueue tuples = new SafeQueue();

		// Combination generator
		Iterator<List<Pair<Integer, Integer>>> tg = TupleGenerator.getAllKWiseCombination(model, strength);

		// Start the filler thread
		TupleFiller tFiller = new TupleFiller(tg, tuples);
		Thread tFillerThread = new Thread(tFiller);
		tFillerThread.start();

		if (nThreads == 0)
			nThreads = Runtime.getRuntime().availableProcessors();

		// Test contexts
		ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
		Vector<TestContext> tcList = new Vector<TestContext>();

		// If old test suite file is specified, load it in the tcList
		if (oldTs != "") {
			Vector<Map<String, String>> oldTests = CSVImporter.read(oldTs);
			initializeTestContexts(model, manager, baseMDD, tcList, oldTests);
		}

		// Start all the TestBuilder threads
		boolean sort = false;
		ArrayList<Thread> testBuilderThreads = new ArrayList<Thread>();
		int nParams = model.getParameters().size();
		boolean useConstraints = model.getConstraints().size() > 0;
		for (int i = 0; i < nThreads; i++) {
			Thread tBuilder = new Thread(new TestBuilder(baseMDD, tuples, tcList, sort, nParams, useConstraints,
					manager, testContextsMutex, verb));
			testBuilderThreads.add(tBuilder);
			tBuilder.start();
		}

		// Join all the test builder threads
		for (int i = 0; i < nThreads; i++) {
			testBuilderThreads.get(i).join();
		}

		// Save the tests
		ArrayList<String> testCases = new ArrayList<String>();
		for (TestContext tc : tcList) {
			nCovered += tc.getNCovered();
			testCases.add(tc.getTest(false));
		}

		// Print test suite
		System.out.println("-----TEST SUITE-----");
		String tsAsCSV = Operations.translateOutputToString(testCases, model);
		System.out.println(tsAsCSV);
		long generationTime = (System.currentTimeMillis() - start);

		if (verb) {
			totTuples = tuples.getNTuples();
			System.out.println("Covered: " + nCovered + " tuples");
			System.out.println("Uncovered: " + (totTuples - nCovered) + " tuples");
			System.out.println("Total number of tuples: " + totTuples + " tuples");
			System.out.println("Time required for test suite generation: " + generationTime + " ms");
		}

		// Join the tuple filler thread
		tFillerThread.join();

		// Create and return the test suite
		TestSuite testSuite = new TestSuite(tsAsCSV, model);
		testSuite.setStrength(strength);
		testSuite.setGeneratorTime(generationTime);

		return testSuite;
	}

	/**
	 * Pre-initialize the list of the test contexts using a previously existing test
	 * suite
	 * 
	 * @param model    : the CTWedge model
	 * @param manager  : the MDD manager
	 * @param baseMDD  : the starting node of the MDD
	 * @param tcList   : the list of tests contexts
	 * @param oldTests : the map containing the old tests
	 * @throws InterruptedException
	 */
	private void initializeTestContexts(CitModel model, MDDManager manager, int baseMDD, Vector<TestContext> tcList,
			Vector<Map<String, String>> oldTests) throws InterruptedException {

		int nParams = model.getParameters().size();
		boolean useConstraints = model.getConstraints().size() > 0;
		// Fetch all the old tests and create new test contexts
		for (Map<String, String> oldTest : oldTests) {
			// Creating the tuple related to the current iteration: we need to create a
			// tuple because the method that verify
			// the constraints accepts only the type Vector<Pair<Integer, Integer>>
			Vector<Pair<Integer, Integer>> tupleNew = new Vector<Pair<Integer, Integer>>();

			// New test context
			TestContext tc = new TestContext(baseMDD, nParams, useConstraints, manager);

			EList<Parameter> parameters = model.getParameters();
			for (int tupleIndex = 0; tupleIndex < parameters.size(); tupleIndex++) {
				Parameter param = parameters.get(tupleIndex);
				// If the parameter of the new model is in the old test suite,
				// its value is added in the corresponding position in the current tuple
				String testParamValue;
				if ((testParamValue = oldTest.get(param.getName())) != null) {					
					List<String> values = ParameterElementsGetterAsStrings.instance.doSwitch(param);
					int value = values.indexOf(testParamValue);
					if (value != -1) {
						
						tupleNew.add(new Pair<Integer, Integer>(tupleIndex, value));
	
						// If also partial tests should be kept, verify assignment per assignment
						if (!tupleNew.isEmpty() && TestBuilder.KeepPartialOldTests) {
							if (tc.verifyWithMDD(tupleNew)) {
								tc.addTuple(tupleNew, false);
							}
						}
					} 
				}
			}
			
			// If we added at least one parameter test value to the tuple, then
			// we check if the created tuple is valid with the model constraints
			if (!tupleNew.isEmpty()) {			

				// If the test context has already been updated step by step, skip the part
				// adding the new tuple
				if (!TestBuilder.KeepPartialOldTests) {

					// If the tuple is compatible with the constraints, then we add the
					// add the tuple to the current test context and we add the
					// current test context to the list of all the test contexts from
					// which the algorithm of medici will later start executing
					if (tc.verifyWithMDD(tupleNew)) {

						// Adding the tuple to the current test context
						// Notice: this method also update the mdd of the text context
						tc.addTuple(tupleNew, false);

						// Adding the tc to the shared list of all the test context tcList
						tcList.add(tc);

					}
				} else {
					tcList.add(tc);
				}
			}
		}
		
		if (verb) {
			System.out.println("Created " + tcList.size() + " test contexts from the previous test suite");
		}
	}

	/**
	 * Returns the model used by pMEDICI
	 * 
	 * @return the model used by pMEDICI
	 */
	public CitModel getModel() {
		return this.model;
	}
}