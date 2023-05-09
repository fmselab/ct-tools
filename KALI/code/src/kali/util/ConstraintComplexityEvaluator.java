package kali.util;

import ctwedge.ctWedge.AndExpression;
import ctwedge.ctWedge.AtomicPredicate;
import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Constraint;
import ctwedge.ctWedge.EqualExpression;
import ctwedge.ctWedge.ImpliesExpression;
import ctwedge.ctWedge.ModMultDiv;
import ctwedge.ctWedge.NotExpression;
import ctwedge.ctWedge.OrExpression;
import ctwedge.ctWedge.PlusMinus;
import ctwedge.ctWedge.RelationalExpression;
import ctwedge.ctWedge.util.CtWedgeSwitch;
import ctwedge.util.ext.Utility;

public class ConstraintComplexityEvaluator extends CtWedgeSwitch<Integer> {
	
	/**
	 * Computes the complexity of the constraints of a model
	 * 
	 * @param fileName: the file name of the combinatorial model
	 * @return the complexity
	 */
	public int evaluateConstraintComplexity(String fileName) {
		// Read the model
		CitModel m = Utility.loadModelFromPath(fileName);

		// Total complexity
		int complexity = 0;

		for (Constraint c : m.getConstraints()) {
			complexity += this.doSwitch(c);
		}

		return complexity;
	}

	/**
	 * Manages Not Expression
	 * 
	 * @param not: the not expression
	 * @return the corresponding complexity
	 */
	@Override
	public Integer caseNotExpression(NotExpression not) {
		return 1 + this.doSwitch(not.getPredicate());
	}

	/**
	 * Manages Implies Expression
	 * 
	 * @param ruleExpr: the implies expression
	 * @return the corresponding complexity
	 */
	@Override
	public Integer caseImpliesExpression(ImpliesExpression ruleExpr) {
		return 1 + this.doSwitch(ruleExpr.getLeft()) + this.doSwitch(ruleExpr.getRight());
	}

	/**
	 * Manages Or Expression
	 * 
	 * @param orExpr: the or expression
	 * @return the corresponding complexity
	 */
	@Override
	public Integer caseOrExpression(OrExpression orExpr) {
		return 1 + this.doSwitch(orExpr.getLeft()) + this.doSwitch(orExpr.getRight());
	}

	/**
	 * Manages And Expression
	 * 
	 * @param andExpr: the And expression
	 * @return the corresponding complexity
	 */
	@Override
	public Integer caseAndExpression(AndExpression andExpr) {
		return 1 + this.doSwitch(andExpr.getLeft()) + this.doSwitch(andExpr.getRight());
	}
	
	/**
	 * Manages Relational Expression
	 * 
	 * @param ineqExpr: the relational expression
	 * @return the corresponding complexity
	 */
	@Override
	public Integer caseRelationalExpression(RelationalExpression ineqExpr) {
		return 1 + this.doSwitch(ineqExpr.getLeft()) + this.doSwitch(ineqExpr.getRight());
	}

	/**
	 * Manages PlusMinus Expression
	 * 
	 * @param pm: the plusMinus expression
	 * @return the corresponding complexity
	 */
	@Override
	public Integer casePlusMinus(PlusMinus pm) {
		return 1 + this.doSwitch(pm.getLeft()) + this.doSwitch(pm.getRight());
	}

	/**
	 * Manages ModMultDiv Expression
	 * 
	 * @param md: the ModMultDiv expression
	 * @return the corresponding complexity
	 */
	@Override
	public Integer caseModMultDiv(ModMultDiv md) {
		return 1 + this.doSwitch(md.getLeft()) + this.doSwitch(md.getRight());
	}
	
	/**
	 * Manages Equal Expression
	 * 
	 * @param object: the Equal expression
	 * @return the corresponding complexity
	 */
	@Override
	public Integer caseEqualExpression(EqualExpression object) {
		return 1 + this.doSwitch(object.getLeft()) + this.doSwitch(object.getRight());
	}

	/**
	 * Manages AtomicPredicates
	 * 
	 * @param atom: the AtomicPredicates
	 * @return the corresponding complexity
	 */
	@Override
	public Integer caseAtomicPredicate(AtomicPredicate atom) {
		return 0;
	}
	
}
