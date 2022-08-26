package pMedici.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.colomoto.mddlib.MDDManager;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.generator.util.Utility;
import pMedici.combinations.TupleGenerator;
import pMedici.importer.CSVImporter;
import pMedici.safeelements.ExtendedSemaphore;
import pMedici.safeelements.SafeQueue;
import pMedici.safeelements.TestContext;
import pMedici.threads.TestBuilder;
import pMedici.threads.TupleFiller;
import pMedici.util.ModelToMDDConverter;
import pMedici.util.Operations;
import pMedici.util.Pair;
import pMedici.util.TestModel;

public class PMediciPlus {

	static final Logger LOGGER = Logger.getLogger(PMediciPlus.class);
	
	public static boolean PRINT_DEBUG = true;

	static boolean verb = false;

	
	public static void main(String[] args) throws IOException, InterruptedException {

		String evolvedModelPath = "";
		String oldTestSuiteFilePath = "";
		String exportFilePath = "";

		int strength = 2;
		CitModel model = null;
		
		// Read the test model from arguments
		// args[0] = t-wise strength
		// args[1] = file name (path) to CTWedge evolved model
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


		Vector<Map<String, String>> oldTests = CSVImporter.read(oldTestSuiteFilePath);

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

		String testSuite = generateTests(model, m, oldTests);

		// Export the test suite
		pMedici.exporter.CSVExporter.export(testSuite, exportFilePath);

	}

	/**
	 * 
	 * @param model the new citmodel 
	 * @param m the new model as test model TODO delete (only 1 model)
	 * @param oldTests the old tests
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static String generateTests(CitModel model, TestModel m, Vector<Map<String, String>> oldTests) throws InterruptedException, IOException {
		// Get current time
		long start = System.currentTimeMillis();
		// Get the MDD representing the model without constraints
		ModelToMDDConverter mc = new ModelToMDDConverter(m);
		MDDManager manager = mc.getMDD();
		int baseMDD = mc.getStartingNode();

		// Adding the constraints to the baseNode (baseMDD)
		baseMDD = Operations.updateMDDWithConstraints(manager, m, baseMDD);

		/* pMEDICIplus algorithm */

		// Creating the list of Text Context (partial test cases)
		// This is eventually filled by the comparision of the evolved model
		// with the old test suite. Then it is completed by the normal medici algorithm.
		Vector<TestContext> tcList = new Vector<TestContext>();

		// For each for each test case of the old test suite, we check if for each
		// parameter
		// of the evolved model (following the order used in the CitModel which is also
		// the
		// order used in the tuple by Medici) the value of the current iteration
		// parameter
		// is present or not in the old test suite. If it is, then we add the parameter
		// value in the tuple of the current iteration.
		for (Map<String, String> oldTest : oldTests) {
			LOGGER.debug("adding test " + oldTest);
			// Creating the tuple related to the current iteration
			Vector<Pair<Integer, Integer>> tuple = new Vector<Pair<Integer, Integer>>();
			int tupleIndex = 0;

			for (Parameter param : model.getParameters()) {

				// if the parameter of the new model is in the old test suite,
				// its value is added in the corresponding position in the current tuple
				String testParamValue;
				if ((testParamValue = oldTest.get(param.getName())) != null) {
					// since we imposed that values must be all boolean, we have only 0="false" or
					// 1="true"
					tuple.add(new Pair<Integer, Integer>(tupleIndex, testParamValue.equals("true") ? 1 : 0));
				}

				tupleIndex++;
			}

			// If we added at least one parameter test value to the tuple, then
			// we check if the created tuple is valid with the model constraints
			if (!tuple.isEmpty()) {
				TestContext tc = new TestContext(baseMDD, m.getnParams(), m.getUseConstraints(), manager);

				// If the tuple is compatible with the constraints, then we add the
				// add the tuple to the current test context and then we add the
				// current test context to the list of all the test context from
				// which the algorithm of medici will start executing

				if (tc.verifyWithMDD(tuple)) {

					// Adding the tuple to the current test context
					// Notice: this method also update the mdd of the text context
					tc.addTuple(tuple);

					// Adding the tc to the list of all the test context tcList
					tcList.add(tc);
				} 
			}

		}

		System.out
				.println("Time required for *pMediciPLUS* algorithm: " + (System.currentTimeMillis() - start) + " ms");

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
		int nThreads = Runtime.getRuntime().availableProcessors();
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
		
		// Deleting eventually duplicated tests
		String reducedTestSuite = Operations.deleteDuplicates(testSuite);
		
		if (PRINT_DEBUG) {
			System.out.println("----- FINAL TEST SUITE -----");
			System.out.print(testSuite);
			System.out.println("SIZE: "+ (testSuite.split("\n").length-1) );
			System.out.println();
			
			System.out.println("----- FINAL TEST SUITE REDUCED -----");
			System.out.print(reducedTestSuite);
			System.out.println("SIZE: "+ (reducedTestSuite.split("\n").length-1) );
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

		System.out.println("Time required for the whole algorithm: " + (System.currentTimeMillis() - start) + " ms");


		return testSuite;
	}
}
