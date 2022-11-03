package pMedici.main;

import java.io.BufferedReader;
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
import pMedici.experiments.pMEDICIExperimenter;

/**
 * Main class to call pMedici
 */
public class PMedici implements Callable<Integer> {

	@Parameters(index = "0", description = "The strength for test generation.")
	int strength = 2;

	@Parameters(index = "1", description = "The name of the file containing the model in CTW format.")
	String fileName = "";

	@Option(names = "-n", description = "Number of threads to be used for test building. Do not specify (or set to 0) if the one of the system architecture has to be used.")
	private int nThreads = Runtime.getRuntime().availableProcessors();

	/** Use the verbose mode */
	@Option(names = "-verb", description = "Use the verbose mode.")
	boolean verb;

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

	/** the model for the generation */
	CitModel model;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException          Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		PMedici pMedici = new PMedici();
		int exitCode = new CommandLine(pMedici).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() throws Exception {
		generateTests(fileName, strength, nThreads);
		return 0;
	}

	/**
	 * Covert from ctwedge to medici and returns the model as string
	 * 
	 * @param fileName the file containing the CTWedge model
	 * @return the MEDICI model as string
	 * @throws IOException
	 */
	public String buildMediciModel(String fileName) throws IOException {
		assert fileName.endsWith(".ctw");
		assert Files.exists(Paths.get(fileName));
		assert Files.isRegularFile(Paths.get(fileName));

		model = Utility.loadModelFromPath(fileName);
		MediciCITGenerator gen = new MediciCITGenerator();
		MediciCITGenerator.OUTPUT_ON_STD_OUT_DURING_TRANSLATION = false;
		// If no constraints are present into the model, then we can ignore constraints
		return gen.translateModel(model, model.getConstraints().size() == 0);
	}

	/**
	 * Generate tests.
	 *
	 * @param fileName the file name containing the cit model in CTWEDGE format!
	 * @param strength the strength
	 * @param nThreads the number of threads to be used
	 * @return the test suite
	 * @throws IOException          Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public TestSuite generateTests(String fileName, int strength, int nThreads)
			throws IOException, InterruptedException {
		// Get current time
		long start = System.currentTimeMillis();
		
		String mediciModel = "";
		// Convert the model from CTWedge to Medici format
		if (!fileName.equals("")) {
			mediciModel = buildMediciModel(fileName);
		} else {
			assert false : "You must specify the name of the file containing the CTWedge model";
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
		
		// Test builder threads may start after the queue is completely full
		while(!tuples.full()) {}

		// Start all the TestBuilder threads
		if (nThreads == 0)
			nThreads = Runtime.getRuntime().availableProcessors();
		ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
		Vector<TestContext> tcList = new Vector<TestContext>();
		boolean sort = false;
		ArrayList<Thread> testBuilderThreads = new ArrayList<Thread>();
		for (int i = 0; i < nThreads; i++) {
			Thread tBuilder = new Thread(new TestBuilder(baseMDD, tuples, tcList, sort, m.getnParams(),
					m.getUseConstraints(), manager, testContextsMutex, verb));
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
		String tsAsCSV = Operations.translateOutputToString(testCases, model);
		System.out.println(tsAsCSV);
		long generationTime = (System.currentTimeMillis() - start);

		if (verb) {
			totTuples = tuples.getNTuples();
			System.out.println("Covered: " + nCovered + " tuples");
			System.out.println("Uncovered: " + (totTuples - nCovered) + " tuples");
			System.out.println("Total number of tuples: " + totTuples + " tuples");
			System.out.println("Time required for test suite generation: " + generationTime + " ms");
		}

		// Join the tuple filler thread
		tFillerThread.join();
		
		// Create and return the test suite
		TestSuite testSuite = new TestSuite(tsAsCSV, model);
		testSuite.setStrength(strength);
		testSuite.setGeneratorTime(generationTime);
		
		return testSuite;
	}

	/**
	 * Returns the model used by pMEDICI
	 * 
	 * @return the model used by pMEDICI
	 */
	public CitModel getModel() {
		return this.model;
	}
}