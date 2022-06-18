package pMedici.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.PathSearcher;
import org.colomoto.mddlib.operators.MDDBaseOperators;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.generator.util.ParameterSize;
import ctwedge.generator.util.Utility;
import ctwedge.util.ParameterValuesToInt;
import pMedici.safeelements.ExtendedSemaphore;
import pMedici.safeelements.SafeQueue;
import pMedici.safeelements.TestContext;
import pMedici.threads.TestBuilder;
import pMedici.threads.TestEarlyFiller;
import pMedici.threads.TestImporter;
import pMedici.threads.TupleFiller;
import pMedici.util.ModelToMDDConverter;
import pMedici.util.Operations;
import pMedici.util.Pair;
import pMedici.util.TestModel;
import pMedici.util.TupleConverter;
import pMedici.combinations.TupleGenerator;
import pMedici.importer.CSVImporter;

public class PMediciPlusMT {
	
	public static boolean PRINT_DEBUG = true;

	public static void main(String[] args) throws IOException, InterruptedException {

		String evolvedModelPath="";
		String oldTestSuiteFilePath="";
		
		int strength = 2;
		CitModel model = null;
		boolean verb = false;
		
		 // 1)
		 // Read the test model from arguments
		 // args[0] = t-wise strength
		 // args[1] = file name (path) to CTWedge evolved model
		 // args[2] = file name (path) to the test suite (.csv) of the old model
		 // args[3] = boolean true/false for verb
		if (args.length >= 3) {
			strength = Integer.parseInt(args[0]);
			evolvedModelPath = args[1];
			oldTestSuiteFilePath = args[2];
			if (args.length > 3)
				verb = Boolean.parseBoolean(args[3]);
		} else {
			throw new RuntimeException("You must specify the strength, the new model file path and the old test suite file path for generating a test suite");
		}
		
		// Importing the tests
		Vector<Map<String, String>> oldTests = CSVImporter.read(oldTestSuiteFilePath);
		
		// Get current time
				long start = System.currentTimeMillis();
				
				
		// Converting the oldTest vector in a safe queue
		ExtendedSemaphore oldTestsMutex = new ExtendedSemaphore();
		ConcurrentLinkedQueue<Map<String, String>> sharedOldTestsQueue = new ConcurrentLinkedQueue<Map<String, String>>();
		
		int nThreads = Runtime.getRuntime().availableProcessors();
		ArrayList<Thread> testImporterThreads = new ArrayList<Thread>();
		for (int i=0; i<nThreads; i++) {
			Thread TestImporter = new Thread(new TestImporter(sharedOldTestsQueue, oldTestsMutex, oldTests));
			testImporterThreads.add(TestImporter);
			TestImporter.start();
		}
		
		// Join all the test importer threads
		for (int i=0; i<nThreads; i++) {
			testImporterThreads.get(i).join();

		}
				
//		// Printing the queue
//		for(Map<String, String> oldTest : sharedOldTestsQueue) { 
//			oldTest.forEach((name, value) -> {
//				System.out.print(name + ":" + value + ", ");
//			}); System.out.println(); } 
//			}
		
		
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

		// Creating the list of Text Context (partial test cases)
		// This is eventually filled by the comparision of the evolved model
		// with the old test suite. Then it is completed by the normal medici algorithm.
		Vector<TestContext> tcList = new Vector<TestContext>();
		
		
		nThreads = Runtime.getRuntime().availableProcessors();
		ArrayList<Thread> testEarlyFillerThreads = new ArrayList<Thread>();
		for (int i=0; i<nThreads; i++) {
			Thread testEarlyFiller = new Thread(new TestEarlyFiller(sharedOldTestsQueue, oldTestsMutex, tcList, model, m, manager, baseMDD));
			testEarlyFillerThreads.add(testEarlyFiller);
			testEarlyFiller.start();
		}
		
		// Join all the test importer threads
		for (int i=0; i<nThreads; i++) {
			testEarlyFillerThreads.get(i).join();
		}
		
		// Save the tests
		ArrayList<String> testCases = new ArrayList<String>();
		for (TestContext tc : tcList) {
	//		nCovered += tc.getNCovered();
			testCases.add(tc.getTest(false));
		}
				
		// Print test suite
		System.out.println("-----TEST SUITE-----");
		Operations.translateOutput(testCases, model);
		

		System.out.println("Time required for test suite generation: " + (System.currentTimeMillis() - start) + " ms");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		int nCovered = 0;
//		int totTuples = 0;
//
//		TestContext tc = new TestContext(baseMDD, m.getnParams(), m.getUseConstraints(), manager);
//
//		for (Parameter param : model.getParameters())
//			System.out.println(param.getName());
//
//		Vector<Pair<Integer, Integer>> tuple = new Vector<Pair<Integer, Integer>>();
//		tuple.add(new Pair<Integer, Integer>(0, 1)); // Boeing = true
//		tuple.add(new Pair<Integer, Integer>(1, 1)); // SeatsConfiguration = true
//		tuple.add(new Pair<Integer, Integer>(2, 1)); // Rows3Passengres210 = true
//		tuple.add(new Pair<Integer, Integer>(3, 1)); // FuelCapacity = true
//		tuple.add(new Pair<Integer, Integer>(4, 1)); // Liters126920 = true
//
//		System.out.println(tc.verifyWithMDD(tuple)); // dovrebbe essere true, ma stampa false
//
//		ParameterValuesToInt valToInt = new ParameterValuesToInt(model);
//		System.out.println(valToInt.convertInt(1).getFirst().getName());

//	System.out.println( valToInt.get("Boeing"));
//	System.out.println( valToInt.get("SeatsConfiguration"));
//	System.out.println( valToInt.get("Rows3Passengers210"));
//	System.out.println( valToInt.get("FuelCapacity"));
//	System.out.println( valToInt.get("Liters126920"));

//	Vector<TestContext> tcList = new Vector<TestContext>();
//		tc.addTuple(tuple);
//	
//		ArrayList<String> testCases = new ArrayList<String>();
//		testCases.add(tc.getTest(false));
//	
//	System.out.println("-----TEST SUITE-----");
//		Operations.translateOutput(testCases, model);

//	// ritorna il numero di valori che può assumere ciascun parametro
//	// essendo booleani: stampa per tutte i parametri 5
//	for (Parameter param : model.getParameters())
//		System.out.println(ParameterSize.eInstance.doSwitch(param));
//	
//	// stampa il numero totale di parametri
//	System.out.println(model.getParameters().size());

		// CONVERSIONI: da numero a valore
//	ParameterValuesToInt valToInt = new ParameterValuesToInt(model);
//	int val=5;
//	// numero val -> nome del parametro
//	csv_out += valToInt.convertInt(val).getFirst().getName() + ";";
//	// numero val -> valore associato a val 
//	csv_out += valToInt.convertInt(val).getSecond() + ";";
//	// nome parametro -> primo numero val associato ad esso
//	csv_out += valToInt.get("Rows3Passengers210")+" ";
//	csv_out += valToInt.get("Liters126920");
//	System.out.println(csv_out);

		// verifyWithMDD(Vector<Pair<Integer, Integer>> tuple)

//			// Create an MDD representing the tuple
//			TupleConverter tc = new TupleConverter(manager);
//			int tupleMdd = tc.getMDDFromTuple(tuple);
//			// Try computing the intersection (AND) 
//			ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
//			int intersection = MDDBaseOperators.AND.combine(manager, this.mdd, tupleMdd);
//			ExtendedSemaphore.OPERATION_SEMAPHORE.release();
//			// If the intersection is empty, then the tuple is not compatible, otherwise it is
//			PathSearcher searcher = new PathSearcher(manager, 1);
//			searcher.setNode(intersection);
//			int nPaths = searcher.countPaths();
//			return nPaths > 0;		

//		System.out.println();

	}

}
