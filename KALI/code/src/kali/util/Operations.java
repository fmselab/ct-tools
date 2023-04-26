package kali.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.SolverContext;

import ctwedge.ctWedge.Bool;
import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Constraint;
import ctwedge.ctWedge.Element;
import ctwedge.ctWedge.Enumerative;
import ctwedge.ctWedge.Parameter;
import ctwedge.ctWedge.Range;
import ctwedge.generator.util.ParameterSize;
import ctwedge.util.Pair;
import kali.safeelements.TestContext;

public class Operations {

	/**
	 * Return the tuple given as parameter as a string
	 * 
	 * @param tuple: the tuple to be printed
	 * @return the string representing the tuple
	 */
	public static String printTuple(Vector<Pair<String, Object>> tuple) {
		String res = "";
		for (Pair<String, Object> t : tuple) {
			res += ("<" + t.getFirst() + "," + t.getSecond() + ">");
		}
		return res;
	}

	/**
	 * Returns the map mapping each parameter on its position
	 * 
	 * @param m: the combinatorial model
	 * @return the map mapping each parameter on its position
	 */
	public static Map<String, Integer> setParamPosition(CitModel m) {
		Map<String, Integer> paramPosition = new HashMap<String, Integer>();
		int i = 0;

		for (Parameter p : m.getParameters()) {
			paramPosition.put(p.getName(), i++);
		}

		return paramPosition;
	}

	/**
	 * Convert the elements in a MAP [ParameterName, List(Values)]
	 * 
	 * @param m:     the combinatorial model
	 * @param order: the order for parameter consideration during tuple generation
	 * @return the elements in a MAP [ParameterName, List(Values)]
	 */
	public static Map<String, List<Object>> getElementsMap(CitModel m, Order order) {
		assert order != null;
		Map<String, List<Object>> elementsMap = new HashMap<String, List<Object>>();

		List<Parameter> parameters = new ArrayList<Parameter>();
		for (Parameter p : m.getParameters())
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
				Enumerative e = (Enumerative) p;
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
						Range e = (Range) p;
						int step = e.getStep() > 0 ? e.getStep() : 1;
						for (int i = e.getBegin(); i <= e.getEnd(); i = i + step) {
							s.add(i);
						}
					} else {
						throw new RuntimeException("Unknown type of parameter");
					}
				}
			}
			elementsMap.put(p.getName(), s);
		}

		// Returns the elements map
		return elementsMap;
	}

	/**
	 * Creates the SMT context from the combinatorial model
	 * 
	 * @param model:     the combinatorial model
	 * @param ctx:       the test context
	 * @param variables: the mapping between parameters and formulas
	 * @return the Boolean formula representing the totality of constraints
	 */
	public static BooleanFormula createCtxFromModel(CitModel model, TestContext ctx,
			Map<Parameter, List<Formula>> variables) {
		// Add all the parameters to the new CTX
		addParameters(model, ctx, variables);

		// Add constraints when more formulas are available for a single parameter (it
		// happens only in case of Enumeratives)
		// or when the values to be assumed are limited (e.g. for ranges)
		// Then, Translate all the constraints and add them to the context
		BooleanFormula constraints = addConstraintsForRanges(model, ctx, variables);
		constraints = ctx.getContext().getFormulaManager().getBooleanFormulaManager().and(constraints,
				addConstraintsForEnumeratives(model, ctx, variables));
		for (Constraint r : model.getConstraints()) {
			ConstraintTranslator translator = new ConstraintTranslator(ctx, variables);
			Formula constraint = translator.doSwitch(r);

			assert constraint instanceof BooleanFormula : "Constraints must be boolean";

			constraints = ctx.getContext().getFormulaManager().getBooleanFormulaManager().and(constraints,
					(BooleanFormula) constraint);
		}

		return constraints;
	}

	/**
	 * Add the constraints for ranges: the value must be included between the lower
	 * and the upper bound
	 * 
	 * @param model:     the combinatorial model
	 * @param ctx:       the TestContext
	 * @param variables: the map of the possible formulas
	 * @return the boolean formula representing the enumeratives constraints
	 */
	private static BooleanFormula addConstraintsForRanges(CitModel model, TestContext ctx,
			Map<Parameter, List<Formula>> variables) {
		SolverContext sContext = ctx.getContext();

		BooleanFormula res = sContext.getFormulaManager().getBooleanFormulaManager().makeTrue();
		for (Parameter p : model.getParameters()) {
			// Only if it is a range
			if (p instanceof Range) {

				List<Formula> list = variables.get(p);
				BooleanFormula constraint = sContext.getFormulaManager().getBooleanFormulaManager().makeTrue();

				// If the size is greater than 1, only one value per time can be true
				if (list.size() == 1) {
					for (Formula f : list) {
						BooleanFormula subFormula = sContext.getFormulaManager().getBooleanFormulaManager().makeTrue();
						// Value >= lowerBound
						subFormula = sContext.getFormulaManager().getBooleanFormulaManager().and(subFormula,
								sContext.getFormulaManager().getIntegerFormulaManager().greaterOrEquals((IntegerFormula) f,
										sContext.getFormulaManager().getIntegerFormulaManager()
												.makeNumber(((Range) p).getBegin())));
						// Value <= upeprBound
						subFormula = sContext.getFormulaManager().getBooleanFormulaManager().and(subFormula,
								sContext.getFormulaManager().getIntegerFormulaManager().lessOrEquals((IntegerFormula) f,
										sContext.getFormulaManager().getIntegerFormulaManager()
												.makeNumber(((Range) p).getEnd())));
						// Add this to the constraint
						constraint = sContext.getFormulaManager().getBooleanFormulaManager().and(constraint,
								subFormula);
					}
				} else {
					throw new RuntimeException("Error in the definition of the range variable " + p.getName());
				}

				res = sContext.getFormulaManager().getBooleanFormulaManager().and(res, constraint);
			}
		}
		return res;
	}

	/**
	 * Fetch all the parameters in the model and creates the needed variables
	 * 
	 * @param model:     the combinatorial model
	 * @param ctx:       the TestContext context
	 * @param variables: the Map of the possible formulas
	 */
	public static void addParameters(CitModel model, TestContext ctx, Map<Parameter, List<Formula>> variables) {
		// Add all the parameters to the logical context
		ParameterAdder pa = new ParameterAdder(ctx);

		for (Parameter nt : model.getParameters()) {
			List<Formula> variable = pa.doSwitch(nt);
			variables.put(nt, variable);
		}
	}

	/**
	 * Add the constraints for enumeratives: Only one value at a time can be true
	 * 
	 * @param model:     the combinatorial model
	 * @param ctx:       the TestContext
	 * @param variables: the map of the possible formulas
	 * @return the boolean formula representing the enumeratives constraints
	 */
	public static BooleanFormula addConstraintsForEnumeratives(CitModel model, TestContext ctx,
			Map<Parameter, List<Formula>> variables) {
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
										sContext.getFormulaManager().getBooleanFormulaManager()
												.not((BooleanFormula) f1));
							}
						}
						// First element equals to the SubFormula
						subFormula = sContext.getFormulaManager().getBooleanFormulaManager().equivalence(subFormula,
								(BooleanFormula) f);
						// Add this to the constraint
						constraint = sContext.getFormulaManager().getBooleanFormulaManager().and(constraint,
								subFormula);
					}
				}

				res = sContext.getFormulaManager().getBooleanFormulaManager().and(res, constraint);
			}
		}
		return res;
	}

	/**
	 * Checks if in the map there is an entry with the key that starts with the
	 * given prefix and return the name
	 * 
	 * @param prefix: the prefix
	 * @param map:    the map
	 * @return the name of the param
	 */
	public static <T> String getCorrespondingKey(String prefix, Map<String, T> map) {
		for (Entry<String, T> e : map.entrySet()) {
			if (prefix.startsWith(e.getKey() + "_"))
				return e.getKey();
		}
		return null;
	}

}