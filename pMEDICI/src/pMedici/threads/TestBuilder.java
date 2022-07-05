package pMedici.threads;

import java.util.Comparator;
import java.util.Vector;

import org.colomoto.mddlib.MDDManager;

import pMedici.main.PMedici;
import pMedici.safeelements.ExtendedSemaphore;
import pMedici.safeelements.SafeQueue;
import pMedici.safeelements.TestContext;
import pMedici.util.Operations;
import pMedici.util.Pair;

public class TestBuilder implements Runnable {
	
	// if true, when a test context is created but not filled, then it can be reused the next time
	public static boolean RecycleUnusedTestContexts = true;
	
	TestContext empty = null;

	/**
	 * The base MDD containing the constraints
	 */
	int baseMDD;

	/**
	 * The queue in which the tuples are stored
	 */
	SafeQueue safeQueue;
	
	/** 
	 * The list of all the Test Contexts
	 */
	Vector<TestContext> tcList;
	
	/**
	 * The mutex semaphore for interacting with the test context list
	 */
	ExtendedSemaphore testContextMutex;
	
	/**
	 * Use the sort optimization?
	 */
	boolean sort;
	
	/**
	 * The number of parameters in the combinatorial problem
	 */
	int nParam;
	
	/**
	 * The number of uncoverable tuples
	 */
	int nUncoverable;
	
	/**
	 * Use the constraints?
	 */
	boolean useConstraints;
	
	/**
	 * The MDD Manager
	 */
	MDDManager manager;
	
	/**
	 * Builds a new test builder
	 * 
	 * @param baseMDD: the MDD containing the constraints
	 * @param safeQueue: the queue in which the tuples are stored
	 * @param tcList: the list of all the Test Contexts
	 * @param sort: use the sort optimization?
	 * @param nParam: the number of parameters in the combinatorial problem
	 * @param useConstraints: use the constraints?
	 * @param manager: the MDD Manager
	 * @param testContextMutex: the mutex semaphore for interacting with the test context list
	 */
	public TestBuilder(int baseMDD, SafeQueue safeQueue, Vector<TestContext> tcList, boolean sort, int nParam, boolean useConstraints, MDDManager manager, ExtendedSemaphore testContextMutex) {
		this.baseMDD = baseMDD;
		this.safeQueue = safeQueue;
		this.tcList = tcList;
		this.testContextMutex = testContextMutex;
		this.sort = sort;
		this.nParam = nParam;
		this.nUncoverable = 0;
		this.useConstraints = useConstraints;
		this.manager = manager;
	}
	
	/**
	 * Finds a test contexts that implies the tuple
	 * 
	 * @param tuple: the tuple to be managed
	 * @return true if a TC is found, false otherwise 
	 */
	private boolean findImplied(Vector<Pair<Integer, Integer>> tuple) { 
		boolean found = false;
		for (int i=0; i<this.tcList.size(); i++) {
			// Try to acquire the mutex
			if (tcList.get(i).testMutex.tryAcquire()) {
				assert(tcList.get(i).testMutex.lockedByCaller());
				// Check the predicate
				if (tcList.get(i).isImplied(tuple)) {
					found = true;
				}
				// In any case free this context
				tcList.get(i).testMutex.release();
			}
			if (found)
				break;
		}
		return found;
	}
	
	/**
	 * Finds a test contexts that is compatible with the tuple
	 * 
	 * @param tuple: the tuple to be managed
	 * @param orderedList: the list of the ordered test contexts
	 * @return true if a TC is found, false otherwise 
	 * @throws InterruptedException 
	 */
	private boolean findCompatible(Vector<Pair<Integer, Integer>> tuple, Vector<TestContext> orderedList) throws InterruptedException {
		boolean found = false;
		for (int i=0; i<orderedList.size(); i++) {
			// Try to acquire the mytex
			if (orderedList.get(i).testMutex.tryAcquire()) {
				assert(orderedList.get(i).testMutex.lockedByCaller());
				// Check the predicate
				if (orderedList.get(i).isCoverable(tuple)) {
					orderedList.get(i).addTuple(tuple);
					found = true;
				}
				// In any case free this context
				orderedList.get(i).testMutex.release();
			}
			if (found)
				break;
		}
		return found;
	}

	@Override
	public void run() {
		// Extract all the values
		while (!safeQueue.finished()) {
			Vector<Pair<Integer, Integer>> tuple = safeQueue.get();
			if (tuple != null) {
				// If a tuple has been extracted
				// Try to find a TestContext which implies this tuple
				if (findImplied(tuple)) {
					if (PMedici.PRINT_DEBUG) {
						System.out.println("The tuple " + Operations.printTuple(tuple) + " is already implied");
					}
					continue;
				}
				
				// If no implied is found, then order the tests contexts in a way that the most different one (i.e., the best one) is the first
				Vector<TestContext> orderedTcList = new Vector<TestContext>();
				try {
					this.testContextMutex.acquire();
					for (TestContext tc : tcList) {
						if (tc.isCompatiblePartialCheck(tuple)) {
							orderedTcList.add(tc);
						}
					}
					if (orderedTcList.size() > 0 && sort) {
						// Sort the orderedTestContext list
						orderedTcList.sort(new Comparator<TestContext>() {
							@Override
							public int compare(TestContext o1, TestContext o2) {
								try {
									int o2NPaths = o2.getNPaths();
									int o1NPaths = o1.getNPaths();
									return Integer.compare(o2NPaths - o2.getNPaths(tuple), o1NPaths - o1.getNPaths(tuple));
								} catch (InterruptedException e) {
									System.out.println(e.getMessage());
								}
								return -1;
							}
						});
					}
					this.testContextMutex.release();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
				
				// Find if an already existing test context can cover the tuple
				try {
					if (findCompatible(tuple, orderedTcList)) {
						if (PMedici.PRINT_DEBUG)
							System.out.println("The tuple " + Operations.printTuple(tuple) + " has been covered by an already existing test context");
						continue;
					}
				} catch (InterruptedException e1) {
					System.out.println(e1.getMessage());
				}
				
				// Incompatible or not implied for every test context
				// 	-> Not implied and not coverable: build a new test context
				TestContext tc;
				if (empty != null && RecycleUnusedTestContexts) {
					tc = empty;
				} else {
					tc = new TestContext(baseMDD, nParam, useConstraints, manager);
				}
				
				try {
					tc.testMutex.acquire();
					// Check if it is coverable by a new test context
					if (tc.isCoverable(tuple)) {
						tc.addTuple(tuple);
						tc.testMutex.release();
						if (PMedici.PRINT_DEBUG)
							System.out.println("The tuple " + pMedici.util.Operations.printTuple(tuple) + " has been covered by a new test context");
						// Add the new test context to the list
						this.testContextMutex.acquire();
						tcList.add(tc);
						this.testContextMutex.release();
						empty = null; // empty is no longer empty
					} else {
						if (PMedici.PRINT_DEBUG)
							System.out.println("The tuple " + pMedici.util.Operations.printTuple(tuple) + " is not coverable");
						nUncoverable++;
						empty = tc; // empty is usable if needed
						tc.testMutex.release();
					}
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}				
			}
		}	
	}	
	
}