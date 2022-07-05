package pMedici.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.colomoto.mddlib.MDDManager;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.generator.util.Utility;
import ctwedge.util.ModelUtils;
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

public class PMedici {

	public static boolean PRINT_DEBUG = false;

	// argument 1 : strength
	// argument 2 : model
	public static void main(String[] args) throws IOException, InterruptedException {
		String fileName = "";
		int strength = 2;
		boolean verb = false;

		// Read the test model from arguments
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
		CitModel model = Utility.loadModelFromPath(fileName);
		executePMedici(model, strength, verb);
	}

	/**
	 * 
	 * @param model
	 * @param strength
	 * @param verb
	 * @return the test suite as list of strings
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static List<String> executePMedici(CitModel model, int strength, boolean verb)
			throws IOException, InterruptedException {
		// convert to medici
		MediciCITGenerator gen = new MediciCITGenerator();
		MediciCITGenerator.OUTPUT_ON_STD_OUT_DURING_TRANSLATION = false;
		String mediciModel = gen.translateModel(model, false);
		// TODO, if one wantd to save medici to file
//			File modelFile = new File("model.txt");
//			FileWriter wf = new FileWriter(modelFile);
//			wf.write(mediciModel);
//			wf.close();
//			fileName = "model.txt";				
		// Read the combinatorial model and get the MDD representing the model without
		// constraints
		TestModel m = Operations.readModelFromReader(new BufferedReader(new StringReader(mediciModel)));

		// Set the strength
		m.setStrength(strength);

		ModelToMDDConverter mc = new ModelToMDDConverter(m);
		MDDManager manager = mc.getMDD();
		int baseMDD = mc.getStartingNode();

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
		int nCovered = 0;
		int totTuples = 0;
		ArrayList<String> testCases = new ArrayList<String>();
		for (TestContext tc : tcList) {
			nCovered += tc.getNCovered();
			testCases.add(tc.getTest(false));
		}
		List<String> csv_out = Operations.translateOutput(testCases, model);
		if (verb) {
			// Print test suite
			System.out.println("-----TEST SUITE-----");
			// Print the results
			//csv_out.stream().distinct().forEach(x -> System.out.println(x));
			csv_out.stream().forEach(x -> System.out.println(x));
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
		return csv_out;
	}

}