package pMedici.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.colomoto.mddlib.MDDManager;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.generator.util.Utility;
import pMedici.safeelements.ExtendedSemaphore;
import pMedici.safeelements.SafeQueue;
import pMedici.safeelements.TestContext;
import pMedici.threads.TestBuilder;
import pMedici.threads.TupleFiller;
import pMedici.util.ModelToMDDConverter;
import pMedici.util.Operations;
import pMedici.util.Pair;
import pMedici.util.TestModel;
import pMedici.combinations.TupleGenerator;
import pMedici.experiments.pMEDICIExperimenter;

public class PMedici {

	public static boolean PRINT_DEBUG = false;

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

	public static void main(String[] args) throws IOException, InterruptedException {
		String fileName = "";
		int strength = 2;
		CitModel model = null;
		boolean verb = false;

		// Read the test model from arguments
		// args[0] = t-wise strength
		// args[1] = file name (path) to the CTWedge model
		// args[2] = boolean true/false for verb
		if (args.length >= 2) {
			strength = Integer.parseInt(args[0]);
			fileName = args[1];
			if (args.length > 2)
				verb = Boolean.parseBoolean(args[2]);
		} else {
			throw new RuntimeException(
					"You must specify the strength and the model file name for generating a test suite");
		}

		// Convert the model from CTWedge to Medici format
		if (!fileName.equals("")) {
			model = Utility.loadModelFromPath(fileName);
			MediciCITGenerator gen = new MediciCITGenerator();
			MediciCITGenerator.OUTPUT_ON_STD_OUT_DURING_TRANSLATION = false;
			String mediciModel = gen.translateModel(model, false);
			File modelFile = new File("model.txt");
			FileWriter wf = new FileWriter(modelFile);
			wf.write(mediciModel);
			wf.close();
			fileName = "model.txt";
		}

		// Read the combinatorial model and get the MDD representing the model without
		// constraints
		TestModel m = Operations.readFile(fileName);

		// Set the strength
		m.setStrength(strength);

		ModelToMDDConverter mc = new ModelToMDDConverter(m);
		MDDManager manager = mc.getMDD();
		int baseMDD = mc.getStartingNode();
		int nCovered = 0;
		int totTuples = 0;

		// Get current time
		long start = System.currentTimeMillis();

		// Add to the baseNode the constraints
		baseMDD = Operations.updateMDDWithConstraints(manager, m, baseMDD);

		// Shared object between producer and consumer
		SafeQueue tuples = new SafeQueue();

		// Combination generator
		Iterator<List<Pair<Integer, Integer>>> tg = TupleGenerator.getAllKWiseCombination(m);

		// Start the filler thread
		TupleFiller tFiller = new TupleFiller(tg, tuples);
		Thread tFillerThread = new Thread(tFiller);
		tFillerThread.start();

		// Start all the TestBuilder threads
		int nThreads = Runtime.getRuntime().availableProcessors();
		threadsNum = nThreads;
		ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
		Vector<TestContext> tcList = new Vector<TestContext>();
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
		System.out.println("-----TEST SUITE-----");
		// Operations.translateOutput(testCases, model);
		String testSuite = Operations.translateOutputToString(testCases, model);
		System.out.println(testSuite);
		testSuiteSize = (testSuite.split("\n").length - 1);
		
		// Deleting eventually duplicated tests
		String reducedTestSuite = Operations.deleteDuplicates(testSuite);
		reducedTestSuiteSize = (reducedTestSuite.split("\n").length - 1);

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
	}

}