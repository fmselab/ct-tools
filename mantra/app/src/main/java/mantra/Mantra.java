package mantra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.util.ModelUtils;
import ctwedge.util.Pair;
import ctwedge.util.validator.SMTTestSuiteValidator;
import mantra.model.Model;
import mantra.safeelements.ExtendedSemaphore;
import mantra.safeelements.SafeQueue;
import mantra.safeelements.TestContext;
import mantra.threads.TestBuilder;
import mantra.threads.TupleFiller;
import mantra.util.Order;
import mantra.util.StatsTestSuite;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public class Mantra implements Callable<Integer> {

	@Parameters(index = "0", description = "The strength for test generation.")
	int strength = 2;

	@Parameters(index = "1", description = "The name of the file containing the model in CTW format.")
	String fileName = "";

	@Option(names = "-d", description = "The parent folder of the plugin in Jar format.")
	String pluginDir = null;

	@Option(names = "-n", description = "Number of threads to be used for test building. Do not specify (or set to 0) if the one of the system architecture has to be used.")
	private int nThreads = Runtime.getRuntime().availableProcessors();

	/** Use the verbose mode */
	@Option(names = "-verb", defaultValue = "true", description = "Use the verbose mode.")
	boolean verb;

	/** The print debug. */
	public static boolean PRINT_DEBUG = false;
	static String OUTPUT_TXT = "output.txt";
	public static boolean SORT = true;
	public static Order ORDER = Order.IN_ORDER_SIZE_DESC;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException          Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		Mantra mantra = new Mantra();
		int exitCode = new CommandLine(mantra).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() throws Exception {
		System.out.println("\r" 
				+ "  __  __             _             \r\n" 
				+ " |  \\/  |           | |            \r\n"
				+ " | \\  / | __ _ _ __ | |_ _ __ __ _ \r\n" 
				+ " | |\\/| |/ _` | '_ \\| __| '__/ _` |\r\n"
				+ " | |  | | (_| | | | | |_| | | (_| |\r\n" 
				+ " |_|  |_|\\__,_|_| |_|\\__|_|  \\__,_|\r\n" 
				+ "");
		generateTests(fileName, strength, nThreads);
		return 0;
	}

	/**
	 * Generate tests.
	 *
	 * @param fileName the file name containing the cit model in CTWEDGE format!
	 * @param strength the strength
	 * @param nThreads the number of threads to be used
	 *
	 * @throws IOException          Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public void generateTests(String fileName, int strength, int nThreads) throws IOException, InterruptedException {
		if (pluginDir == null)
			pluginDir = "plugins";
		System.setProperty(DefaultPluginManager.PLUGINS_DIR_PROPERTY_NAME, pluginDir);

		PluginManager pluginManager = new DefaultPluginManager();
		pluginManager.loadPlugins();
		pluginManager.startPlugins();

		String[] pluginIds = choosePlugins(pluginManager);

		List<StatsTestSuite> suites = new ArrayList<>(pluginIds.length);

		for (String pluginId : pluginIds) {
			StatsTestSuite suite = generateTestsWithPlugin(fileName, strength, nThreads, pluginManager, pluginId);
			suites.add(suite);

			System.out.println(suite.toString());

			// Print the tests
			if (verb) {
				System.out.println("Generator: " + pluginId);
				System.out.println("Covered: " + suite.getCoveredTuples() + " tuples");
				System.out.println("Uncovered: " + suite.getNotCoveredTuples() + " tuples");
				System.out.println("Total number of tuples: " + suite.getTotTuples() + " tuples");
				System.out.println("Time required for test suite generation: " + suite.getGeneratorTime() + " ms");
				System.out.println("Generated " + suite.getTestNumber() + " tests");
			} else {
				FileWriter fw = new FileWriter(OUTPUT_TXT, true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(fileName + ";" + suite.getGeneratorName() + ";" + suite.getTestNumber() + ";"
						+ suite.getGeneratorTime() + ";" + SORT + ";" + ORDER.toString() + ";" + nThreads);
				bw.newLine();
				bw.close();
			}

			SMTTestSuiteValidator validator = new SMTTestSuiteValidator(suite);
			System.out.println("Is the suite valid? " + validator.isValid());
			System.out.println("Validator's covered tuples: " + validator.howManyTuplesCovers());

		}
	}

	protected StatsTestSuite generateTestsWithPlugin(String fileName, int strength, int nThreads,
			PluginManager pluginManager, String pluginId) throws InterruptedException, IOException {

		if (PRINT_DEBUG) {
			List<Class<?>> ext = pluginManager.getExtensionClasses(pluginId);
			System.out.println(String.format("Found %d extensions in plugin %s:", ext.size(), pluginId));
			for (Class<?> extension : ext) {
				System.out.println("  " + extension.getCanonicalName());
			}
		}

		Model model = pluginManager.getExtensions(Model.class, pluginId).get(0);

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

		if (PRINT_DEBUG) {
			System.out.println("Started test suite generation with " + pluginId + "...");
		}

		// Get current time
		long start = System.currentTimeMillis();

		// Shared object between producer and consumer
		SafeQueue tuples = new SafeQueue();
		Iterator<List<Pair<Object, Object>>> tg = ModelUtils.getAllKWiseCombination(model.getElements(ORDER), strength);

		// Start the filler thread
		TupleFiller tFiller = new TupleFiller(tg, tuples);
		Thread tFillerThread = new Thread(tFiller);
		tFillerThread.start();

		// Start all the TestBuilder threads
		if (nThreads == 0) {
			nThreads = Runtime.getRuntime().availableProcessors();
			if (Mantra.PRINT_DEBUG)
				System.out.println("using " + nThreads + " threads");
		}

		ExtendedSemaphore testContextsMutex = new ExtendedSemaphore();
		Vector<TestContext> tcList = new Vector<TestContext>();
		ArrayList<Thread> testBuilderThreads = new ArrayList<Thread>();
		for (int i = 0; i < nThreads; i++) {
			Thread tBuilder = new Thread(new TestBuilder(model, tuples, tcList, SORT, model.getNParams(),
					model.getUseConstraints(), testContextsMutex, pluginManager, pluginId));
			testBuilderThreads.add(tBuilder);
			tBuilder.start();
		}

		// Join all the test builder threads
		for (int i = 0; i < nThreads; i++) {
			testBuilderThreads.get(i).join();
		}

		if (PRINT_DEBUG) {
			System.out.println("Test suite generation with " + pluginId + " finished");
		}

		// Compute the summary values
		System.out.println("-----TEST SUITE: " + pluginId + "-----");

		// First row -> parameter names
		HashSet<String> tests = new HashSet<String>();

		// Remove empty contexts
		tcList.removeIf(x -> x.getNCovered() == 0);

		for (TestContext tc : tcList) {
			nCovered += tc.getNCovered();
			try {
				tests.add(tc.getTest(false));
			} catch (InterruptedException | SolverException e) {
				System.out.println(e.getMessage());
			}
			// Close the context
			tc.close();
		}

		long stop = System.currentTimeMillis();

		totTuples = tuples.getNTuples();
		String testsAsCsv = model.translateOutputToString(tests);
		// Join the tuple filler thread
		tFillerThread.join();

		StatsTestSuite testSuite = new StatsTestSuite(testsAsCsv, model.getCitModel(), strength, totTuples, nCovered,
				tcList.size(), stop - start, nThreads, ORDER, pluginId);

		return testSuite;
	}

	private String[] choosePlugins(PluginManager pluginManager) {
		String[] pluginIdsNo = {};

		List<String> availablePlugins = pluginManager.getPlugins().stream().map(it -> it.getPluginId())
				.collect(Collectors.toList());
		System.out.println("Available plugins:");
		for (int i = 0; i < availablePlugins.size(); i++) {
			System.out.println(i + " - " + availablePlugins.get(i));
		}

		String line;

		System.out.println("Choose which generation plugin(s) to use ('all' to use all the available plugins):");

		if (System.console() != null) {
			line = System.console().readLine();
		} else {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
				line = reader.readLine();
			} catch (IOException e) {
				System.err.print(e.getMessage());
				line = "all";
			}
		}

		pluginIdsNo = line.split(" ");

		if (pluginIdsNo.length == 1 && (pluginIdsNo[0].equalsIgnoreCase("all") || pluginIdsNo[0].isBlank()))
			pluginIdsNo = availablePlugins.toArray(new String[0]);

		String[] pluginIds = new String[pluginIdsNo.length];

		for (int i = 0; i < pluginIdsNo.length; i++) {
			try {
				pluginIds[i] = availablePlugins.get(Integer.parseInt(pluginIdsNo[i]));
			} catch (NumberFormatException nfe) {
				pluginIds = availablePlugins.toArray(new String[availablePlugins.size()]);
				break;
			}
		}

		return pluginIds;
	}

}