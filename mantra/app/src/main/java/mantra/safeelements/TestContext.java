package mantra.safeelements;

import java.util.Vector;

import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.util.Pair;
import mantra.model.Model;

public interface TestContext extends Comparable<TestContext> {

	void init(Model model, int nParam, boolean useConstraints);

	void close();
	
	boolean isImplied(Vector<Pair<Object, Object>> tuple);

	boolean isCoverable(Vector<Pair<Object, Object>> tuple) throws InterruptedException, SolverException;

	boolean isCompatiblePartialCheck(Vector<Pair<Object, Object>> tuple);

	boolean addTuple(Vector<Pair<Object, Object>> tuple);
	
	ExtendedSemaphore getTestMutex();

	int getNCovered();

	String getTest(boolean b);

}