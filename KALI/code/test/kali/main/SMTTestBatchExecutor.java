package kali.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;

import kali.safeelements.TestContext;
import kali.util.Order;

/**
 * Class for the experiments
 */
public class SMTTestBatchExecutor {

	public static int NUM_EXECUTION = 3;
	public static String BASE_FOLDER = "./examples/ctcomp/";

	/**
	 * Calls the main of the SMTTest test generator
	 * @param threads: the number of threads to be used
	 * @param strength: the strength of threads to be used
	 * @param file: the file containing the combinatorial model
	 * @param verbose: use verbose output (print on the console) if true, or print on file if false
	 * @param solver: the solver to be used
	 * @param ordering: the parameter ordering to be uses
	 * @param sort: sort test contexts if grue
	 * @throws IOException
	 */
	static void callMain(int threads, int strength, String file, boolean verbose, String solver, String ordering, Boolean sort) throws IOException {
		// Build the line argument
		String args = "-n " + threads + (verbose ? " -verbose" : "") + " " + strength + " " + file;
		System.out.println(Arrays.toString(args.split(" ")));
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Object> task = new Callable<Object>() {
			public Object call() throws IOException, InterruptedException, InvalidConfigurationException {
				KALI.main(args.split(" "));
				return 1;
			}
		};
		Future<Object> future = executor.submit(task);
		try {
			Object result = future.get(300, TimeUnit.SECONDS);
			System.out.println(result);
		} catch (TimeoutException ex) {
			// Handle the timeout
			FileWriter fw = new FileWriter(KALI.OUTPUT_TXT, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(file + ";NA;timeout;" + sort + ";" + KALI.ORDER.toString() + ";" + threads + ";" + TestContext.SMTSolver);
			bw.newLine();
			bw.close();
		} catch (InterruptedException e) {
			// Handle the interrupt
			System.out.println(e.getMessage());
		} catch (ExecutionException e) {
			// Handle other exceptions
			System.out.println(e.getMessage());
		} finally {
			// May or may not desire this
			future.cancel(true); 
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException, InvalidConfigurationException {
		int numThreads = 32;
		// Find all the files in the test folder
		File file = new File(BASE_FOLDER);
		Solvers[] solvers = {Solvers.SMTINTERPOL, Solvers.Z3, Solvers.PRINCESS};
		//Solvers[] solvers = {Solvers.Z3};
		File[] fileList = file.listFiles();
		for (File str : fileList) {
			// Handle only CTW files
			if (str.getAbsolutePath().endsWith(".ctw")) {
				// Try all the solvers
				for (Solvers s : solvers) {
					// Try all the type of parameter ordering
					for (Order o : Order.values()) {
						// Repeat the same experiment without sorting the test contexts
						for (int i = 0; i < NUM_EXECUTION; i++)
							callMain(numThreads, 2, str.getAbsolutePath(), false, s.toString(), o.toString(), false);
	
						// Repeat the same experiment sorting the test contexts
						for (int i = 0; i < NUM_EXECUTION; i++)
							callMain(numThreads, 2, str.getAbsolutePath(), false, s.toString(), o.toString(), true);
					}
				}
			}
		}
	}
}
