package pMedici.threads;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import ctwedge.ctWedge.CitModel;
import pMedici.safeelements.SafeQueue;
import pMedici.util.ExtendedSemaphore;
import pMedici.util.Operations;
import pMedici.util.TestContext;

public class TestSuitePrinter implements Runnable {

	/**
	 * The list of all the Test Contexts
	 */
	Vector<TestContext> tcList;

	/**
	 * The mutex semaphore for interacting with the test context list
	 */
	ExtendedSemaphore testContextMutex;

	/**
	 * The queue in which the tuples are stored
	 */
	SafeQueue safeQueue;

	/**
	 * Start time instant
	 */
	long start;

	/**
	 * The step (milliseconds) for printing
	 */
	int msStep;

	/**
	 * The combinatorial model
	 */
	CitModel model;
	
	/**
	 * The prefix
	 */
	String prefix;

	/**
	 * The output path
	 */
	String output;

	/**
	 * Builds a new TestSuitePrinter
	 * 
	 * @param tcList
	 * @param testContextMutex
	 * @param safeQueue
	 * @param msStep
	 * @param model
	 */
	public TestSuitePrinter(Vector<TestContext> tcList, ExtendedSemaphore testContextMutex, SafeQueue safeQueue,
			int msStep, CitModel model, String prefix, String output) {
		this.tcList = tcList;
		this.testContextMutex = testContextMutex;
		this.safeQueue = safeQueue;
		this.start = System.currentTimeMillis();
		this.msStep = msStep;
		this.model = model;
		this.prefix = prefix;
		this.output = output;
	}

	/**
	 * Prints the test suite
	 */
	@Override
	public void run() {
		int counter = 0;
		
		// Create the directory
		new File(output).mkdir();

		// Extract all the values
		while (!safeQueue.finished()) {
			if ((System.currentTimeMillis() - start) > msStep) {
				// Print the test suite
				try {
					testContextMutex.acquire();

					// Obtain the list of test cases
					ArrayList<String> testCases = new ArrayList<String>();
					for (TestContext tc : tcList) {
						testCases.add(tc.getTest(false));
					}

					// Convert the list in the CSV format
					String tsAsCSV = Operations.translateOutputToString(testCases, model);

					// Store the list in the results folder
					BufferedWriter bw = new BufferedWriter(
							new FileWriter(new File(output + "/" + prefix + counter + ".csv")));
					bw.write(tsAsCSV);
					bw.close();

					// Release the mutex
					testContextMutex.release();
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
				// Update the start value
				start = System.currentTimeMillis();
				counter++;
			}
		}
	}

}
