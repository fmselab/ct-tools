package mantra.kali;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.pf4j.Extension;
import org.sosy_lab.common.NativeLibraries;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverException;
import org.sosy_lab.java_smt.api.Model.ValueAssignment;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Constraint;
import ctwedge.ctWedge.Enumerative;
import ctwedge.ctWedge.Parameter;
import ctwedge.util.Pair;
import mantra.kali.util.ConstraintTranslator;
import mantra.kali.util.ParameterAdder;
import mantra.kali.util.TupleConverter;
import mantra.model.Model;
import mantra.safeelements.ExtendedSemaphore;
import mantra.safeelements.TestContext;
import mantra.threads.TestBuilder;

@Extension
public class KaliTestContext implements TestContext{
	
	public static String UNDEF = "*";
	
	public static Solvers SMTSolver = Solvers.SMTINTERPOL;

	public static Configuration config =Configuration.defaultConfiguration();
	
	Object[] test;
	
	boolean useConstraints;
	
	int nCovered;
	
	BooleanFormula currentFormula;
	
	SolverContext context;
	
	public ExtendedSemaphore testMutex;
	
	Map<String, Integer> paramPosition;
	
	KaliModel model;

	HashMap<Parameter, List<Formula>> variablesList;
	
	public Map<Parameter, Map<String, Formula>> declaredTypes = new HashMap<>();
	
	ProverEnvironment prover;
	
	Integer completenessGrades;
	
	
	@Override
	public int compareTo(TestContext o) {
		KaliTestContext t = (KaliTestContext) o;
		return this.getCompletenessGrade() - t.getCompletenessGrade();
	}

	@Override
	public void init(Model model, int nParam, boolean useConstraints) {
		try {
			variablesList = new HashMap<Parameter,List<Formula>>();
			this.useConstraints = useConstraints;
			this.test = new Object[nParam];
			this.nCovered = 0;
			this.context = SolverContextFactory.createSolverContext(
					config,
			        LogManager.createNullLogManager(),
			        ShutdownNotifier.createDummy(),
			        SMTSolver,
			        NativeLibraries::loadLibrary);
			if(model instanceof KaliModel)
				this.model = (KaliModel) model;
			this.currentFormula = setupContext();
			this.testMutex = new ExtendedSemaphore();
			Arrays.fill(this.test, UNDEF);
			this.paramPosition = setParamPosition(this.model.citModel);
			this.prover = context.newProverEnvironment(ProverOptions.GENERATE_UNSAT_CORE);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void close() {
		context.close();
	}

	@Override
	public boolean isImplied(Vector<Pair<Object, Object>> tuple) {
		// We must use a test context in a mutex mode
		if (!TestBuilder.LockTCOnlyOnWriting && !TestBuilder.IN_TEST)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		// Check if it is implied
		for (Pair<Object, Object> t : tuple) {
			// If the values are not equal (undef or valid and different), it is not implied
			if (test[this.paramPosition.get(t.getFirst())] != t.getSecond()) {
				return false;
			}
		}
		
		nCovered++;
		return true;
	}

	@Override
	public boolean isCoverable(Vector<Pair<Object, Object>> tuple) throws InterruptedException, SolverException {
		// We must use a test context in a mutex mode
		if (!TestBuilder.IN_TEST)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		// Checks using the test vector
		for (Pair<Object, Object> t : tuple) {
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

	@Override
	public boolean isCompatiblePartialCheck(Vector<Pair<Object, Object>> tuple) {
		// We must use a test context in a mutex mode
		if (!TestBuilder.LockTCOnlyOnWriting && !TestBuilder.IN_TEST)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		for (Pair<Object, Object> t : tuple) {
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

	@Override
	public boolean addTuple(Vector<Pair<Object, Object>> tuple) {
		boolean added = true;
		try {
			
			// We must use a test context in a mutex mode
			if (!TestBuilder.IN_TEST)
				assert (this.testMutex.lockedByCaller() || nCovered == 0);
			
			// Add the tuple to the partial test
			for (Pair<Object, Object> t : tuple) {
				test[this.paramPosition.get(t.getFirst())] = t.getSecond();
			}

			// Update the context, if constraints are available
			if (useConstraints) {
				added = updateContext(tuple);
			}
			else {
				nCovered++;
			}
			
		} catch (InterruptedException e) {
			added = false;
			e.printStackTrace();
		} catch (SolverException e) {
			added = false;
			e.printStackTrace();
		}
		this.completenessGrades = getCompletenessGrade();
		return added;
	}

	@Override
	public ExtendedSemaphore getTestMutex() {
		return this.testMutex;
	}

	@Override
	public int getNCovered() {
		return this.nCovered;
	}

	@Override
	public String getTest(boolean printVector) throws InterruptedException, SolverException {
		String res = "";
		
		if (printVector || !useConstraints || isComplete()) {
			for (Object i : test)
				res += i.toString() + ";";
		} else {
			// Print the assignments given by the SMT Context
			ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS);
			prover.addConstraint(currentFormula);
			
			if (prover.isUnsat()) 
				throw new RuntimeException("The context is UNSAT");
			
			org.sosy_lab.java_smt.api.Model proverModel = prover.getModel();
			// We need to sort the parameters and extract only values 
			List<ValueAssignment> finalTest = proverModel.asList().stream().collect(Collectors.toList());
			List<Pair<String, Object>> filteredFinalTest = new ArrayList<Pair<String, Object>>();
			
			// First, if enumeratives are present, only those TRUE must be retained
			for (ValueAssignment va : finalTest) {
				String paramName = getCorrespondingKey(va.getName(), paramPosition);
				if (!paramPosition.containsKey(va.getName()) && paramName != null) {
					if (va.getValue().equals(true)) {
						String value = va.getName().toString().substring(paramName.length()+1);
						filteredFinalTest.add(new Pair<String, Object>(paramName, value));
					}
				} else {
					filteredFinalTest.add(new Pair<String, Object>(va.getName(), va.getValue()));
				}
			}
			
			Collections.sort(filteredFinalTest, new Comparator<Pair<String, Object>>() {
				@Override
				public int compare(Pair<String, Object> o1, Pair<String, Object> o2) {
					return paramPosition.get(o1.getFirst()).compareTo(paramPosition.get(o2.getFirst()));
				}				
			});
			
			for (Pair<String, Object> va : filteredFinalTest) {
				res += va.getSecond() + ";";
			}
		}
		
		//res += " --> T]";
		
		return res.substring(0, res.length()-1);
	}

	public BooleanFormula getFormulaFromTuple(Vector<Pair<Object, Object>> tuple) {
		
		Map<Parameter, String> tupleMap = new HashMap<Parameter, String>();
		tuple.forEach((x) -> {
			tupleMap.put(model.getParameters().get(this.paramPosition.get(x.getFirst())), x.getSecond().toString());
		}); 
		
		return TupleConverter.extractFormulaFromTuple(this, variablesList, tupleMap);
	}

	public SolverContext getContext() {
		return this.context;
	}
	
	public int getCompletenessGrade() {
		if (!TestBuilder.IN_TEST)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
		int counter = 0;
		for (Object i : test)
			if (i.equals(UNDEF))
				counter++;
		return counter;
	}
	
	// PRIVATE METHODS
	
	private BooleanFormula setupContext() throws InvalidConfigurationException {		
		// The formula representing the constraints		
		return createCtxFromModel(model, this, variablesList);
	}
	
	
	private boolean isCompatible(Vector<Pair<Object, Object>> tuple, boolean skipFirstStep) throws InterruptedException, SolverException {
		// We must use a test context in a mutex mode
		if (!TestBuilder.IN_TEST)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
		
		// First phase - Check without the SAT Solver
		if (!skipFirstStep) {
			for (Pair<Object, Object> t : tuple) {
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
	
	private boolean verifyWithSAT(Vector<Pair<Object, Object>> tuple) throws InterruptedException, SolverException {
		// We must use a test context in a mutex mode
		if (!TestBuilder.IN_TEST)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
				
		// Create a formula representing the tuple
		BooleanFormula tupleFormula = getFormulaFromTuple(tuple);
		
		// Try computing the intersection (AND)
		ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_UNSAT_CORE);
		prover.addConstraint(context.getFormulaManager().getBooleanFormulaManager().and(currentFormula, tupleFormula));
		
		// If the context is not SAT, it means that the tuple can't be added to this context
		boolean unsat = prover.isUnsat();
		return !unsat;		
	}
	
	private boolean updateContext(Vector<Pair<Object, Object>> tuple) throws InterruptedException, SolverException {
		// We must use a test context in a mutex mode
		if (!TestBuilder.IN_TEST)
			assert (this.testMutex.lockedByCaller() || nCovered == 0);
				
		// Create a formula representing the tuple
		BooleanFormula tupleFormula = getFormulaFromTuple(tuple);
		
		// Add the formula to the context in order to cover the new tuple
		currentFormula = context.getFormulaManager().getBooleanFormulaManager().and(currentFormula, tupleFormula);
		
		// Create the new prover
		ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_UNSAT_CORE);
		prover.addConstraint(currentFormula);
		
		// Now verify that the cardinality is still greater than 0
		if(prover.isUnsat()) {
			return false;
		}

		nCovered++;
		return true;
	}
	
	private boolean isComplete() {
		for (Object i : test)
			if (i.equals(UNDEF))
				return false;
		return true;
	}
	
	//STATIC METHODS FOR OPERATIONS
	
	private static BooleanFormula createCtxFromModel(KaliModel model, KaliTestContext ctx, Map<Parameter, List<Formula>> variables) {
		// Add all the parameters to the new CTX
		addParameters(model, ctx, variables);
		
		// Add constraints when more formulas are available for a single parameter (it happens only in case of Enumeratives)
		// Then, Translate all the constraints and add them to the context
		BooleanFormula constraints = addConstraintsForEnumeratives(model, ctx, variables);
		for (Constraint r : model.getConstraints()) {
			ConstraintTranslator translator = new ConstraintTranslator(ctx, variables);
			Formula constraint = translator.doSwitch(r);
			
			assert constraint instanceof BooleanFormula : "Constraints must be boolean";
			
			constraints = ctx.getContext().getFormulaManager().getBooleanFormulaManager().and(constraints, (BooleanFormula)constraint);
		}
		
		return constraints;
	}
	
	private static void addParameters(Model model, KaliTestContext ctx, Map<Parameter, List<Formula>> variables) {
		// Add all the parameters to the logical context
		ParameterAdder pa = new ParameterAdder(ctx);
		
		for (Parameter nt : model.getParameters()) {
			List<Formula> variable = pa.doSwitch(nt);
			variables.put(nt, variable);
		}
	}
	
	private static BooleanFormula addConstraintsForEnumeratives(Model model, KaliTestContext ctx, Map<Parameter, List<Formula>> variables) {
		SolverContext sContext = ctx.getContext();
		
		BooleanFormula res = sContext.getFormulaManager().getBooleanFormulaManager().makeTrue();
		for (Parameter p : model.getParameters()) {	
			// Only if it is an enumerative
			if (p instanceof Enumerative) {
				
				List<Formula> list = variables.get(p);
				BooleanFormula constraint = sContext.getFormulaManager().getBooleanFormulaManager().makeTrue();
				
				// If the size is greater than 1, only one value per time can be true
				if (list.size() > 1) {
					for (Formula f : list) {
						BooleanFormula subFormula = sContext.getFormulaManager().getBooleanFormulaManager().makeTrue();
						for (Formula f1 : list) {
							if (!f.equals(f1)) {
								// AND Between all the NOT of the elements
								subFormula = sContext.getFormulaManager().getBooleanFormulaManager().and(subFormula, 
										sContext.getFormulaManager().getBooleanFormulaManager().not((BooleanFormula)f1));
							}
						}
						// First element equals to the SubFormula
						subFormula = sContext.getFormulaManager().getBooleanFormulaManager().equivalence(subFormula, (BooleanFormula)f);
						// Add this to the constraint
						constraint = sContext.getFormulaManager().getBooleanFormulaManager().and(constraint, subFormula);
					}
				}
				
				res = sContext.getFormulaManager().getBooleanFormulaManager().and(res, constraint);
			}
		}
		return res;
	}
	
	public static Map<String, Integer> setParamPosition(CitModel m) {
		Map<String, Integer> paramPosition = new HashMap<String, Integer>();
		int i=0;
		
		for (Parameter p : m.getParameters()) {
			paramPosition.put(p.getName(), i++);
		}
		
		return paramPosition;
	}
	
	public static<T> String getCorrespondingKey(String prefix, Map<String, T> map) {
		for (Entry<String, T> e : map.entrySet()) {
			if (prefix.startsWith(e.getKey() + "_"))
				return e.getKey();
		}
		return null;
	}
}