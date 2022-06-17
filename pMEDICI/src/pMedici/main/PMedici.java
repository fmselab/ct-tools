package pMedici.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.colomoto.mddlib.MDDManager;

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
import pMedici.threads.TupleFiller;
import pMedici.util.ModelToMDDConverter;
import pMedici.util.Operations;
import pMedici.util.Pair;
import pMedici.util.TestModel;
import pMedici.combinations.TupleGenerator;

public class PMedici {
	
	public static boolean PRINT_DEBUG = false;

/*	
	public static void main(String[] args)  throws IOException, InterruptedException {
		
		String fileName = "../pMEDICI/evolutionModels/Boeing/Boeingv1_ctwedge.ctw";
		int strength = 2;
		CitModel model = null;
		boolean verb = false;
			
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
		// Read the combinatorial model	(fileName in this case is
		// the "medici" model which has been creating from a conversion
		// from a CTWedge model)
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
		
		// 6) Creating a safe queue (for concurrency) in order to manage the tuples
		// SHARED OBJECT between producer and consumer
		SafeQueue tuples = new SafeQueue();

		// 7) Creating the tuple generator tg
		// Combination generator
		Iterator<List<Pair<Integer, Integer>>> tg = TupleGenerator.getAllKWiseCombination(m);

		// 8) Create the filler thread with tuples queue and tg
		//	  then ".start()" the filler thread (call the method "run()")
		//	  -> tuples queue is continuously filled until all
		//    the tuples are inserted (stopping and restarting
		//	  every time the tuples queue is full)
		TupleFiller tFiller = new TupleFiller(tg, tuples);
		Thread tFillerThread = new Thread(tFiller);
		tFillerThread.start();
		
		// 9) 
		// Start all the TestBuilder threads:
		// Here we are asking for the available number of logical cores on
		// the current machine. We are creating a number of thread which
		// is the maximum number that can be created within the used calculator
		int nThreads = Runtime.getRuntime().availableProcessors();
		ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
		Vector<TestContext> tcList = new Vector<TestContext>();
		boolean sort = false;
		ArrayList<Thread> testBuilderThreads = new ArrayList<Thread>();
		for (int i=0; i<nThreads; i++) {
			// at every tBuilder I pass as parameter:
			// - baseMDD = base MTS of the model with constraints
			// - tuples = the shared object which has the tuples to be consumed
			// - tcList = the list where the tc have to be added
			// - sort = boolean that ask wheter to use the sorting optimization or not
			// - manager = the object of MDD library that deals with MDD operations
			// - testContextsMutex = the semaphore for managing the access to tcList object
			Thread tBuilder = new Thread(new TestBuilder(baseMDD, tuples, tcList, sort, m.getnParams(), m.getUseConstraints(), manager, testContextsMutex));
			testBuilderThreads.add(tBuilder);
			tBuilder.start();
			// testBuilder = the thread that consumes the tuples from the shared object
			// and searches the best context where to drop it in "tcList"
		}
		
		// 10)
		// Join all the test builder threads
		for (int i=0; i<nThreads; i++) {
			testBuilderThreads.get(i).join();
		}
		
		// 11)
		// Save the tests
		ArrayList<String> testCases = new ArrayList<String>();
		for (TestContext tc : tcList) {
			nCovered += tc.getNCovered();
			testCases.add(tc.getTest(false));
		}
		
		
		// First row -> parameter names
		int[] sizes = new int[model.getParameters().size()];
		int count=0;
		String csv_out = "";
		for (Parameter param : model.getParameters()) {
			sizes[count] = ParameterSize.eInstance.doSwitch(param); // i valori passano da "0 (default)" a 2 qui
			
			System.out.println("sizes["+count+"]: "+sizes[count]); // tutti i valori hanno size=2
			System.out.println("param.getName(): "+param.getName()); //stampa il nome del parametro
			System.out.println("\n");
			
			csv_out += param.getName() + ";"; // aggiungo il nome del parametro a csv_out
			
			count++;
				}
		
		System.out.println(csv_out);
		csv_out = csv_out.substring(0, csv_out.length() - 1); // con questo tolgo l'ultimo ";" in eccesso
		System.out.println(csv_out); // ora in csv_out ho la lista di tutti i parametri del modello
		csv_out += "\n";
	
		ParameterValuesToInt valToInt = new ParameterValuesToInt(model);
		for (String s : testCases) {
			String[] values = s.split(" ");
			for (int i = 0; i < values.length; i++) {
				
				int previousCount = 0;
				int val = 0;
				for (int j = 0; j < i; j++) {
					previousCount += sizes[j];
				}
				if (Integer.parseInt(values[i]) == -1) {
					val = randInt(previousCount, previousCount + sizes[i] - 1);
				
				} else {
					val = previousCount + Integer.parseInt(values[i]);
				
				}
				System.out.println(val); // mi aspetto 1,3,5,7,9
				// 2) Questo permette, da un valore intero, di ottenere
				//    il valore della stringa ad esso corrispondente e lo
				//    aggiunge a csv_out
				csv_out += valToInt.convertInt(val).getSecond() + ";";
			}
			csv_out = csv_out.substring(0, csv_out.length() - 1);
			csv_out += "\n";
		}
		
		System.out.println(csv_out);
		

		// 12)
		// Print test suite
		System.out.println("-----TEST SUITE-----");
		Operations.translateOutput(testCases, model);
		
		
	}
	
	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
	
*/
	
	
	/*Note generali:
	  - quando costruisco un modello, possiamo richiedere la LISTA di parametri del
	    modello, la quale è di tipo EList<Parameter> (con model.getParameters())
	   
	*/
	
	/*
	public static void main(String[] args) {
		String fileName = "../pMEDICI/evolutionModels/Boeing/Boeingv1_ctwedge.ctw";
		int strength = 2;
		CitModel model = null;
		model = Utility.loadModelFromPath(fileName);
		System.out.println(model.toString());
		
		
//		for(int i=0; i<sizes.length; i++) {
//			// sizes=0=booleans
//			System.out.println("sizes["+i+"]: "+sizes[i]); // stampa tutti 0
//		}

		// First row -> parameter names
		// con questo creo un array sizes che contiene la dimensione (in termini
		// di "quanti valori può assumere") di ogni parametro 
		int[] sizes = new int[model.getParameters().size()];
		int count=0;
		String csv_out = "";
		for (Parameter param : model.getParameters()) {
			// chiama il singleton object di ParameterSize "eInstance" e con questa
			// mette il numero di valori che può assumere ciascun parametro all'interno
			// del campo sizes -> essendo booleani ci aspettiamo di avere 2 ovunque
			sizes[count] = ParameterSize.eInstance.doSwitch(param); // i valori passano da 0 a 1 qui
			
			System.out.println("sizes["+count+"]: "+sizes[count]); // tutti i valori hanno size=2
			System.out.println("param.getName(): "+param.getName()); //stampa il nome del parametro
			System.out.println("\n");
			
			csv_out += param.getName() + ";"; // aggiungo il nome del parametro a csv_out
			
			count++;
				}
		
		System.out.println(csv_out);
		csv_out = csv_out.substring(0, csv_out.length() - 1); // con questo tolgo l'ultimo ";" in eccesso
		System.out.println(csv_out); // ora in csv_out ho la lista di tutti i parametri del modello
		csv_out += "\n";
	
		
		// CONVERSIONI: da numero a valore
		ParameterValuesToInt valToInt = new ParameterValuesToInt(model);
		int val=5;
		// numero val -> nome del parametro
		csv_out += valToInt.convertInt(val).getFirst().getName() + ";";
		// numero val -> valore associato a val 
		csv_out += valToInt.convertInt(val).getSecond() + ";";
		// nome parametro -> primo numero val associato ad esso
		csv_out += valToInt.get("Rows3Passengers210")+" ";
		csv_out += valToInt.get("Liters126920");
		System.out.println(csv_out);
			
	}
	
	*/
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String fileName = "../pMEDICI/evolutionModels/Boeing/Boeingv1_ctwedge.ctw";
		int strength = 2;
		CitModel model = null;
		boolean verb = false;
		
		// 1)
		// Read the test model from arguments
		// args[0] = t-wise strength
		// args[1] = file name (path) to CTWedge model
		// args[2] = boolean true/false for verb
//		if (args.length >= 2) {
//			strength = Integer.parseInt(args[0]);
//			fileName = args[1];
//			if (args.length > 2)
//				verb = Boolean.parseBoolean(args[2]);
//		} else {
//			throw new RuntimeException("You must specify the strength and the model file name for generating a test suite");
//		}
		
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
		// Read the combinatorial model	(fileName in this case is
		// the "medici" model which has been creating from a conversion
		// from a CTWedge model)
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
		
		// 6) Creating a safe queue (for concurrency) in order to manage the tuples
		// SHARED OBJECT between producer and consumer
		SafeQueue tuples = new SafeQueue();

		// 7) Creating the tuple generator tg
		// Combination generator
		Iterator<List<Pair<Integer, Integer>>> tg = TupleGenerator.getAllKWiseCombination(m);

		// 8) Create the filler thread with tuples queue and tg
		//	  then ".start()" the filler thread (call the method "run()")
		//	  -> tuples queue is continuously filled until all
		//    the tuples are inserted (stopping and restarting
		//	  every time the tuples queue is full)
		TupleFiller tFiller = new TupleFiller(tg, tuples);
		Thread tFillerThread = new Thread(tFiller);
		tFillerThread.start();
		
		// 9) 
		// Start all the TestBuilder threads:
		// Here we are asking for the available number of logical cores on
		// the current machine. We are creating a number of thread which
		// is the maximum number that can be created within the used calculator
		int nThreads = Runtime.getRuntime().availableProcessors();
		ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
		Vector<TestContext> tcList = new Vector<TestContext>();
		boolean sort = false;
		ArrayList<Thread> testBuilderThreads = new ArrayList<Thread>();
		for (int i=0; i<nThreads; i++) {
			// at every tBuilder I pass as parameter:
			// - baseMDD = base MTS of the model with constraints
			// - tuples = the shared object which has the tuples to be consumed
			// - tcList = the list where the tc have to be added
			// - sort = boolean that ask wheter to use the sorting optimization or not
			// - manager = the object of MDD library that deals with MDD operations
			// - testContextsMutex = the semaphore for managing the access to tcList object
			Thread tBuilder = new Thread(new TestBuilder(baseMDD, tuples, tcList, sort, m.getnParams(), m.getUseConstraints(), manager, testContextsMutex));
			testBuilderThreads.add(tBuilder);
			tBuilder.start();
			// testBuilder = the thread that consumes the tuples from the shared object
			// and searches the best context where to drop it in "tcList"
		}
		
		// 10)
		// Join all the test builder threads
		for (int i=0; i<nThreads; i++) {
			testBuilderThreads.get(i).join();
		}
		
		// 11)
		// Save the tests
		ArrayList<String> testCases = new ArrayList<String>();
		for (TestContext tc : tcList) {
			nCovered += tc.getNCovered();
			testCases.add(tc.getTest(false));
		}
		
		// 12)
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
