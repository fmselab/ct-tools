package mantra;

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


import mantra.model.Model;
import mantra.safeelements.SafeQueue;
import mantra.threads.TupleFiller;
import mantra.util.Pair;
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
	 public int generateTests(String fileName, int strength, int nThreads)
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
		SafeQueue<?,?> tuples = model.getSafeQueue(); //TODO sistemare!

		

		return 0;

	}
	 
}