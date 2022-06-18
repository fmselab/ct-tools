package pMedici.threads;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.colomoto.mddlib.MDDManager;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import pMedici.main.PMediciPlusMT;
import pMedici.safeelements.ExtendedSemaphore;
import pMedici.safeelements.TestContext;
import pMedici.util.Pair;
import pMedici.util.TestModel;

public class TestEarlyFiller implements Runnable {

	/**
	 * The safe queue from which the old tests are extracted
	 */
	ConcurrentLinkedQueue<Map<String, String>> oldTestsQueue;

	/**
	 * The mutex semaphore for interacting in a safe way with the shared list of
	 * test contexts
	 */
	ExtendedSemaphore tcListMutex;

	/**
	 * The shared {@link Vector} that contains all the test contexts
	 */
	Vector<TestContext> tcList;

	/**
	 * The {@link CitModel} of the evolved model
	 */
	CitModel model;

	/**
	 * The manager element for dealing with MDDs
	 */
	MDDManager manager;

	/**
	 * The evolved model in medici format
	 */
	TestModel m;

	/**
	 * The baseMDD with constraints
	 */
	int baseMDD;

	/**
	 * Creates a new thread that fills the list of test contexts with test cases of
	 * the old test suite which are compatible with the new model
	 * 
	 */
	public TestEarlyFiller(ConcurrentLinkedQueue<Map<String, String>> oldTestsQueue, ExtendedSemaphore tcListMutex,
			Vector<TestContext> tcList, CitModel model, TestModel m, MDDManager manager, int baseMDD) {
		this.oldTestsQueue = oldTestsQueue;
		this.tcListMutex = tcListMutex;
		this.tcList = tcList;
		this.model = model;
		this.m = m;
		this.manager = manager;
		this.baseMDD = baseMDD;
	}

	@Override
	public void run() {
		Map<String, String> oldTest;
		while ( ( oldTest = oldTestsQueue.poll()) !=null ) {

			/* Debug code */
			if (PMediciPlusMT.PRINT_DEBUG) {
				System.out.println("--------------------");
				System.out.println("Current thread ID: " + Thread.currentThread().getId());
				oldTest.forEach((name, value) -> {
					System.out.print(name + ":" + value + ", ");
				});
				System.out.println("\n--------------------");
			}

			// Creating the tuple related to the current iteration
			Vector<Pair<Integer, Integer>> tuple = new Vector<Pair<Integer, Integer>>();
			int tupleIndex = 0;

			for (Parameter param : model.getParameters()) {

				/* Debug code */
				if (PMediciPlusMT.PRINT_DEBUG) {
					System.out.println("Parametro iterazione [" + tupleIndex + "]: " + param.getName());
				}

				// if the parameter of the new model is in the old test suite,
				// its value is added in the corresponding position in the current tuple
				String testParamValue;
				if ((testParamValue = oldTest.get(param.getName())) != null) {
					// since we imposed that values must be all boolean, we have only 0="false" or
					// 1="true"
					tuple.add(new Pair<Integer, Integer>(tupleIndex, testParamValue.equals("true") ? 1 : 0));
				}

				tupleIndex++;
			}

			// If we added at least one parameter test value to the tuple, then
			// we check if the created tuple is valid with the model constraints
			if (!tuple.isEmpty()) {
				TestContext tc = new TestContext(baseMDD, m.getnParams(), m.getUseConstraints(), manager);

				// If the tuple is compatible with the constraints, then we add the
				// add the tuple to the current test context and then we add the
				// current test context to the list of all the test context from
				// which the algorithm of medici will start executing

				/* Debug code */
				if (PMediciPlusMT.PRINT_DEBUG) {
					try {
						System.out.println("Verifica constraints: " + tc.verifyWithMDD(tuple));
					} catch (InterruptedException e) {
						System.out.println(e.getMessage());
					}
				}

				try {
					if (tc.verifyWithMDD(tuple)) {

						// Adding the tuple to the current test context
						// Notice: this method also update the mdd of the text context
						tc.addTuple(tuple);

						// Adding the tc to the shared list of all the test context tcList in a safe way
						this.tcListMutex.acquire();
						tcList.add(tc);
						this.tcListMutex.release();

					}
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
			}

		}

	}

}
