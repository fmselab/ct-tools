package mantra.threads;

import java.util.Collections;
import java.util.Vector;

import org.pf4j.PluginManager;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.util.Pair;
import mantra.Mantra;
import mantra.model.Model;
import mantra.safeelements.ExtendedSemaphore;
import mantra.safeelements.SafeQueue;
import mantra.safeelements.TestContext;

public class TestBuilder implements Runnable {

	/*
	 * ----------------------------------- 
	 * 			OPTIMIZATIONS
	 * -----------------------------------
	 */

	// if true, when a test context is created but not filled, then it can be reused
	// the next time
	public static boolean RecycleUnusedTestContexts = true;

	// if true, a test context is locked only when writing
	public static boolean LockTCOnlyOnWriting = true;

	// if true, the lock while checking if a tuple is implied is only performed with
	// tryAcquire
	public static boolean UseTryAcquireForFindImplies = false;

	/*
	 * ----------------------------------- 
	 * 			END OPTIMIZATIONS
	 * -----------------------------------
	 */

	TestContext empty = null;

	/**
	 * Exclude the check during testing
	 */
	public static boolean IN_TEST = true;

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
	 * The Solver Model
	 */
	Model model;

	PluginManager pluginManager;

	String pluginId;

	public TestBuilder(Model model, SafeQueue safeQueue, Vector<TestContext> tcList, boolean sort, int nParam,
			boolean useConstraints, ExtendedSemaphore testContextMutex, PluginManager pluginManager, String pluginId) {
		this.safeQueue = safeQueue;
		this.tcList = tcList;
		this.testContextMutex = testContextMutex;
		this.sort = sort;
		this.nParam = nParam;
		this.nUncoverable = 0;
		this.useConstraints = useConstraints;
		this.model = model;
		this.pluginManager = pluginManager;
		this.pluginId = pluginId;
	}

	/**
	 * Finds a test contexts that implies the tuple
	 * 
	 * @param tuple: the tuple to be managed
	 * @return true if a TC is found, false otherwise
	 */
	private boolean findImplied(Vector<Pair<Object, Object>> tuple) {
		boolean found = false;
		
		for(int i = 0; i < tcList.size(); i++) {
			TestContext tc = tcList.get(i);
			// Try to acquire the mutex if the lock even during reading is required
			if (!LockTCOnlyOnWriting)
				if (UseTryAcquireForFindImplies || tc.mustTryAcquireForFindImplies()) {
					if (!tc.getTestMutex().tryAcquire())
						continue;
					else
					// If the lock has been acquired, check if it is locked by the caller
					if (!IN_TEST)
						assert (tc.getTestMutex().lockedByCaller());
				} else {
					try {
						tc.getTestMutex().acquire();
					} catch (InterruptedException e) {
						continue;
					}
				}

			// Check the predicate
			if (tc.isImplied(tuple)) {
				found = true;
			}

			// In any case free this context if the lock has been acquired
			if (!LockTCOnlyOnWriting)
				tc.getTestMutex().release();

			if (found)
				break;
		}
		return found;
	}

	private boolean findCompatible(Vector<Pair<Object, Object>> tuple, Vector<TestContext> orderedList)
			throws InterruptedException, SolverException {
		boolean found = false;
	
		for(int i = 0; i < tcList.size(); i++) {
			TestContext tc = tcList.get(i);
			// Try to acquire the mutex if it is needed
			if (!LockTCOnlyOnWriting || tc.mustLockOnReadForFindCompatible())
				if (tc.getTestMutex().tryAcquire()) 
					assert(tc.getTestMutex().lockedByCaller());
				else
					continue;
			
			// Check the predicate
			if (tc.isCoverable(tuple)) {
				found = tc.addTuple(tuple);
			}
			
			// If the context has been locked, free it
			if (!LockTCOnlyOnWriting || tc.mustLockOnReadForFindCompatible())
				tc.getTestMutex().release();
			
			// If the tuple has been added, stop the iteration
			if (found)
				break;
		}
		return found;
	}

	@Override
	public void run() {
		// Extract all the values
		while (!safeQueue.finished()) {
			Vector<Pair<Object, Object>> tuple = safeQueue.get();
			if (tuple != null) {
				// If a tuple has been extracted
				// Try to find a TestContext which implies this tuple
				if (findImplied(tuple)) {
					if (Mantra.PRINT_DEBUG) {
						System.out.println("The tuple " + model.printTuple(tuple) + " is already implied");
					}
					continue;
				}

				// If no implied is found, then order the tests contexts in a way that the most
				// different one (i.e., the best one) is the first
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
						Collections.sort(orderedTcList);
					}
					this.testContextMutex.release();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}

				// Find if an already existing test context can cover the tuple
				try {
					if (findCompatible(tuple, orderedTcList)) {
						if (Mantra.PRINT_DEBUG)
							System.out.println("The tuple " + model.printTuple(tuple)
									+ " has been covered by an already existing test context");
						continue;
					}
				} catch (InterruptedException e1) {
					System.out.println(e1.getMessage());
				} catch (SolverException e) {
					System.out.println(e.getMessage());
				}

				// Incompatible or not implied for every test context
				// -> Not implied and not coverable: build a new test context
				TestContext tc;
				if (empty != null && RecycleUnusedTestContexts) {
					tc = empty;
				} else {
					tc = pluginManager.getExtensions(TestContext.class, pluginId).get(0);
					tc.init(model, nParam, useConstraints);
				}

				try {
					tc.getTestMutex().acquire();
					// Check if it is coverable by a new test context
					if (tc.isCoverable(tuple)) {
						boolean added = tc.addTuple(tuple);
						if (!added)
							safeQueue.reinsert(tuple);

						tc.getTestMutex().release();

						// Add the new test context to the list
						this.testContextMutex.acquire();
						tcList.add(tc);
						this.testContextMutex.release();

						if (Mantra.PRINT_DEBUG)
							System.out.println(
									"The tuple " + model.printTuple(tuple) + " has been covered by a new test context");
						empty = null; // empty is no longer empty
					} else {
						if (Mantra.PRINT_DEBUG)
							System.out.println("The tuple " + model.printTuple(tuple) + " is not coverable");
						nUncoverable++;

						empty = tc; // empty is usable if needed
						tc.getTestMutex().release();
					}
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				} catch (SolverException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

}
