package mantra.safeelements;

import java.util.Vector;

import org.pf4j.ExtensionPoint;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.util.Pair;
import mantra.model.Model;

public interface TestContext extends Comparable<TestContext>, ExtensionPoint {

	/**
	 * Init a new TestContext
	 * 
	 * @param nParams: the number of parameters of the combinatorial problem
	 * @param useConstraints: are constraints present?
	 * @param model: the Model
	 */
	void init(Model model, int nParam, boolean useConstraints);

	/**
	 * Checks if a tuple is implied (already contained in the partial test of the TestContext).
	 * It does not require the use of a SAT Solver
	 * 
	 * @param tuple: the tuple to be checked
	 * @return true if the tuple is implied, false otherwise
	 */
	boolean isImplied(Vector<Pair<Object, Object>> tuple);

	/**
	 * Checks whether the tuple given as parameter is coverable or not by the current context
	 * 
	 * @param tuple: the tuple to be checked
	 * @return true if the tuple is coverable, false otherwise
	 * @throws InterruptedException 
	 * @throws SolverException 
	 */
	boolean isCoverable(Vector<Pair<Object, Object>> tuple) throws InterruptedException, SolverException;

	/**
	 * Checks whether the tuple given as parameter is compatible or not by the current test context without using the SAT Solver
	 * 
	 * @param tuple: the tuple to be checked
	 * @return true if the tuple is compatible, false otherwise
	 */
	boolean isCompatiblePartialCheck(Vector<Pair<Object, Object>> tuple);

	/**
	 * This method adds a tuple to the partial test associated to the test context.
	 * The tuple to be added must be compatible with the partial test
	 * 
	 * @param tuple: the tuple to be added
	 * @return true if the tuple has been added, false otherwise
	 * @throws InterruptedException 
	 * @throws SolverException 
	 */
	boolean addTuple(Vector<Pair<Object, Object>> tuple) throws InterruptedException;

	/**
	 * Returns the semaphore owned by this test context
	 * 
	 * @return the semaphore owned by this test context
	 */
	ExtendedSemaphore getTestMutex();

	/**
	 * Returns the number of tuples covered by this test context
	 * 
	 * @return the number of tuples covered by this test context
	 */
	int getNCovered();

	/**
	 * Returns the test derived from this test context
	 * 
	 * @param printVector: if true the test in the vector is printed (to be used when no constraints are available), otherwise the test is extracted from the context
	 * @return the string representing the test derived from this test context
	 * @throws InterruptedException 
	 * @throws SolverException 
	 */
	String getTest(boolean printB) throws InterruptedException, SolverException;
	
	/**
	 * Closes the SMT logical context
	 */
	void close();
	
	boolean mustLockOnReadForFindCompatible();
	
	boolean mustTryAcquireForFindImplies();
}
