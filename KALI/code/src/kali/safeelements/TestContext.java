package kali.safeelements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import org.sosy_lab.common.NativeLibraries;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.Model.ValueAssignment;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.util.Pair;
import kali.threads.TestBuilder;
import kali.util.Operations;
import kali.util.TestCase;
import kali.util.TupleConverter;

public class TestContext {
	
	public static String UNDEF = "*";
	
	/**
	 *  The smt solver to be used
	 */
	public static Solvers SMTSolver = Solvers.SMTINTERPOL;

	/**
	 * The configuration to be used by the SMT solver 
	 */
	public static Configuration config =Configuration.defaultConfiguration();
	
	/**
	 * The (partial) test containing the values already set
	 */
	Object[] test;
	
	/**
	 * Are the constraints present?
	 */
	boolean useConstraints;

	/**
	 *  Number of tuples covered by this test context 
	 */
	int nCovered;
	
	/**
	 * The current boolean formula
	 */
	BooleanFormula currentFormula;
	
	/**
	 * The context
	 */
	SolverContext context;
	
	/**
	 * The semaphore for managing a test context in a mutex manner
	 */
	public ExtendedSemaphore testMutex;
	
	/**
	 * The structure mapping the parameter on its position
	 */
	Map<String, Integer> paramPosition;
	
	/**
	 * The Cit Model
	 */
	CitModel model;
	
	/**
	 * Support structures to maintain the elements and the variables mapping
	 */
	HashMap<Parameter, List<Formula>> variablesList;
	
	/**
	 * The Map storing each parameter and the corresponding formulas (one for each value of the enumerative)
	 */
	public Map<Parameter, Map<String, Formula>> declaredTypes = new HashMap<>();
	
	/**
	 * Builds a new TestContext
	 * 
	 * @param nParams: the number of parameters of the combinatorial problem
	 * @param useConstraints: are constraints present?
	 * @param paramPosition: structure mapping the parameter on its position
	 * @param model: the Cit Model
	 * @throws InvalidConfigurationException 
	 */
	public TestContext(int nParams, boolean useConstraints, Map<String, Integer> paramPosition, CitModel model) throws InvalidConfigurationException {
		variablesList = new HashMap<Parameter,List<Formula>>();
		this.useConstraints = useConstraints;
		this.test = new Object[nParams];
		this.nCovered = 0;
		this.context = SolverContextFactory.createSolverContext(
				config,
	            LogManager.createNullLogManager(),
	            ShutdownNotifier.createDummy(),
	            SMTSolver,
	            NativeLibraries::loadLibrary);
		this.model = model;
		this.currentFormula = setupContext();
		this.testMutex = new ExtendedSemaphore();
		Arrays.fill(this.test, UNDEF);
		this.paramPosition = paramPosition;
	}
	
	/**
	 * Checks whether the tuple given as parameter is coverable or not by the current context
	 * 
	 * @param tuple: the tuple to be checked
	 * @return true if the tuple is coverable, false otherwise
	 * @throws InterruptedException 
	 * @throws SolverException 
	 */
	public boolean isCoverable(Vector<Pair<String, Object>> tuple) throws InterruptedException, SolverException {
		// We must use a test context in a mutex mode
		assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		// Checks using the test vector
		for (Pair<String, Object> t : tuple) {
			Object valueInTest = test[this.paramPosition.get(t.getFirst())];
			if (!valueInTest.equals(UNDEF)) {
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
	 * Checks whether the tuple given as parameter is compatible or not by the current context
	 * 
	 * @param tuple: the tuple to be checked
	 * @param skipFirstStep: true if the check with the vector has already been done
	 * @return true if the tuple is compatible, false otherwise
	 * @throws InterruptedException 
	 * @throws SolverException 
	 */
	private boolean isCompatible(Vector<Pair<String, Object>> tuple, boolean skipFirstStep) throws InterruptedException, SolverException {
		// We must use a test context in a mutex mode
		assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		// First phase - Check without the SAT Solver
		if (!skipFirstStep) {
			for (Pair<String, Object> t : tuple) {
				Object valueInTest = test[this.paramPosition.get(t.getFirst())];
				if (!valueInTest.equals(UNDEF)) {
					// If the value is not undef, and differs from that in the tuple
					if (valueInTest != t.getSecond()) {
						return false;
					}
				}
			}
		}

		// Now, if constraints are available check with the SAT in order to verify if it is compatible even with the constraints
		// Otherwise, it is compatible by default
		if (!useConstraints)
			return true;

		return verifyWithSAT(tuple);
	}
	
	/**
	 * Checks whether the tuple given as parameter is compatible or not by the current test context without using the SAT Solver
	 * 
	 * @param tuple: the tuple to be checked
	 * @return true if the tuple is compatible, false otherwise
	 */
	public boolean isCompatiblePartialCheck(Vector<Pair<String, Object>> tuple) {
		// We must use a test context in a mutex mode
		assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		for (Pair<String, Object> t : tuple) {
			Object valueInTest = test[this.paramPosition.get(t.getFirst())];
			if (!valueInTest.equals(UNDEF)) {
				// If the value is not undef, and differs from that in the tuple
				if (valueInTest != t.getSecond()) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Verify with the use of the SAT Solver if the tuple is compatible with this test context
	 * 
	 * @param tuple: the tuple to be checked
	 * @return true if the tuple is compatible, false otherwise
	 * @throws InterruptedException 
	 * @throws SolverException 
	 */
	private boolean verifyWithSAT(Vector<Pair<String, Object>> tuple) throws InterruptedException, SolverException {
		// We must use a test context in a mutex mode
		assert (this.testMutex.lockedByCaller() || nCovered == 0);
				
		// Create a formula representing the tuple
		BooleanFormula tupleFormula = getFormulaFromTuple(tuple);
		
		// Try computing the intersection (AND)
		ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_UNSAT_CORE);
		prover.addConstraint(context.getFormulaManager().getBooleanFormulaManager().and(currentFormula, tupleFormula));
		
		// If the context is not SAT, it means that the tuple can't be added to this context
		return !prover.isUnsat();		
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
	 * It does not require the use of a SAT Solver
	 * 
	 * @param tuple: the tuple to be checked
	 * @return true if the tuple is implied, false otherwise
	 */
	public boolean isImplied(Vector<Pair<String, Object>> tuple) {
		// We must use a test context in a mutex mode
		if (!TestBuilder.LockTCOnlyOnWriting)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		// Check if it is implied
		for (Pair<String, Object> t : tuple) {
			// If the values are not equal (undef or valid and different), it is not implied
			if (test[this.paramPosition.get(t.getFirst())] != t.getSecond()) {
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
	 * @throws SolverException 
	 */
	public void addTuple(Vector<Pair<String, Object>> tuple) throws InterruptedException, SolverException {
		// We must use a test context in a mutex mode
		assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		// Add the tuple to the partial test
		for (Pair<String, Object> t : tuple) {
			test[this.paramPosition.get(t.getFirst())] = t.getSecond();
		}

		// Update the context, if constraints are available
		if (useConstraints) {
			updateContext(tuple);
		}
		else {
			nCovered++;
		}
	}

	/**
	 * Updates the internal context structure by adding the new tuple
	 * 
	 * @param tuple: the tuple to be added
	 * @throws InterruptedException 
	 * @throws SolverException 
	 */
	private void updateContext(Vector<Pair<String, Object>> tuple) throws InterruptedException, SolverException {
		// We must use a test context in a mutex mode
		assert (this.testMutex.lockedByCaller() || nCovered == 0);
				
		// Create a formula representing the tuple
		BooleanFormula tupleFormula = getFormulaFromTuple(tuple);
		
		// Add the formula to the context in order to cover the new tuple
		currentFormula = context.getFormulaManager().getBooleanFormulaManager().and(currentFormula, tupleFormula);
		
		// Create the new prover
		ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_UNSAT_CORE);
		prover.addConstraint(currentFormula);
		
		// Now verify that the cardinality is still greater than 0
		assert (!prover.isUnsat());
		
		nCovered++;
	}
	
	/**
	 * Returns the test derived from this test context
	 * 
	 * @param printVector: if true the test in the vector is printed (to be used when no constraints are available), otherwise the test is extracted from the context
	 * @return the string representing the test derived from this test context
	 * @throws InterruptedException 
	 * @throws SolverException 
	 */
	public String getTest(boolean printVector) throws InterruptedException, SolverException {
		String res = "";
		//res = "[ ";
		
		if (printVector || !useConstraints || isComplete()) {
			for (Object i : test)
				res += i.toString() + ";";
		} else {
			// Print the assignments given by the SMT Context
			ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS);
			prover.addConstraint(currentFormula);
			if (!prover.isUnsat()) {
				Model proverModel = prover.getModel();
				// We need to sort the parameters and extract only values 
				List<ValueAssignment> finalTest = proverModel.asList().stream().collect(Collectors.toList());
				List<TestCase> filteredFinalTest = new ArrayList<TestCase>();
				
				// First, if enumeratives are present, only those TRUE must be retained
				for (ValueAssignment va : finalTest) {
					String paramName = Operations.getCorrespondingKey(va.getName(), paramPosition);
					if (!paramPosition.containsKey(va.getName()) && paramName != null) {
						if (va.getValue().equals(true)) {
							String value = va.getName().toString().substring(paramName.length()+1);
							filteredFinalTest.add(new TestCase(paramName, value));
						}
					} else {
						filteredFinalTest.add(new TestCase(va.getName(), va.getValue()));
					}
				}
				
				Collections.sort(filteredFinalTest, new Comparator<TestCase>() {
					@Override
					public int compare(TestCase o1, TestCase o2) {
						return paramPosition.get(o1.getParamName()).compareTo(paramPosition.get(o2.getParamName()));
					}				
				});
				
				for (TestCase va : filteredFinalTest) {
					res += va.getValue() + ";";
				}
			}
			else {
				throw new RuntimeException("The context is UNSAT");
			}
		}
		
		//res += " --> T]";
		
		return res.substring(0, res.length()-1);
	}
	
	/**
	 * Checks whether the test is complete or not
	 * 
	 * @return true if the test is complete, false otherwise
	 */
	private boolean isComplete() {
		for (Object i : test)
			if (i.equals(UNDEF))
				return false;
		return true;
	}
	
	/**
	 * Updates the context with all the parameters of the combinatorial model, and creates a boolean formula representing
	 * the conjunction of all the constraints

	 * @return the boolean formula representing the conjunction of all the constraints
	 * @throws InvalidConfigurationException
	 */
	private BooleanFormula setupContext() throws InvalidConfigurationException {		
		// The formula representing the constraints		
		return Operations.createCtxFromModel(model, this, variablesList);
	}

	/**
	 * Returns the model
	 * 
	 * @return the model
	 */
	public CitModel getModel() {
		return model;
	}
	
	/**
	 * Convert the tuple in a boolean formula
	 * 
	 * @param tuple: the tuple to be converted
	 * @return: the BooleanFormula corresponding to the tuple
	 */
	public BooleanFormula getFormulaFromTuple(Vector<Pair<String, Object>> tuple) {
		
		Map<Parameter, String> tupleMap = new HashMap<Parameter, String>();
		tuple.forEach((x) -> {
			tupleMap.put(model.getParameters().get(this.paramPosition.get(x.getFirst())), x.getSecond().toString());
		}); 
		
		return TupleConverter.extractFormulaFromTuple(this, variablesList, tupleMap);
	}
	
	/**
	 * Return the completeness grade (number of elements not yet assigned). Used when sorting TestContexts
	 * 
	 * @return the completeness grade of the test context
	 */
	public int getCompletenessGrade() {
		assert (this.testMutex.lockedByCaller() || nCovered == 0);
		int counter = 0;
		for (Object i : test)
			if (i.equals(UNDEF))
				counter++;
		return counter;
	}
	
	/**
	 * Get the current logical context
	 * 
	 * @return the current logical context
	 */
	public SolverContext getContext() {
		return this.context;
	}
	
	/**
	 * Closes the SMT logical context
	 */
	public void close(){
		context.close();
	}
	
}
