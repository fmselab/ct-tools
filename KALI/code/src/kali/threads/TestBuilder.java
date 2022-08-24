package kali.threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.Pair;
import kali.main.KALI;
import kali.safeelements.ExtendedSemaphore;
import kali.safeelements.SafeQueue;
import kali.safeelements.TestContext;
import kali.util.Operations;

public class TestBuilder implements Runnable {

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
	 * The structure mapping the parameter on its position
	 */
	Map<String, Integer> paramPosition;

	/**
	 * The Cit Model
	 */
	CitModel model;
	
	/**
	 *  empty test context : if null there no empty test context that can be used otherwise
	 *  reuse this.
	 */
	TestContext empty = null;
	
	/**
	 * Set the optimization: LockTCOnlyOnWriting
	 */
	public static boolean LockTCOnlyOnWriting = true;
	
	/**
	 * Exclude the check during testing
	 */
	public static boolean IN_TEST = false;

	/**
	 * Builds a new test builder
	 * 
	 * @param safeQueue:        the queue in which the tuples are stored
	 * @param tcList:           the list of all the Test Contexts
	 * @param sort:             use the sort optimization?
	 * @param nParam:           the number of parameters in the combinatorial
	 *                          problem
	 * @param useConstraints:   use the constraints?
	 * @param testContextMutex: the mutex semaphore for interacting with the test
	 *                          context list
	 * @param paramPosition:    structure mapping the parameter on its position
	 * @param model:            the combinatorial model
	 */
	public TestBuilder(SafeQueue safeQueue, Vector<TestContext> tcList, boolean sort, int nParam,
			boolean useConstraints, ExtendedSemaphore testContextMutex, Map<String, Integer> paramPosition,
			CitModel model) {
		this.safeQueue = safeQueue;
		this.tcList = tcList;
		this.testContextMutex = testContextMutex;
		this.sort = sort;
		this.nParam = nParam;
		this.nUncoverable = 0;
		this.useConstraints = useConstraints;
		this.paramPosition = paramPosition;
		this.model = model;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (empty!= null) empty.close();
	}
	
	
	/**
	 * Finds a test contexts that implies the tuple
	 * 
	 * @param tuple: the tuple to be managed
	 * @return true if a TC is found, false otherwise
	 */
	private boolean findImplied(Vector<Pair<String, Object>> tuple) {
		boolean found = false;
		for (int i=0; i<this.tcList.size(); i++) {
			// Try to acquire the mutex if the lock even during reading is required
			if (!LockTCOnlyOnWriting)
				if (!tcList.get(i).testMutex.tryAcquire())
					continue;
				else 
					// If the lock has been acquired, check if it is locked by the caller
					if (!IN_TEST)
						assert(tcList.get(i).testMutex.lockedByCaller());
			
			// Check the predicate
			if (tcList.get(i).isImplied(tuple)) {
				found = true;
			}
			
			// In any case free this context if the lock has been acquired
			if (!LockTCOnlyOnWriting)
				tcList.get(i).testMutex.release();
			
			if (found)
				break;
		}
		return found;
	}

	/**
	 * Finds a test contexts that is compatible with the tuple
	 * @param completenessGrades 
	 * 
	 * @param tuple:       the tuple to be managed
	 * @param orderedList: the list of the ordered test contexts
	 * @param completenessGrades: the list of Pairs <Index, grades>
	 * @return true if a TC is found, false otherwise
	 * @throws InterruptedException
	 * @throws SolverException
	 */
	private boolean findCompatible(Vector<Pair<String, Object>> tuple, Vector<TestContext> orderedList, List<Pair<Integer, Integer>> completenessGrades)
			throws InterruptedException, SolverException {
		boolean found = false;
		for (int i = 0; i < completenessGrades.size(); i++) {
			int index = completenessGrades.get(i).getFirst();
			
			// Try to acquire the mutex
			if (orderedList.get(index).testMutex.tryAcquire()) {
				if (!IN_TEST)
					assert (orderedList.get(index).testMutex.lockedByCaller());
				// Check the predicate
				if (orderedList.get(index).isCoverable(tuple)) {
					found = orderedList.get(index).addTuple(tuple);
				}
				// In any case free this context
				orderedList.get(index).testMutex.release();
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
			Vector<Pair<String, Object>> tuple = safeQueue.get();
			if (tuple != null) {
				// If a tuple has been extracted
				// Try to find a TestContext which implies this tuple
				if (findImplied(tuple)) {
					if (KALI.PRINT_DEBUG)
						System.out.println("The tuple " + Operations.printTuple(tuple) + " is already implied");
					continue;
				}

				// If no implied is found, then order the tests contexts in a way that the most
				// different one (i.e., the best one) is the first
				Vector<TestContext> orderedTcList = new Vector<TestContext>();
				List<Pair<Integer, Integer>> completenessGrades = new ArrayList<Pair<Integer, Integer>>();
				int i = 0;
				try {
					this.testContextMutex.acquire();
					for (TestContext tc : tcList) {
						if (tc.isCompatiblePartialCheck(tuple)) {
							orderedTcList.add(tc);
							completenessGrades.add(new Pair<Integer, Integer>(i, tc.getCompletenessGrade()));
							i++;
						}
					}
					this.testContextMutex.release();
					if (orderedTcList.size() > 0 && sort) {
						// Sort <TCIndex, CompletenessGrade>
						Collections.sort(completenessGrades, new Comparator<Pair<Integer, Integer>>() {
							@Override
							public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
								return Integer.compare(o2.getSecond(), o1.getSecond());
							}
						});
					}					
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}

				// Find if an already existing test context can cover the tuple
				try {
					if (findCompatible(tuple, orderedTcList, completenessGrades)) {
						if (KALI.PRINT_DEBUG)
							System.out.println("The tuple " + Operations.printTuple(tuple)
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
				try {
					if (empty != null)
						tc = empty;
					else
						tc = new TestContext(nParam, useConstraints, paramPosition, model);
					
					try {
						tc.testMutex.acquire();
						// Check if it is coverable by a new test context
						if (tc.isCoverable(tuple)) {
							boolean added = tc.addTuple(tuple);
							if (!added)
								safeQueue.insert(tuple);
							tc.testMutex.release();
							if (KALI.PRINT_DEBUG)
								System.out.println("The tuple " + Operations.printTuple(tuple)
										+ " has been covered by a new test context");
							// Add the new test context to the list
							this.testContextMutex.acquire();
							tcList.add(tc);
							this.testContextMutex.release();
							// empty is no longer empty
							empty = null;
						} else {
							if (KALI.PRINT_DEBUG)
								System.out.println("The tuple " + Operations.printTuple(tuple) + " is not coverable");
							nUncoverable++;
							tc.testMutex.release();
							// the empty is now valid 
							empty = tc;
							tc.testMutex.release();
						}
					} catch (InterruptedException e) {
						System.out.println(e.getMessage());
					} catch (SolverException e) {
						System.out.println(e.getMessage());
					}
				} catch (InvalidConfigurationException e1) {
					System.out.println(e1.getMessage());
				}
			}
		}
	}

}
