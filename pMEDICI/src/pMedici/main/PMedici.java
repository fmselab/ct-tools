package pMedici.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.colomoto.mddlib.MDDManager;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.generator.util.Utility;
import ctwedge.util.TestSuite;
import pMedici.safeelements.ExtendedSemaphore;
import pMedici.safeelements.SafeQueue;
import pMedici.safeelements.TestContext;
import pMedici.threads.TestBuilder;
import pMedici.threads.TupleFiller;
import pMedici.util.ModelToMDDConverter;
import pMedici.util.Operations;
import pMedici.util.Pair;
import pMedici.util.TestModel;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import pMedici.combinations.TupleGenerator;

/**
 *  main class to call pMedici
 * TODO: use the picocli library, convert method to non static and variables as fields
 */
public class PMedici implements Callable<Integer> {
	
	@Parameters(index = "0", description = "The strength for test generation.")
	int strength = 2;
	
	@Parameters(index = "1", description = "The name of the file containing the model in CTW format.")
	String fileName = "";
	

	/** Use the verbose mode */
	@Option(names = "-verb", description = "Use the verbose mode.")
	boolean verb;
	
	/** The print debug. */
	public static boolean PRINT_DEBUG = false;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		PMedici pMedici = new PMedici();
		int exitCode = new CommandLine(pMedici).execute(args);
		System.exit(exitCode);
	}
	
	@Override
    public Integer call() throws Exception {
		generateTests(fileName, strength);
		return 0;
	}
	
	// the model for the generation
	CitModel model;

	// covert from ctwedge to medici and saves into a file called "model.txt"
	private String buildMediciModel(String fileName) throws IOException {
		assert fileName.endsWith(".ctw");
		assert Files.exists(Paths.get(fileName));
		assert Files.isRegularFile(Paths.get(fileName));
		
		model = Utility.loadModelFromPath(fileName);
		MediciCITGenerator gen = new MediciCITGenerator();
		MediciCITGenerator.OUTPUT_ON_STD_OUT_DURING_TRANSLATION = false;
		return gen.translateModel(model, false);
	}

	/**
	 * Generate tests.
	 *
	 * @param fileName the file name containing the cit model in CTWEDGE format!
	 * @param strength the strength
	 * @return 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	 TestSuite generateTests(String fileName, int strength)
			throws IOException, InterruptedException {
		 String mediciModel = "";
		// Convert the model from CTWedge to Medici format
		if (!fileName.equals("")) {
			mediciModel = buildMediciModel(fileName);
		} else {
			assert false : "what to do if the filename is empty???";
		}
		// Read the combinatorial model and get the MDD representing the model without
		// constraints
		TestModel m = Operations.readModelFromReader(new BufferedReader(new StringReader(mediciModel)));

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
		TestSuite testSuite = new TestSuite(Operations.translateOutputToString(testCases, model),model);
		System.out.println(testSuite.getTs());
		// TODO
		// testSuite.setGeneratorTime(nThreads);

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
		// return the test suite
		return testSuite;
	}

}