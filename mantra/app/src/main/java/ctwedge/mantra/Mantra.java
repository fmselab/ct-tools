package ctwedge.mantra;

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

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.generator.util.Utility;
import ctwedge.mantra.model.Model;
import ctwedge.mantra.safeelements.SafeQueue;
import ctwedge.util.TestSuite;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public class Mantra implements Callable<Integer> {
	
	@Parameters(index = "0", description = "The strength for test generation.")
	int strength = 2;
	
	@Parameters(index = "1", description = "The name of the file containing the model in CTW format.")
	String fileName = "";
	
	@Parameters(index = "2", description = "The parent folder of the plugin in Jar format.")
	String pluginDir = null;
	
	@Option(names = "-n", description = "Number of threads to be used for test building. Do not specify (or set to 0) if the one of the system architecture has to be used.")
    private int nThreads = Runtime.getRuntime().availableProcessors();

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
		Mantra mantra = new Mantra();
		int exitCode = new CommandLine(mantra).execute(args);
		System.exit(exitCode);
	}
	
	@Override
    public Integer call() throws Exception {
		generateTests(fileName, strength, nThreads);
		return 0;
	}


	/**
	 * Generate tests.
	 *
	 * @param fileName the file name containing the cit model in CTWEDGE format!
	 * @param strength the strength
	 * @param nThreads the number of threads to be used
	 * @return 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	 public TestSuite generateTests(String fileName, int strength, int nThreads)
			throws IOException, InterruptedException {
		 assert pluginDir != null : "which plugin should be user???";
		 System.setProperty(DefaultPluginManager.PLUGINS_DIR_PROPERTY_NAME, pluginDir);
	     
		 PluginManager pluginManager = new DefaultPluginManager();
         pluginManager.loadPlugins();
	     pluginManager.startPlugins();

	    List<Model> models = pluginManager.getExtensions(Model.class);
	    System.out.println(String.format("Found %d extensions for extension point '%s'", models.size(), Model.class.getName()));
	        
		Model model = pluginManager.getExtensions(Model.class).get(0);
		
		if (!fileName.equals("")) {
			assert fileName.endsWith(".ctw");
			assert Files.exists(Paths.get(fileName));
			assert Files.isRegularFile(Paths.get(fileName));
			model.loadModelFromPath(fileName);
		} else {
			assert false : "what to do if the filename is empty???";
		}

		int nCovered = 0;
		int totTuples = 0;

		// Get current time
		long start = System.currentTimeMillis();

		// Add to the baseNode the constraints

		// Shared object between producer and consumer
		SafeQueue<?,?> tuples = model.getSafeQueue();

		// Combination generator
		Iterator<List<Pair<Integer, Integer>>> tg = TupleGenerator.getAllKWiseCombination(m);

		// Start the filler thread
		TupleFiller tFiller = new TupleFiller(tg, tuples);
		Thread tFillerThread = new Thread(tFiller);
		tFillerThread.start();

		// Start all the TestBuilder threads
		if (nThreads == 0)
			nThreads = Runtime.getRuntime().availableProcessors();
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
		String tsAsCSV = Operations.translateOutputToString(testCases, model);
		TestSuite testSuite = new TestSuite(tsAsCSV,model);
		System.out.println(tsAsCSV);
		testSuite.setStrength(strength);
		// TODO
		// testSuite.setGeneratorTime(nThreads);

		if (verb) {
			totTuples = tuples.getNTuples();
			System.out.println("Covered: " + nCovered + " tuples");
			System.out.println("Uncovered: " + (totTuples - nCovered) + " tuples");
			System.out.println("Total number of tuples: " + totTuples + " tuples");
			System.out.println(
					"Time required for test suite generation: " + (System.currentTimeMillis() - start) + " ms");
			System.out.println("Number of tests generated before duplicate removal: " + tcList.size());
			System.out.println("Number of tests generated after duplicate removal: " + testSuite.getTests().size());
		}

		// Join the tuple filler thread
		tFillerThread.join();
		// return the test suite
		return testSuite;
	}
	 
}