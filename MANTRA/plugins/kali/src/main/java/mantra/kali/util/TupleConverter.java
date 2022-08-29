package mantra.kali.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

import ctwedge.ctWedge.Bool;
import ctwedge.ctWedge.Enumerative;
import ctwedge.ctWedge.Parameter;
import ctwedge.ctWedge.Range;
import mantra.kali.KaliPlugin.KaliTestContext;

public class TupleConverter {

	public static BooleanFormula extractFormulaFromTuple(KaliTestContext ctx, Map<Parameter, List<Formula>> variables,
			Map<Parameter, String> requirement) {
		BooleanFormula t = ctx.getContext().getFormulaManager().getBooleanFormulaManager().makeTrue();

		for (Parameter p : requirement.keySet()) {
			BooleanFormula tNew = null;
			List<Formula> varPointer = variables.get(p);
			assert varPointer != null;

			// Check the type of the parameter
			if (p instanceof Enumerative) {
				Formula leftSide = null;

				// If the parameter is an Enumerative, we must choose the correct boolean
				// formula between those of the several values
				Map<String, Formula> formulaMap = ctx.declaredTypes.get(p);
				for (Entry<String, Formula> e : formulaMap.entrySet()) {
					if (e.getKey().equals(p.getName() + "_" + requirement.get(p))) {
						leftSide = e.getValue();
					}
				}

				if (leftSide == null || !(leftSide instanceof BooleanFormula))
					throw new RuntimeException("Error during tuple composition - Enumerative");

				tNew = (BooleanFormula) leftSide;
			} else if (p instanceof Bool) {
				assert varPointer.size() == 1;
				if (requirement.get(p).toLowerCase().equals("true"))
					tNew = (BooleanFormula) varPointer.get(0);
				else
					tNew = ctx.getContext().getFormulaManager().getBooleanFormulaManager().not((BooleanFormula) varPointer.get(0));
			} else if (p instanceof Range) {
				// Get the left side of the comparison
				Formula leftSide = null;
				for (Entry<Parameter, List<Formula>> e : variables.entrySet()) {
					assert e.getValue().size() == 1;
					if (e.getKey().getName().equals(p.getName()))
						leftSide = e.getValue().get(0);
				}
				// Get the right side of the comparison
				Formula rightSide = ctx.getContext().getFormulaManager().getIntegerFormulaManager()
						.makeNumber(Integer.parseInt(requirement.get(p)));

				tNew = ctx.getContext().getFormulaManager().getIntegerFormulaManager().equal((IntegerFormula) leftSide,
						(IntegerFormula) rightSide);
			}

			t = ctx.getContext().getFormulaManager().getBooleanFormulaManager().and(t, tNew);
		}
		return t;
	}
}