package pMedici.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.colomoto.mddlib.MDDManager;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.ext.Utility;
import pMedici.safeelements.SafeQueue;
import pMedici.threads.TestBuilder;
import pMedici.threads.TupleFiller;
import pMedici.util.ExtendedSemaphore;
import pMedici.util.ModelToMDDConverter;
import pMedici.util.Operations;
import pMedici.util.Pair;
import pMedici.util.TestContext;
import pMedici.util.TupleGenerator;

public class PMedici_withTestSuiteExport {

	public static boolean PRINT_DEBUG = false;

	public static void main(String[] args) throws IOException, InterruptedException {
		String fileName = "";
		String exportFilePath="";
		int strength = 2;
		CitModel model = null;
		boolean verb = false;

		// Read the test model from arguments
		// args[0] = t-wise strength
		// args[1] = file name (path) to the input CTWedge model
		// args[2] =  the path to the file where the test suite has to be exported
		// args[3] = boolean true/false for verb
		if (args.length >= 3) {
			strength = Integer.parseInt(args[0]);
			fileName = args[1];
			exportFilePath = args[2];
			if (args.length > 3)
				verb = Boolean.parseBoolean(args[2]);
		} else {
			throw new RuntimeException(
					"You must specify the strength and the model file name for generating a test suite");
		}

		// Read the combinatorial model
		if (!fileName.equals("")) {
			model = Utility.loadModelFromPath(fileName);
		}

		ModelToMDDConverter mc = new ModelToMDDConverter(model);
		MDDManager manager = mc.getMDD();
		int baseMDD = mc.getStartingNode();
		int nCovered = 0;
		int totTuples = 0;

		// Get current time
		long start = System.currentTimeMillis();

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

		// Start all the TestBuilder threads
		int nThreads = Runtime.getRuntime().availableProcessors();
		ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
		Vector<TestContext> tcList = new Vector<TestContext>();
		boolean sort = false;
		ArrayList<Thread> testBuilderThreads = new ArrayList<Thread>();
		for (int i = 0; i < nThreads; i++) {
			Thread tBuilder = new Thread(new TestBuilder(baseMDD, tuples, tcList, sort, model.getParameters().size(),
					model.getConstraints().size()>0, manager, testContextsMutex, verb, false));
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

		// Deleting eventually duplicated tests
		String reducedTestSuite = Operations.deleteDuplicates(testSuite);

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
		pMedici.exporter.CSVExporter.export(reducedTestSuite, exportFilePath);
	}

}