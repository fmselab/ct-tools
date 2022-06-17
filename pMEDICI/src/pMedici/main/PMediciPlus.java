package pMedici.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import pMedici.importer.CSVImporter;

public class PMediciPlus {
	
	public static boolean PRINT_DEBUG = true;

	public static void main(String[] args) throws IOException, InterruptedException {

		// Reading the CTWedge evolved model
		String evolvedModelPath = "../pMEDICI/evolutionModels/Boeing/Boeingv2_ctwedge.ctw";
		int strength = 2;
		CitModel model = null;
		boolean verb = false;

		// Reading the test suite of the old model
		String oldTestSuiteFilePath = "../pMEDICI/evolutionModels_TestsCSV/CSVTest_Boeingv1.csv";
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
		
		// For each for each test case of the old test suite, we check if for each parameter
		// of the evolved model (following the order used in the CitModel which is also the
		// order used in the tuple by Medici) the value of the current iteration parameter
		// is present or not in the old test suite. If it is, then we add the parameter
		// value in the tuple of the current iteration.
		for (Map<String, String> oldTest : oldTests) {
			
			/* Debug code */
			if(PRINT_DEBUG) {
			oldTest.forEach((name, value) -> {
				System.out.print(name + ":" + value + ", ");
			}); System.out.println(); }
			
			// Creating the tuple related to the current iteration
			Vector<Pair<Integer, Integer>> tuple = new Vector<Pair<Integer, Integer>>();
			int tupleIndex=0;
			
			for (Parameter param : model.getParameters()) {
	
				/* Debug code */
				if(PRINT_DEBUG) {
				System.out.println("Parametro iterazione ["+tupleIndex+"]: "+param.getName());
				}
				
				// if the parameter of the new model is in the old test suite,
				// its value is added in the corresponding position in the current tuple
				String testParamValue;
				if( ( testParamValue=oldTest.get(param.getName()) ) !=null) {
					// since we imposed that values must be all boolean, we have only 0="false" or 1="true"
					tuple.add(new Pair<Integer, Integer>(tupleIndex, testParamValue.equals("true") ? 1 : 0 ));
				}
				
				tupleIndex++;
			}
			
			// If we added at least one parameter test value to the tuple, then
			// we check if the created tuple is valid with the model constraints
			if(!tuple.isEmpty()) {
				TestContext tc = new TestContext(baseMDD, m.getnParams(), m.getUseConstraints(), manager);

				// If the tuple is compatible with the constraints, then we add the
				// add the tuple to the current test context and then we add the
				// current test context to the list of all the test context from
				// which the algorithm of medici will start executing
				
				/* Debug code */
				if(PRINT_DEBUG) {
				System.out.println("Verifica constraints: "+tc.verifyWithMDD(tuple));
				}
				
				if(tc.verifyWithMDD(tuple)) {
								
					// Adding the tuple to the current test context
					// Notice: this method also update the mdd of the text context
					tc.addTuple(tuple);
					
					// Adding the tc to the list of all the test context tcList
					tcList.add(tc);
					
				}
			}
			
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
