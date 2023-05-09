package pMedici.threads;

import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.colomoto.mddlib.MDDManager;
import org.eclipse.emf.common.util.EList;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import pMedici.main.PMedici;
import pMedici.main.PMediciPlusMT;
import pMedici.util.ExtendedSemaphore;
import pMedici.util.Pair;
import pMedici.util.TestContext;
import pMedici.util.TestModel;

/**
 * Thread that fill the initial test context list of the new model with all
 * the valid test cases from the old test suite
 * 
 * @author Luca Parimbelli
 *
 */
public class TestEarlyFiller implements Runnable {
	
	Logger logger = LogManager.getRootLogger();
	

	/**
	 * The {@link Vector} of the imported test cases from CSV
	 */
	Vector<Map<String, String>> oldTests;

	/**
	 * The mutex semaphore for interacting in a safe way with the {@link Vector} of
	 * the imported old test suite
	 */
	ExtendedSemaphore oldTestsMutex;

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
	public TestEarlyFiller(Vector<Map<String, String>> oldTests, ExtendedSemaphore oldTestsMutex,
			Vector<TestContext> tcList, ExtendedSemaphore tcListMutex, CitModel model, TestModel m, MDDManager manager,
			int baseMDD) {
		this.oldTests = oldTests;
		this.oldTestsMutex = oldTestsMutex;
		this.tcListMutex = tcListMutex;
		this.tcList = tcList;
		this.model = model;
		this.m = m;
		this.manager = manager;
		this.baseMDD = baseMDD;
	}

	@Override
	public void run() {

		while (true) {

			try {
				oldTestsMutex.acquire();
				
				// if the Vector of the imported test suite is empty, the thread is stopped
				if (oldTests.isEmpty()) {
					oldTestsMutex.release();
					break;
				}
				
				// otherwise we take the last test and we insert it in the queue
				else {
					// Extracting the last element
					Map<String, String> oldTest = oldTests.lastElement();
					oldTests.remove((oldTests.size() - 1));
					oldTestsMutex.release();				

						/* Debug code */
						if (logger.getLevel().isGreaterOrEqual(Level.DEBUG)) {
							System.out.println("--------------------");
							System.out.println("Current thread ID: " + Thread.currentThread().getId());
							oldTest.forEach((name, value) -> {
								System.out.print(name + ":" + value + ", ");
							});
							System.out.println("\n--------------------");
						}

						// Creating the tuple related to the current iteration
						// we need to create a tuple because the method that verify
						// the constraints accepts only the type Vector<Pair<Integer, Integer>>
						Vector<Pair<Integer, Integer>> tuple = new Vector<Pair<Integer, Integer>>();
						Vector<Pair<Integer, Integer>> tupleNew = new Vector<Pair<Integer, Integer>>();

						// New test context
						TestContext tc = new TestContext(baseMDD, m.getnParams(), m.getUseConstraints(), manager);
						
						EList<Parameter> parameters = model.getParameters();
						for (int tupleIndex = 0; tupleIndex < parameters.size(); tupleIndex++) {
							Parameter param = parameters.get(tupleIndex);
							// if the parameter of the new model is in the old test suite,
							// its value is added in the corresponding position in the current tuple
							String testParamValue;
							if ((testParamValue = oldTest.get(param.getName())) != null) {
								
								// since we imposed that values must be all boolean, we have only 0="false" or
								// 1="true"
								assert ( testParamValue.equals("true") || testParamValue.equals("false") ) : "model parameters must be boolean";
								
								tupleNew.add(
										new Pair<Integer, Integer>(tupleIndex, testParamValue.equals("true") ? 1 : 0));
							
								// If also partial tests should be kept, verify assignment per assignment
								if (!tupleNew.isEmpty() && TestBuilder.KeepPartialOldTests) {
									if (tc.verifyWithMDD(tupleNew)) { 
										tc.addTuple(tupleNew);
										tuple = tupleNew;
									}
								}
							}

						}

						// If we added at least one parameter test value to the tuple, then
						// we check if the created tuple is valid with the model constraints
						if (!tuple.isEmpty()) {
							
							// If the test context has already been updated step by step, skip the part
							// adding the new tuple
							if (!TestBuilder.KeepPartialOldTests) {
							
								// If the tuple is compatible with the constraints, then we add the
								// add the tuple to the current test context and we add the
								// current test context to the list of all the test contexts from
								// which the algorithm of medici will later start executing
	
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
								
							} else {
								// Adding the tc to the shared list of all the test context tcList in a safe way
								this.tcListMutex.acquire();
								tcList.add(tc);
								this.tcListMutex.release();
							}
					}
				}
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}

	}


}
