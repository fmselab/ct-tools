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

public class PMedici {
	
	public static boolean PRINT_DEBUG = false;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String fileName = "";
		int strength = 2;
		CitModel model = null;
		boolean verb = false;
		
		// 1)
		// Read the test model from arguments
		// args[0] = t-wise strength
		// args[1] = file name (path) to CTWedge model
		// args[2] = boolean true/false for verb
		if (args.length >= 2) {
			strength = Integer.parseInt(args[0]);
			fileName = args[1];
			if (args.length > 2)
				verb = Boolean.parseBoolean(args[2]);
		} else {
			throw new RuntimeException("You must specify the strength and the model file name for generating a test suite");
		}
		
		// 2)
		// Convert the model from CTWedge to Medici format
		// and write the converted Medici format in "model.txt" file
		if (!fileName.equals("")) {
			model = Utility.loadModelFromPath(fileName); // importing CTWedge model to "CitModel model"
			MediciCITGenerator gen = new MediciCITGenerator();
			MediciCITGenerator.OUTPUT_ON_STD_OUT_DURING_TRANSLATION = false;
			String mediciModel = gen.translateModel(model, false); // translating CIT model to medici model
			File modelFile = new File("model.txt");
			FileWriter wf = new FileWriter(modelFile);
			wf.write(mediciModel); // writing the translated medici model to "model.txt"
			wf.close();
			fileName = "model.txt"; // saving the name of the written model in the String variable "fileName"
		}
		
		// 3)
		// Read the combinatorial model		
		TestModel m = Operations.readFile(fileName); // TestModel = model with constraints
		
		// Set the strength
		// in the previous line m is created with strength=0
		// here we set the current desired value of strength
		m.setStrength(strength);
		
		
		// 4)
		// Get the MDD representing the model without constraints
		ModelToMDDConverter mc = new ModelToMDDConverter(m);
		MDDManager manager = mc.getMDD(); // MDD with no constraints
		int baseMDD = mc.getStartingNode(); // starting node of the mdd
		int nCovered = 0;
		int totTuples = 0;
		
		// Get current time
		long start = System.currentTimeMillis();
		
		// 5)
		// Add to the baseNode the constraints
		// notice: the constraints are added to the basenode (starting node)
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
		for (int i=0; i<nThreads; i++) {
			Thread tBuilder = new Thread(new TestBuilder(baseMDD, tuples, tcList, sort, m.getnParams(), m.getUseConstraints(), manager, testContextsMutex));
			testBuilderThreads.add(tBuilder);
			tBuilder.start();
		}
		
		// Join all the test builder threads
		for (int i=0; i<nThreads; i++) {
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
		Operations.translateOutput(testCases, model);
		
		if (verb) {
			totTuples = tuples.getNTuples();
			System.out.println("Covered: " + nCovered + " tuples");
			System.out.println("Uncovered: " + (totTuples - nCovered) + " tuples");
			System.out.println("Total number of tuples: " + totTuples + " tuples");
			System.out.println("Time required for test suite generation: " + (System.currentTimeMillis() - start) + " ms");
			System.out.println("Generated " + tcList.size() + " tests");
		}
		
		// Join the tuple filler thread
		tFillerThread.join();
	}
	
}
