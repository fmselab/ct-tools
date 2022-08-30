 package mantra.kali.util;

/*******************************************************************************
 * Copyright (c) 2020 University of Bergamo - Italy
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Andrea Bombarda - initial API and implementation
 *   
 * add a parameter (and return the parameter added) with the right type
 *  
 ******************************************************************************/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;

import ctwedge.ctWedge.Bool;
import ctwedge.ctWedge.Element;
import ctwedge.ctWedge.Enumerative;
import ctwedge.ctWedge.Range;
import ctwedge.ctWedge.util.CtWedgeSwitch;
import ctwedge.util.validator.ParameterElementsGetterAsStrings;
import mantra.kali.KaliTestContext;

public class ParameterAdder extends CtWedgeSwitch<List<Formula>> {

	/**
	 * The test context
	 */
	private KaliTestContext ctx;

	/**
	 * Builds a new ParameterAdder
	 * 
	 * @param ctx: the Logical Context
	 */
	public ParameterAdder(KaliTestContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * Manages Enumerative parameters
	 * 
	 * @param enumerative: the Enumerative Parameter
	 * @return the corresponding formula (or possibily a list of formulas)
	 */
	@Override
	public List<Formula> caseEnumerative(Enumerative enumerative) {
		// The list of formulas to be returned
		List<Formula> list = new ArrayList<Formula>();
		
		// Check if the enumerative type is already declared		
		if (ctx.declaredTypes.get(enumerative) != null) {
			Map<String, Formula> thisElement = ctx.declaredTypes.get(enumerative);
			for (String key : thisElement.keySet()) {
				list.add(thisElement.get(key));
			}
			return list;
		}
		
		// The enumerative type is a new declared type. For each element we must create a Boolean Variable
		String enumName = enumerative.getName();
		
		// Create the list of elements
		Map<String, Formula> thisElement = new HashMap<String, Formula>();
		for (Element e : enumerative.getElements()) {
			BooleanFormula enumElement = ctx.getContext().getFormulaManager().getBooleanFormulaManager().makeVariable(enumName + "_" + e.getName());
			list.add(enumElement);
			thisElement.put(enumName + "_" + e.getName(), enumElement);
		}
		ctx.declaredTypes.put(enumerative, thisElement);
		
		// Return the list of the variables corresponding to the enums
		return list;
	}

	/**
	 * Manages Boolean parameters
	 * 
	 * @param boolParam: the Boolean Parameter
	 * @return the corresponding formula (or possibily a list of formulas)
	 */
	@Override
	public List<Formula> caseBool(Bool boolParam) {
		Formula f = ctx.getContext().getFormulaManager().getBooleanFormulaManager().makeVariable(boolParam.getName());
		List<Formula> list = Arrays.asList(f);
		Map<String, Formula> thisElement = new HashMap<String, Formula>();
		thisElement.put(boolParam.getName(), f);
		ctx.declaredTypes.put(boolParam, thisElement);
		return list;
	}

	/**
	 * Manages Ranges parameters
	 * 
	 * @param range: the Range Parameter
	 * @return the corresponding formula (or possibily a list of formulas)
	 */
	@Override
	public List<Formula> caseRange(Range range) {
		// The Range object can be seen as an array of values => Get the list of all possible values
		ArrayList<String> values = new ArrayList<String>(ParameterElementsGetterAsStrings.eInstance.caseRange(range));
		if (values.size() < 1) 
			throw new RuntimeException("Not valid");
		
		List<Formula> list = Arrays.asList(ctx.getContext().getFormulaManager().getIntegerFormulaManager().makeVariable(range.getName()));
		return list;
	}

}
