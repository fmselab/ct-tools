package mantra.kali;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;
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
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;

import ctwedge.ctWedge.Bool;
import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Constraint;
import ctwedge.ctWedge.Element;
import ctwedge.ctWedge.Enumerative;
import ctwedge.ctWedge.Parameter;
import ctwedge.ctWedge.Range;
import ctwedge.generator.util.ParameterSize;
import ctwedge.generator.util.Utility;
import ctwedge.util.Pair;
import mantra.kali.util.ConstraintTranslator;
import mantra.kali.util.ParameterAdder;
import mantra.model.Model;
import mantra.safeelements.ExtendedSemaphore;
import mantra.safeelements.TestContext;
import mantra.util.Order;

import org.pf4j.Extension;
import org.pf4j.Plugin;

public class KaliPlugin extends Plugin {

	public KaliPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	public void start() {
		System.out.println("KaliPlugin.start()");
		// for testing the development mode
		if (RuntimeMode.DEVELOPMENT.equals(wrapper.getRuntimeMode())) {
			System.out.println(StringUtils.upperCase("KaliPlugin"));
		}
	}

	@Override
	public void stop() {
		System.out.println("KaliPlugin.stop()");
	}

	@Extension
	public static class KaliModel implements Model {
		
		CitModel citModel;
		
		

		@Override
		public void loadModelFromPath(String filename) {
			this.citModel =  Utility.loadModelFromPath(filename);
		}

		@Override
		public Map<Object, List<Object>> getElements(Order order) {
			assert order != null;
			Map<String, List<Object>> elementsMap = new HashMap<String, List<Object>>();		

			List<Parameter> parameters = new ArrayList<Parameter>();
			for(Parameter p: citModel.getParameters())
				parameters.add(p);
			
			// Order the parameters
			switch (order) {
			case RANDOM:
				Collections.shuffle(parameters);
				break;
			case IN_ORDER_SIZE_ASC:
				Collections.sort(parameters, new Comparator<Parameter>() {
					@Override
					public int compare(Parameter o1, Parameter o2) {
						return Integer.compare(ParameterSize.eInstance.doSwitch(o1), ParameterSize.eInstance.doSwitch(o2));
					}
				});
				break;
			case IN_ORDER_SIZE_DESC:
				Collections.sort(parameters, new Comparator<Parameter>() {
					@Override
					public int compare(Parameter o1, Parameter o2) {
						return Integer.compare(ParameterSize.eInstance.doSwitch(o2), ParameterSize.eInstance.doSwitch(o1));
					}
				});
				break;
			default:
				break;
			}

			// Fetch all the parameters
			for (Parameter p : parameters) {
				List<Object> s = new ArrayList<Object>();
				
				// If enumerative
				if (p instanceof Enumerative) {
					Enumerative e = (Enumerative)p;
					for (Element element : e.getElements()) {
						s.add(element.getName());
					}
				} else {
					// If boolean
					if (p instanceof Bool) {
						s.add(Boolean.TRUE);
						s.add(Boolean.FALSE);
					} else {
						// If range
						if (p instanceof Range) {
							Range e = (Range)p;
							int step = e.getStep() > 0 ? e.getStep() : 1;
							for (int i=e.getBegin(); i<=e.getEnd(); i=i+step)  {
								s.add(i);
							}
						} else {
							throw new RuntimeException ("Unknown type of parameter");
						}
					}
				}
				elementsMap.put(p.getName(), s);
			}
			
			// Returns the elements map
			return elementsMap.entrySet().stream().collect(Collectors.toMap(
					e -> e.getKey(), 
					e -> e.getValue()));
		}

		@Override
		public int getNParams() {
			return citModel.getParameters().size();
		}

		@Override
		public boolean getUseConstraints() {
			return getNParams() > 0;
		}

		@Override
		public EList<Parameter> getParameters() {
			return citModel.getParameters();
		}

		@Override
		public void translateOutputToString(HashSet<String> tests) {
			tests.forEach(x -> {System.out.println(x);});
		}

		@Override
		public String printTuple(Vector<Pair<Object, Object>> tuple) {
			String res = "";
			for (Pair<Object, Object> t : tuple) {
				res += ("<" + t.getFirst() + "," + t.getSecond() + ">");
			}
			return res;
		}

		public EList<Constraint> getConstraints(){
			return this.citModel.getConstraints();
		}
	}
	
	
	@Extension
	public static class KaliTestContext implements TestContext{
		
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
			return this.completenessGrades - t.completenessGrades;
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isImplied(Vector<Pair<Object, Object>> tuple) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCoverable(Vector<Pair<Object, Object>> tuple) throws InterruptedException, SolverException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCompatiblePartialCheck(Vector<Pair<Object, Object>> tuple) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean addTuple(Vector<Pair<Object, Object>> tuple) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ExtendedSemaphore getTestMutex() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getNCovered() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getTest(boolean b) throws InterruptedException, SolverException {
			// TODO Auto-generated method stub
			return null;
		}
		
		private BooleanFormula setupContext() throws InvalidConfigurationException {		
			// The formula representing the constraints		
			return createCtxFromModel(model, this, variablesList);
		}
		
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
		
		public SolverContext getContext() {
			return this.context;
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
	
}
