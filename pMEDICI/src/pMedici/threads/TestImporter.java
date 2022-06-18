package pMedici.threads;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import pMedici.safeelements.ExtendedSemaphore;

public class TestImporter implements Runnable {

	/**
	 * The queue in which the tests must be added
	 */
	ConcurrentLinkedQueue<Map<String, String>> oldTestsQueue;

	/**
	 * The mutex semaphore for interacting with the {@link Vector} of the imported
	 * test cases from CSV
	 */
	ExtendedSemaphore oldTestsMutex;

	/**
	 * The {@link Vector} of the imported test cases from CSV
	 */
	Vector<Map<String, String>> oldTests;

	/**
	 * Creates a new thread that fills the shared queue with the old tests
	 *
	 */
	public TestImporter(ConcurrentLinkedQueue<Map<String, String>> oldTestsQueue, ExtendedSemaphore oldTestsMutex, Vector<Map<String, String>> oldTests) {
		this.oldTestsQueue = oldTestsQueue;
		this.oldTestsMutex = oldTestsMutex;
		this.oldTests = oldTests;
	}

	@Override
	public void run() {
		while(true) {
			
			try {
				oldTestsMutex.acquire();
				
				// if the Vector of the old tests is empty, the
				// the thread is stopped
				if(oldTests.isEmpty()) {
					oldTestsMutex.release();
					break;
				}	
				// otherwise we take the last test and we insert it in the queue
				else {
					// Extracting the last element
					Map<String, String> oldTest = oldTests.lastElement();
					oldTests.remove( (oldTests.size()-1) );
					oldTestsMutex.release();
					
					oldTestsQueue.add(oldTest);
					
				}
				
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			
			
			
		}

	}

}
