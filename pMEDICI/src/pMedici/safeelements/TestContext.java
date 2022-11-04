package pMedici.safeelements;

import java.util.Arrays;
import java.util.Vector;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.PathSearcher;
import org.colomoto.mddlib.operators.MDDBaseOperators;

import pMedici.threads.TestBuilder;
import pMedici.util.Operations;
import pMedici.util.Pair;
import pMedici.util.TupleConverter;

public class TestContext {
	
	public static int UNDEF = -1;
	public static boolean IN_TEST = false;
	
	/**
	 * The (partial) test containing the values already set
	 */
	int[] test;
	
	/**
	 * The starting node of its own MDD
	 */
	int mdd;
	
	/**
	 * Are the constraints present?
	 */
	boolean useConstraints;

	/**
	 *  Number of tuples covered by this test context 
	 */
	int nCovered;
	
	/**
	 * The MDD Manager
	 */
	MDDManager manager;
	
	/**
	 * The semaphore for managing a test context in a mutex manner
	 */
	public ExtendedSemaphore testMutex;
	
	/**
	 * Builds a new TestContext
	 * 
	 * @param startMdd: the identifier of the starting MDD (containing the constraints)
	 * @param nParams: the number of parameters of the combinatorial problem
	 * @param useConstraints: are constraints present?
	 * @param manager: the MDD Manager
	 */
	public TestContext(int startMdd, int nParams, boolean useConstraints, MDDManager manager) {
		this.useConstraints = useConstraints;
		this.mdd = startMdd;
		this.test = new int[nParams];
		this.nCovered = 0;
		this.manager = manager;
		this.testMutex = new ExtendedSemaphore();
		Arrays.fill(this.test, UNDEF);		
	}
	
	/**
	 * Checks whether the tuple given as parameter is coverable or not by the current MDD
	 * 
	 * @param tuple: the tuple to be checked
	 * @return true if the tuple is coverable, false otherwise
	 * @throws InterruptedException 
	 */
	public boolean isCoverable(Vector<Pair<Integer, Integer>> tuple) throws InterruptedException {
		// We must use a test context in a mutex mode
		if (!IN_TEST && !TestBuilder.LockTCOnlyOnWriting)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		// Checks using the test vector
		for (Pair<Integer, Integer> t : tuple) {
			int valueInTest = test[t.getFirst()];
			if (valueInTest != UNDEF) {
				// If the value is not undef, and differs from that in the tuple
				if (valueInTest != t.getSecond()) {
					return false;
				}
			}
		}
		
		// If the tuple is not compatible, then obviously is not coverable by the current TestContext
		if (!isCompatible(tuple, true)) {
			return false;
		}

		return true;
	}

	/**
	 * Checks whether the tuple given as parameter is compatible or not by the current MDD
	 * 
	 * @param tuple: the tuple to be checked
	 * @param skipFirstStep: true if the check with the vector has already been done
	 * @return true if the tuple is compatible, false otherwise
	 * @throws InterruptedException 
	 */
	private boolean isCompatible(Vector<Pair<Integer, Integer>> tuple, boolean skipFirstStep) throws InterruptedException {
		// We must use a test context in a mutex mode
		if (!IN_TEST && !TestBuilder.LockTCOnlyOnWriting)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		// First phase - Check without the MDD
		if (!skipFirstStep) {
			for (Pair<Integer, Integer> t : tuple) {
				int valueInTest = test[t.getFirst()];
				if (valueInTest != UNDEF) {
					// If the value is not undef, and differs from that in the tuple
					if (valueInTest != t.getSecond()) {
						return false;
					}
				}
			}
		}

		// Now, if constraints are available check with the MDD in order to verify if it is compatible even with the constraints
		// Otherwise, it is compatible by default
		if (!useConstraints)
			return true;

		return verifyWithMDD(tuple);
	}
	
	/**
	 * Checks whether the tuple given as parameter is compatible or not by the current test context without using the MDD
	 * 
	 * @param tuple: the tuple to be checked
	 * @return true if the tuple is compatible, false otherwise
	 */
	public boolean isCompatiblePartialCheck(Vector<Pair<Integer, Integer>> tuple) {
		// We must use a test context in a mutex mode
		if (!IN_TEST)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);	
		for (Pair<Integer, Integer> t : tuple) {
			int valueInTest = test[t.getFirst()];
			if (valueInTest != UNDEF) {
				// If the value is not undef, and differs from that in the tuple
				if (valueInTest != t.getSecond()) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Verify with the use of the MDD if the tuple is compatible with this test context
	 * 
	 * @param tuple: the tuple to be checked
	 * @return true if the tuple is compatible, false otherwise
	 * @throws InterruptedException 
	 */
	public boolean verifyWithMDD(Vector<Pair<Integer, Integer>> tuple) throws InterruptedException {
		// We must use a test context in a mutex mode
		if (!IN_TEST && !TestBuilder.LockTCOnlyOnWriting)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
				
		// Create an MDD representing the tuple
		TupleConverter tc = new TupleConverter(manager);
		int tupleMdd = tc.getMDDFromTuple(tuple);
		
		// Try computing the intersection (AND) 
		ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
		int intersection = MDDBaseOperators.AND.combine(manager, this.mdd, tupleMdd);
		ExtendedSemaphore.OPERATION_SEMAPHORE.release();
		
		// If the intersection is empty, then the tuple is not compatible, otherwise it is
		PathSearcher searcher = new PathSearcher(manager, 1);
		searcher.setNode(intersection);
		int nPaths = searcher.countPaths();
		
		return nPaths > 0;		
	}
	
	/**
	 * Returns the number of tuples covered by this test context
	 * 
	 * @return the number of tuples covered by this test context
	 */
	public int getNCovered() {
		return nCovered;
	}
	
	/**
	 * Checks if a tuple is implied (already contained in the partial test of the TestContext).
	 * It does not require the use of an MDD
	 * 
	 * @param tuple: the tuple to be checked
	 * @return true if the tuple is implied, false otherwise
	 */
	public boolean isImplied(Vector<Pair<Integer, Integer>> tuple) {
		// We must use a test context in a mutex mode
		if (!IN_TEST && !TestBuilder.LockTCOnlyOnWriting)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		// Check if it is implied
		for (Pair<Integer, Integer> t : tuple) {
			// If the values are not equal (undef or valid and different), it is not implied
			if (test[t.getFirst()] != t.getSecond()) {
				return false;
			}
		}
		
		nCovered++;
		return true;
	}
	
	/**
	 * This method adds a tuple to the partial test associated to the test context.
	 * The tuple to be added must be compatible with the partial test
	 * 
	 * @param tuple: the tuple to be added
	 * @throws InterruptedException 
	 */
	public boolean addTuple(Vector<Pair<Integer, Integer>> tuple) throws InterruptedException {
		return addTuple(tuple, true);
	}
	
	/**
	 * This method adds a tuple to the partial test associated to the test context.
	 * The tuple to be added must be compatible with the partial test
	 * 
	 * @param tuple: the tuple to be added
	 * @throws InterruptedException 
	 */
	public boolean addTuple(Vector<Pair<Integer, Integer>> tuple, boolean increaseCovered) throws InterruptedException {
		// If LockTCOnlyOnWriting, we try to acquire the mutex 
		if (TestBuilder.LockTCOnlyOnWriting) {
			if(this.testMutex.availablePermits() > 0) {
				if (!this.testMutex.tryAcquire())
					return false;
				else {
					// Verify that the tuple is still compatible
					if (!isCompatible(tuple, false)) {
						this.testMutex.release();
						return false;
					}
				}
			} else {
				return false;
			}
		}
		
		// We must use a test context in a mutex mode
		if (!IN_TEST)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		// Add the tuple to the partial test
		for (Pair<Integer, Integer> t : tuple) {
			test[t.getFirst()] = t.getSecond();
		}

		// Update the MDD, if constraints are available
		if (useConstraints) {
			updateMdd(tuple, increaseCovered);
		}
		else {
			if (increaseCovered)
				nCovered++;
		}
		
		if (TestBuilder.LockTCOnlyOnWriting) 
			this.testMutex.release();
		
		return true;
	}
	
	/**
	 * Updates the internal MDD structure by adding the new tuple
	 * 
	 * @param tuple: the tuple to be added
	 * @throws InterruptedException 
	 */
	private void updateMdd(Vector<Pair<Integer, Integer>> tuple, boolean increaseCovered) throws InterruptedException {
		// We must use a test context in a mutex mode
		if (!IN_TEST)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
				
		// Create an MDD representing the tuple
		TupleConverter tc = new TupleConverter(manager);
		int tupleMdd = tc.getMDDFromTuple(tuple);
		
		// Compute the intersection (AND) 
		ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
		this.mdd = MDDBaseOperators.AND.combine(manager, this.mdd, tupleMdd);
		ExtendedSemaphore.OPERATION_SEMAPHORE.release();
		
		// Now verify that the cardinality is still greater than 0
		PathSearcher searcher = new PathSearcher(manager, 1);
		searcher.setNode(this.mdd);
		assert (searcher.countPaths() > 0);
		
		if (increaseCovered)
			nCovered++;
	}
	
	/**
	 * Returns the test derived from this test context
	 * 
	 * @param printVector: if true the test in the vector is printed (to be used when no constraints are available), otherwise the test is extracted from the MDD
	 * @return the string representing the test derived from this test context
	 */
	public String getTest(boolean printVector) {
		String res = "";
		
		if (printVector || !useConstraints || isComplete()) {
			for (int i : test)
				res += i + " ";
		} else {
			for (int i : Operations.getPathInMDD(this.mdd, manager, this.test)) {
				res += i + " ";
			}
		}
		
		return res;
	}
	
	/**
	 * Checks whether the test is complete or not
	 * 
	 * @return true if the test is complete, false otherwise
	 */
	private boolean isComplete() {
		for (int i : test)
			if (i == UNDEF)
				return false;
		return true;
	}
	
	/**
	 * Count the number of paths
	 * 
	 * @return the number of paths of the MDD. The higher is the number of paths, the higher is the cardinality
	 */
	public int getNPaths() {
		PathSearcher searcher = new PathSearcher(manager, 1);
		searcher.setNode(this.mdd);
		return searcher.countPaths();
	}
	
	/**
	 * Count the number of paths after the addition of a tuple
	 * 
	 * @param tuple: the tuple to be added
	 * @return the number of paths of the MDD. The higher is the number of paths, the higher is the cardinality
	 * @throws InterruptedException 
	 */
	public int getNPaths(Vector<Pair<Integer, Integer>> tuple) throws InterruptedException {
		// Create an MDD representing the tuple
		TupleConverter tc = new TupleConverter(manager);
		int tupleMdd = tc.getMDDFromTuple(tuple);
		
		// Try computing the intersection (AND) 
		ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
		int intersection = MDDBaseOperators.AND.combine(manager, this.mdd, tupleMdd);
		ExtendedSemaphore.OPERATION_SEMAPHORE.release();
		
		// If the intersection is empty, then the tuple is not compatible, otherwise it is
		PathSearcher searcher = new PathSearcher(manager, 1);
		searcher.setNode(intersection);
		return searcher.countPaths();
	}
	
	/**
	 * Returns the current MDD 
	 * 
	 * @return the current MDD 
	 */
	public int getMDD() {
		return mdd;
	}
}