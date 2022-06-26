package pMedici.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.colomoto.mddlib.MDDManager;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.generator.util.Utility;
import pMedici.safeelements.ExtendedSemaphore;
import pMedici.safeelements.SafeQueue;
import pMedici.safeelements.TestContext;
import pMedici.threads.TestBuilder;
import pMedici.threads.TestEarlyFiller;
import pMedici.threads.TupleFiller;
import pMedici.util.ModelToMDDConverter;
import pMedici.util.Operations;
import pMedici.util.Pair;
import pMedici.util.TestModel;
import pMedici.combinations.TupleGenerator;
import pMedici.importer.CSVImporter;

/**
 * Multithread version of pMEDICI+
 */
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
			oldTestSuiteFilePath = args[2];
			exportFilePath = args[3];
			if (args.length > 4)
				verb = Boolean.parseBoolean(args[4]);
		} else {
			throw new RuntimeException(
					"You must specify 1) the strength, 2) the new model file path, 3) the old test suite file path and 4) the file path for the test suite export file");
		}

		// Get current time
		long start = System.currentTimeMillis();

		// Convert the CTWedge model to Medici format (exported in "model.txt" file)
		if (!evolvedModelPath.equals("")) {
			model = Utility.loadModelFromPath(evolvedModelPath);
			MediciCITGenerator gen = new MediciCITGenerator();
			MediciCITGenerator.OUTPUT_ON_STD_OUT_DURING_TRANSLATION = false;
			String mediciModel = gen.translateModel(model, false);
			File modelFile = new File("model.txt");
			FileWriter wf = new FileWriter(modelFile);
			wf.write(mediciModel);
			wf.close();
			evolvedModelPath = "model.txt";
		}

		// Read the combinatorial model (from the exported medici "model.txt")
		TestModel m = Operations.readFile(evolvedModelPath); // TestModel = model with constraints

		// Set the strength (default was 0)
		m.setStrength(strength);

		// Get the MDD representing the model without constraints
		ModelToMDDConverter mc = new ModelToMDDConverter(m);
		MDDManager manager = mc.getMDD();
		int baseMDD = mc.getStartingNode();

		// Adding the constraints to the baseNode (baseMDD)
		baseMDD = Operations.updateMDDWithConstraints(manager, m, baseMDD);

		/* pMEDICIplusMT algorithm */

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

		int nThreads = Runtime.getRuntime().availableProcessors();
		nThreads = Runtime.getRuntime().availableProcessors();
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

		if (PRINT_DEBUG) {
			System.out.println("----- INITIAL TEST SUITE (tcList before pMedici normal algorithm execution) -----");
			ArrayList<String> testCases = new ArrayList<String>();
			for (TestContext tc : tcList) {
				testCases.add(tc.getTest(false));
			}
			Operations.translateOutput(testCases, model);
			System.out.println();
		}

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
		threadsNum = nThreads;
		ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
		boolean sort = false;
		ArrayList<Thread> testBuilderThreads = new ArrayList<Thread>();
		for (int i = 0; i < nThreads; i++) {
			Thread tBuilder = new Thread(new TestBuilder(baseMDD, tuples, tcList, sort, m.getnParams(),
					m.getUseConstraints(), manager, testContextsMutex));
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
		reducedTestSuiteSize = (reducedTestSuite.split("\n").length - 1);;

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
		// ** Commented because we don't want to include this time in the data generation **
		// pMedici.exporter.CSVExporter.export(reducedTestSuite, exportFilePath);

	}

}
