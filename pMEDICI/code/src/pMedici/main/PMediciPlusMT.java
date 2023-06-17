package pMedici.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.colomoto.mddlib.MDDManager;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.ext.Utility;
import pMedici.experiments.pMEDICIPlusMTExperimenter;
import pMedici.importer.CSVImporter;
import pMedici.safeelements.SafeQueue;
import pMedici.threads.TestBuilder;
import pMedici.threads.TestEarlyFiller;
import pMedici.threads.TupleFiller;
import pMedici.util.ExtendedSemaphore;
import pMedici.util.ModelToMDDConverter;
import pMedici.util.Operations;
import pMedici.util.Pair;
import pMedici.util.TestContext;
import pMedici.util.TestModel;
import pMedici.util.TupleGenerator;

/**
 * Multithread version of pMEDICI+
 * 
 * @author Luca Parimbelli
 * 
 */
@Deprecated
public class PMediciPlusMT {

	public static boolean PRINT_DEBUG = false;

	/**
	 * Variable used to share the size of the generated test suite with the class
	 * {@link pMEDICIPlusMTExperimenter}
	 */
	public static int testSuiteSize = -1;

	/**
	 * Variable used to share the size of the reduced generated test suite (no
	 * duplicated tests) with the class {@link pMEDICIPlusMTExperimenter}
	 */
	public static int reducedTestSuiteSize = -1;

	/**
	 * Variable used to share the number of thread used in generation with the class
	 * {@link pMEDICIPlusMTExperimenter}
	 */
	public static int threadsNum = -1;

	/**
	 * Variable used to share the size of tcList after the initialization with the
	 * TestEarlyFiller threads with the class {@link pMEDICIPlusMTExperimenter}
	 */
	public static int tcListInitialSize = -1;
	
//	/**
//	 * Variable used to share the tcList after the initialization with the
//	 * TestEarlyFiller threads with the class {@link pMEDICIPlusMTExperimenter}
//	 */
//	public static String tcListInitial = "";

	/**
	 * Variable used to share with the class {@link pMEDICIPlusMTExperimenter} the
	 * time required for the first part of the algorithm. This is the time required
	 * for filling the initial tcList with the old valid test cases.
	 */
	public static long timeForOldTSFilling = -1;

	public static void main(String[] args) throws IOException, InterruptedException {
		
		String evolvedModelPath = "";
		String oldTestSuiteFilePath = "";
		String exportFilePath = "";

		int strength = 2;
		CitModel model = null;
		boolean verb = false;

		// Read the test model from arguments
		// args[0] = t-wise strength
		// args[1] = file name (path) to the CTWedge evolved model
		// args[2] = file name (path) to the test suite (.csv) of the old model
		// args[3] = file name (path) to the file where to export the test suite (.csv)
		// args[4] = boolean true/false for verb
		if (args.length >= 4) {
			strength = Integer.parseInt(args[0]);
			evolvedModelPath = args[1];
			model = Utility.loadModel(evolvedModelPath);
			oldTestSuiteFilePath = args[2];
			exportFilePath = args[3];
			if (args.length > 4)
				verb = Boolean.parseBoolean(args[4]);
		} else {
			throw new RuntimeException(
					"You must specify 1) the strength, 2) the new model file path, 3) the old test suite file path and 4) the file path for the test suite export file");
		}

		// Get current time (required for calculations when verb==true)
		long start = System.currentTimeMillis();

		// Read the combinatorial model
		TestModel m = Operations.readFile(evolvedModelPath); // TestModel = model with constraints

		// Set the strength (default was 0)
		m.setStrength(strength);

		// Get the MDD representing the model without constraints
		ModelToMDDConverter mc = new ModelToMDDConverter(model);
		MDDManager manager = mc.getMDD();
		int baseMDD = mc.getStartingNode();

		// Adding the constraints to the baseNode (baseMDD)
		baseMDD = Operations.updateMDDWithConstraints(manager, model, baseMDD);

		/* pMEDICIplusMT algorithm */
		
		long fillingTime = System.currentTimeMillis();
		
		// Importing the old test suite
		Vector<Map<String, String>> oldTests = CSVImporter.read(oldTestSuiteFilePath);

		// Creating the list of Text Context (partial test cases)
		// This is eventually filled at first by the comparision of the evolved model
		// with the old test suite.
		Vector<TestContext> tcList = new Vector<TestContext>();

		// Semaphore for managing concurrency during the comparision with the old test
		// suite
		ExtendedSemaphore oldTestsMutex = new ExtendedSemaphore();
		ExtendedSemaphore tcListMutex = new ExtendedSemaphore();
	
//		int nThreads = Runtime.getRuntime().availableProcessors();
		int nThreads = 2;
		threadsNum = nThreads;
		ArrayList<Thread> testEarlyFillerThreads = new ArrayList<Thread>();
		for (int i = 0; i < nThreads; i++) {
			Thread testEarlyFiller = new Thread(
					new TestEarlyFiller(oldTests, oldTestsMutex, tcList, tcListMutex, model, m, manager, baseMDD));
			testEarlyFillerThreads.add(testEarlyFiller);
			testEarlyFiller.start();
		}

		// Join all the test filler threads
		for (int i = 0; i < nThreads; i++) {
			testEarlyFillerThreads.get(i).join();
		}
		
		timeForOldTSFilling = System.currentTimeMillis()-fillingTime;

		if (PRINT_DEBUG) {
			System.out.println("----- INITIAL TEST SUITE (tcList before pMedici normal algorithm execution) -----");
			ArrayList<String> testCases = new ArrayList<String>();
			for (TestContext tc : tcList) {
				testCases.add(tc.getTest(false));
			}
			Operations.translateOutput(testCases, model);
			System.out.println();
		}

		// saving the number of test cases from the previous test suite
		// that has been kept (only for experimental purposes)
		tcListInitialSize = tcList.size();

		// Now tcList may contain some initial partial test cases
		// taken from the old test suite. The normal pMedici algorithm
		// is now applied starting using this tcList.

		/* pMEDICI algorithm */

		int nCovered = 0;
		int totTuples = 0;

		// Shared object between producer and consumer
		SafeQueue tuples = new SafeQueue();

		// Combination generator
		Iterator<List<Pair<Integer, Integer>>> tg = TupleGenerator.getAllKWiseCombination(m);

		// Start the filler thread
		TupleFiller tFiller = new TupleFiller(tg, tuples);
		Thread tFillerThread = new Thread(tFiller);
		tFillerThread.start();

		// Start all the TestBuilder threads
		nThreads = Runtime.getRuntime().availableProcessors();
		ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
		boolean sort = false;
		ArrayList<Thread> testBuilderThreads = new ArrayList<Thread>();
		for (int i = 0; i < nThreads; i++) {
			Thread tBuilder = new Thread(new TestBuilder(baseMDD, tuples, tcList, sort, m.getnParams(),
					m.getUseConstraints(), manager, testContextsMutex, PRINT_DEBUG, false));
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
		String testSuite = Operations.translateOutputToString(testCases, model);
		testSuiteSize = (testSuite.split("\n").length - 1);

		// Deleting eventually duplicated tests
		String reducedTestSuite = Operations.deleteDuplicates(testSuite);
		reducedTestSuiteSize = (reducedTestSuite.split("\n").length - 1);

		if (PRINT_DEBUG) {
			System.out.println("----- FINAL TEST SUITE -----");
			System.out.print(testSuite);
			System.out.println("SIZE: " + (testSuite.split("\n").length - 1));
			System.out.println();

			System.out.println("----- FINAL TEST SUITE REDUCED -----");
			System.out.print(reducedTestSuite);
			System.out.println("SIZE: " + (reducedTestSuite.split("\n").length - 1));
			System.out.println();
		}

		if (verb) {
			totTuples = tuples.getNTuples();
			System.out.println("Covered: " + nCovered + " tuples");
			System.out.println("Uncovered: " + (totTuples - nCovered) + " tuples");
			System.out.println("Total number of tuples: " + totTuples + " tuples");
			System.out.println(
					"Time required for test suite generation: " + (System.currentTimeMillis() - start) + " ms");
			System.out.println("Generated " + tcList.size() + " tests");
		}

		// Join the tuple filler thread
		tFillerThread.join();

		// Export the test suite
		// ** To be commented during experiments because  because we don't want to include this time in the data
		// sgeneration (just like in the classic pMEDICI algorithm) **
		pMedici.exporter.CSVExporter.export(reducedTestSuite, exportFilePath);

	}

}
