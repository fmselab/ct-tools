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
import pMedici.threads.TupleFiller;
import pMedici.util.ModelToMDDConverter;
import pMedici.util.Operations;
import pMedici.util.Pair;
import pMedici.util.TestModel;
import pMedici.util.TupleConverter;
import pMedici.combinations.TupleGenerator;

public class PMediciPlusCarBody {
	
	public static void main(String[] args) throws IOException, InterruptedException {

	// Reading the CTWedge model
	String fileName = "../pMEDICI/evolutionModels/CarBody/CarBodyv1_ctwedge.ctw";
	int strength = 2;
	CitModel model = null;
	boolean verb = false;

	// Convert the CTWedge model to Medici format
	// and write the converted Medici format in temp "model.txt" file
	if(!fileName.equals("")) {
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
	
	// Read the combinatorial model	(fileName in this case is
	// the medici "model.txt" which has been creating from a conversion
	// from a CTWedge model)
	TestModel m = Operations.readFile(fileName); // TestModel = model with constraints
	
	// Set the strength
	// in the previous line m is created with strength=0
	// here we set the current desired value of strength
	m.setStrength(strength);
	
	// Get the MDD representing the model without constraints
	ModelToMDDConverter mc = new ModelToMDDConverter(m);
	MDDManager manager = mc.getMDD(); // MDD with no constraints
	int baseMDD = mc.getStartingNode(); // starting node of the mdd
	int nCovered = 0;
	int totTuples = 0;
	// Add the constraints to the baseNode
	baseMDD = Operations.updateMDDWithConstraints(manager, m, baseMDD);
	
	TestContext tc = new TestContext(baseMDD, m.getnParams(), m.getUseConstraints(), manager);
	
	for (Parameter param : model.getParameters())
		System.out.println(param.getName());
	
	Vector<Pair<Integer, Integer>> tuple = new Vector<Pair<Integer,Integer>>();
	tuple.add(new Pair<Integer, Integer>(0, 1)); // CarBody = true
	tuple.add(new Pair<Integer, Integer>(1, 1)); // MultimediaDevices = true
	tuple.add(new Pair<Integer, Integer>(2, 0)); // Radio = false
	tuple.add(new Pair<Integer, Integer>(3, 0)); // MonochromeRadioDisplay = false
	tuple.add(new Pair<Integer, Integer>(4, 0)); // ColorRadioDisplay = false
//	tuple.add(new Pair<Integer, Integer>(5, 1)); // OtherFeatures = true
	
	
	System.out.println( tc.verifyWithMDD(tuple) ); // stampa true
	
//	ParameterValuesToInt valToInt = new ParameterValuesToInt(model);
//	System.out.println(valToInt.convertInt(1).getFirst().getName());
	
//	System.out.println( valToInt.get("Boeing"));
//	System.out.println( valToInt.get("SeatsConfiguration"));
//	System.out.println( valToInt.get("Rows3Passengers210"));
//	System.out.println( valToInt.get("FuelCapacity"));
//	System.out.println( valToInt.get("Liters126920"));
	
//	Vector<TestContext> tcList = new Vector<TestContext>();
	tc.addTuple(tuple);
//	
	ArrayList<String> testCases = new ArrayList<String>();
	testCases.add(tc.getTest(false));
//	
//	System.out.println("-----TEST SUITE-----");
	Operations.translateOutput(testCases, model);
	
	
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
	
	
	
	
	//verifyWithMDD(Vector<Pair<Integer, Integer>> tuple) 
	
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
			
	System.out.println();
	
//	// 6) Creating a safe queue (for concurrency) in order to manage the tuples
//	// SHARED OBJECT between producer and consumer
//	SafeQueue tuples = new SafeQueue();
//	
//	// 7) Creating the tuple generator tg
//	// Combination generator
//	Iterator<List<Pair<Integer, Integer>>> tg = TupleGenerator.getAllKWiseCombination(m);
//
//	// 8) Create the filler thread with tuples queue and tg
//	//	  then ".start()" the filler thread (call the method "run()")
//	//	  -> tuples queue is continuously filled until all
//	//    the tuples are inserted (stopping and restarting
//	//	  every time the tuples queue is full)
//	TupleFiller tFiller = new TupleFiller(tg, tuples);
//	Thread tFillerThread = new Thread(tFiller);
//	tFillerThread.start();
//	
//	// 9)
//	// Start all the TestBuilder threads:
//	// Here we are asking for the available number of logical cores on
//	// the current machine. We are creating a number of thread which
//	// is the maximum number that can be created within the used calculator
//	int nThreads = Runtime.getRuntime().availableProcessors();
//	ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
//	// Vector<TestContext> tcList = new Vector<TestContext>();
//	boolean sort = false;
//	ArrayList<Thread> testBuilderThreads = new ArrayList<Thread>();
//	for (int i=0; i<nThreads; i++) {
//		// at every tBuilder I pass as parameter:
//		// - baseMDD = base MTS of the model with constraints
//		// - tuples = the shared object which has the tuples to be consumed
//		// - tcList = the list where the tc have to be added
//		// - sort = boolean that ask wheter to use the sorting optimization or not
//		// - manager = the object of MDD library that deals with MDD operations
//		// - testContextsMutex = the semaphore for managing the access to tcList object
//		Thread tBuilder = new Thread(new TestBuilder(baseMDD, tuples, tcList, sort, m.getnParams(), m.getUseConstraints(), manager, testContextsMutex));
//		testBuilderThreads.add(tBuilder);
//		tBuilder.start();
//		// testBuilder = the thread that consumes the tuples from the shared object
//		// and searches the best context where to drop it in "tcList"
//	}
	
	}

}
