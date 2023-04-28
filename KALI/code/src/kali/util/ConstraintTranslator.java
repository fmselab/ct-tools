package kali.util;

import java.util.List;

/*******************************************************************************
 * Copyright (c) 2020 University of Bergamo - Italy
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Andrea Bombarda - initial API and implementation
 ******************************************************************************/

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import ctwedge.ctWedge.*;
import ctwedge.ctWedge.util.CtWedgeSwitch;
import ctwedge.generator.util.ParameterElementsGetterAsStrings;
import kali.safeelements.TestContext;

/**
 * translates a constraints in SMT expressions
 * 
 * @author bombarda_andrea
 * 
 */
public class ConstraintTranslator extends CtWedgeSwitch<Formula> {

	private static final Logger logger = Logger.getLogger(ConstraintTranslator.class);

	/**
	 * The test context
	 */
	TestContext ctx;
	
	/**
	 * The map between parameters and the corresponding formulas
	 */
	Map<Parameter, List<Formula>> variables;
	
	/**
	 * The formula managers
	 */
	FormulaManager fmgr;
	BooleanFormulaManager bmgr;
	IntegerFormulaManager ifmgr; 

	/**
	 * Creates a Constraint Translator
	 * 
	 * @param ctx: the test context
	 * @param variables: the map between parameters and the corresponding formulas
	 */
	public ConstraintTranslator(TestContext ctx, Map<Parameter, List<Formula>> variables) {
		this.ctx = ctx;
		this.variables = variables;
		fmgr= ctx.getContext().getFormulaManager();
		bmgr = fmgr.getBooleanFormulaManager();
		ifmgr = fmgr.getIntegerFormulaManager();
		// DO NOT USE - we do not support rational 
		// RationalFormulaManager rfmgr = fmgr.getRationalFormulaManager();
	}
	
	/**
	 * Manages Not Expression
	 * 
	 * @param not: the not expression
	 * @return the corresponding formula
	 */
	@Override
	public Formula caseNotExpression(NotExpression not) {
		
		Formula predicate = this.doSwitch(not.getPredicate());
		
		assert predicate instanceof BooleanFormula : "NotExpression predicate must be a boolean term";
		
		return bmgr.not((BooleanFormula) predicate);
	}

	/**
	 * Manages Implies Expression
	 * 
	 * @param ruleExpr: the implies expression
	 * @return the corresponding formula
	 */
	@Override
	public Formula caseImpliesExpression(ImpliesExpression ruleExpr) {
		
		Formula leftVal = this.doSwitch(ruleExpr.getLeft());
		Formula rightVal = this.doSwitch(ruleExpr.getRight());

		assert leftVal instanceof BooleanFormula && rightVal instanceof BooleanFormula: "ImpliesExpression terms must be boolean";
		
		switch (ruleExpr.getOp()) {
		case IMPL:
			// left => right
			return bmgr.implication((BooleanFormula)leftVal, (BooleanFormula)rightVal);
		case IFF:
			return bmgr.equivalence((BooleanFormula)leftVal, (BooleanFormula)rightVal);
		}
		throw new RuntimeException("Operator not found");
	}

	/**
	 * Manages Or Expression
	 * 
	 * @param orExpr: the or expression
	 * @return the corresponding formula
	 */
	@Override
	public Formula caseOrExpression(OrExpression orExpr) {
		
		Formula leftVal = this.doSwitch(orExpr.getLeft());
		Formula rightVal = this.doSwitch(orExpr.getRight());
		
		assert leftVal instanceof BooleanFormula && rightVal instanceof BooleanFormula: "OrExpression terms must be boolean";
		
		return bmgr.or((BooleanFormula)leftVal, (BooleanFormula)rightVal);
	}

	/**
	 * Manages And Expression
	 * 
	 * @param andExpr: the And expression
	 * @return the corresponding formula
	 */
	@Override
	public Formula caseAndExpression(AndExpression andExpr) {
		
		Formula leftVal = this.doSwitch(andExpr.getLeft());
		Formula rightVal = this.doSwitch(andExpr.getRight());
		
		assert leftVal instanceof BooleanFormula && rightVal instanceof BooleanFormula: "AndExpression terms must be boolean";
		
		return bmgr.and((BooleanFormula)leftVal, (BooleanFormula)rightVal);
	}
	
	/**
	 * Manages Relational Expression
	 * 
	 * @param ineqExpr: the relational expression
	 * @return the corresponding formula
	 */
	@Override
	public Formula caseRelationalExpression(RelationalExpression ineqExpr) {
		
		logger.debug("Parsing left");
		Formula leftVal = this.doSwitch(ineqExpr.getLeft());
		logger.debug(leftVal);
		
		logger.debug("Parsing Right");
		Formula rightVal = this.doSwitch(ineqExpr.getRight());
		logger.debug(rightVal);
		
		switch (ineqExpr.getOp()) {
		case GE:
			return ifmgr.greaterOrEquals((IntegerFormula)leftVal, (IntegerFormula)rightVal);
		case GT:			
			return ifmgr.greaterThan((IntegerFormula)leftVal, (IntegerFormula)rightVal);
		case LE:
			return ifmgr.lessOrEquals((IntegerFormula)leftVal, (IntegerFormula)rightVal);
		case LT:
			return ifmgr.lessThan((IntegerFormula)leftVal, (IntegerFormula)rightVal);
		case EQ:
		case NE:
			throw new RuntimeException("This should never happen");
		}
		
		throw new RuntimeException("Operator not found in constraint");
	}

	/**
	 * Manages PlusMinus Expression
	 * 
	 * @param pm: the plusMinus expression
	 * @return the corresponding formula
	 */
	@Override
	public Formula casePlusMinus(PlusMinus pm) {

		Formula leftVal = this.doSwitch(pm.getLeft());
		Formula rightVal = this.doSwitch(pm.getRight());

		assert leftVal instanceof IntegerFormula && rightVal instanceof IntegerFormula
				: "PlusMinus terms must be integers";

		if (pm.getOp() == PlusMinusOperators.MINUS)
			return ifmgr.subtract((IntegerFormula) leftVal, (IntegerFormula) rightVal);
		else
			return ifmgr.add((IntegerFormula) leftVal, (IntegerFormula) rightVal);
	}

	/**
	 * Manages ModMultDiv Expression
	 * 
	 * @param md: the ModMultDiv expression
	 * @return the corresponding formula
	 */
	@Override
	public Formula caseModMultDiv(ModMultDiv md) {

		Formula leftVal = this.doSwitch(md.getLeft());
		Formula rightVal = this.doSwitch(md.getRight());

		assert leftVal instanceof IntegerFormula && rightVal instanceof IntegerFormula
				: "ModMultDiv terms must be IntegerFormula";

		switch (md.getOp()) {
		case DIV: 
			return ifmgr.divide((IntegerFormula) leftVal, (IntegerFormula) rightVal);
		case MULT:
			return ifmgr.multiply((IntegerFormula) leftVal, (IntegerFormula) rightVal);
		case MOD: 
			return ifmgr.modulo((IntegerFormula) leftVal, (IntegerFormula) rightVal);
		}
		throw new RuntimeException("Operator not found");
	}
	
	/**
	 * Checks if an atomic predicate is an enumerative function
	 * 
	 * @param atom: the atomic predicate
	 * @return true if an atomic predicate is an enumerative function, false otherwise
	 */
	private boolean isEnumerative(AtomicPredicate atom) {
		// Check regular enumeratives
		for (Entry<Parameter, List<Formula>> e : variables.entrySet()) {
			if (e.getKey().getName().equals(atom.getName()) && e.getValue().size() >1) {
				return true;
			}
		}
		// Enumeratives may also be single-valued
		for (Entry<Parameter, List<Formula>> e : variables.entrySet()) {
			if (e.getKey().getName().equals(atom.getName()) && e.getValue().size() == 1 && ParameterElementsGetterAsStrings.instance.doSwitch(e.getKey()).size()==1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return the mapping between values and formulas
	 * 
	 * @param atom: the atomic predicate
	 * @return the mapping between values and formulas
	 */
	private Map<String, Formula> getFormulaMap(AtomicPredicate atom) {
		for (Entry<Parameter, Map<String, Formula>> entry : ctx.declaredTypes.entrySet()) {
			if (atom.getName().equals(entry.getKey().getName())) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Manages Equal Expression
	 * 
	 * @param object: the Equal expression
	 * @return the corresponding formula
	 */
	@Override
	public Formula caseEqualExpression(EqualExpression object) {
		
		Formula leftVal = null;
		Formula rightVal = null;
		
		logger.debug("Parsing left");
		// If the left part is an enumerative, the equal must be handled here
		if (object.getLeft() instanceof AtomicPredicate && isEnumerative((AtomicPredicate)object.getLeft())) {
			// "P1 = v1" or "P1 != v1" 
			Map<String, Formula> formulaMap = getFormulaMap((AtomicPredicate)object.getLeft());
			for (Entry<String, Formula> e : formulaMap.entrySet()) {
				if (e.getKey().equals(((AtomicPredicate)object.getLeft()).getName() + "_" + ((AtomicPredicate)object.getRight()).getName())) {
					leftVal = e.getValue();
				}
			}
			
			if (leftVal == null) {
				throw new RuntimeException("Comparison between parameters is not allowed");
			}

			switch (object.getOp()) {
			case EQ:
				return leftVal;
			case NE:					
				return bmgr.not((BooleanFormula)leftVal);
			default:
				throw new RuntimeException("This should never happen");
			}
		}
		
		// If no return has been performed, proceed as usual analyzing left and right
		leftVal = this.doSwitch(object.getLeft()); 
		logger.debug(leftVal);
		
		logger.debug("Parsing Right");
		rightVal = this.doSwitch(object.getRight());
		logger.debug(rightVal);
		
		switch (object.getOp()) {
		case EQ:
			return areEqual(leftVal, rightVal);
		case NE:
			return bmgr.not((BooleanFormula) areEqual(leftVal, rightVal));
		default:
			throw new RuntimeException("This should never happen");
		}
	}
	
	/**
	 * Checks if the two formulas are equal
	 * 
	 * @param leftVal: the first formula
	 * @param rightVal: the second formula
	 * @return the corresponding formula
	 */
	private Formula areEqual(Formula leftVal, Formula rightVal) {
		
		if (leftVal == null || rightVal==null)
			return bmgr.makeBoolean(false);
			
		// Different classes -> Cannot be equals
		if (!leftVal.getClass().equals(rightVal.getClass()))
			return bmgr.makeBoolean(false);
		
		if (leftVal instanceof BooleanFormula && rightVal instanceof BooleanFormula) {
			return bmgr.equivalence((BooleanFormula)leftVal, (BooleanFormula)rightVal);
		}
		
		if (leftVal instanceof IntegerFormula && rightVal instanceof IntegerFormula) {
			return ifmgr.equal((IntegerFormula)leftVal, (IntegerFormula)rightVal);
		}
				
		// None of the previous applicable -> Cannot be equals
		return bmgr.makeBoolean(false);
	}

	/**
	 * Manages AtomicPredicates
	 * 
	 * @param atom: the AtomicPredicates
	 * @return the corresponding formula
	 */
	@Override
	public Formula caseAtomicPredicate(AtomicPredicate atom) {

		// Boolean value
		if (atom.getBoolConst() != null)
			return bmgr.makeBoolean(atom.getBoolConst().equalsIgnoreCase("true") ? true : false);
		
		// Integer value
		try {
			int num = Integer.parseInt(atom.getName().toString());
			return ifmgr.makeNumber(num);
		} catch (NumberFormatException ex) {}
		
		// Numeric value with decimals
		try {
			Double.parseDouble(atom.getName().toString());
			throw new RuntimeException("currently rational numbers and not supported");
		} catch (NumberFormatException ex) {}
		
		// Variable name
		String varName = atom.getName().replace("\"", "");
		for (Entry<Parameter, List<Formula>> p : variables.entrySet())
			if (((Parameter)p.getKey()).getName().toString().equalsIgnoreCase(varName)) {
				if (p.getValue().size() == 1) {
					// Boolean parameter
					return p.getValue().get(0);
				} else {
					// Enumerative parameter. This should never happen since it should be resolved a priori
					throw new RuntimeException("This should never happen");
				}
			}
		
		return null;
	}

}
