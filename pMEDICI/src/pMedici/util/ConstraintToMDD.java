package pMedici.util;

import java.util.Stack;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.operators.MDDBaseOperators;

import ctwedge.ctWedge.AndExpression;
import ctwedge.ctWedge.AtomicPredicate;
import ctwedge.ctWedge.Bool;
import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.EqualExpression;
import ctwedge.ctWedge.OrExpression;
import ctwedge.ctWedge.Parameter;
import ctwedge.ctWedge.util.CtWedgeSwitch;
import ctwedge.generator.util.ParameterElementsGetterAsStrings;
import ctwedge.util.ParameterValuesToInt;
import pMedici.safeelements.ExtendedSemaphore;

/**
 * 
 */
public class ConstraintToMDD extends CtWedgeSwitch<Void> {

	private ParameterValuesToInt valConverter;
	private MDDManager manager;

	public ConstraintToMDD(CitModel citModel, MDDManager manager) {
		valConverter = new ParameterValuesToInt(citModel);
		this.manager = manager;
	}
	
	Stack<Integer> tPList;
	@Override
	public Void caseConstraint(ctwedge.ctWedge.Constraint object) {
		tPList = new Stack<Integer>();
		return null; 
	}

	@Override
	public Void caseAndExpression(AndExpression and){
		// AND Operation
		assert (tPList.size() >= 2);
		Integer n1 = tPList.pop();
		Integer n2 = tPList.pop();
		try {
			ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
			int newNode = MDDBaseOperators.AND.combine(manager, n1, n2);
			ExtendedSemaphore.OPERATION_SEMAPHORE.release();
			tPList.push(newNode);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Void caseOrExpression(OrExpression or) {
		return null;
	}

	@Override
	public Void caseEqualExpression(EqualExpression x) {
		throw new RuntimeException();
	}

	@Override
	public Void caseAtomicPredicate(AtomicPredicate x) {
		// in case the predicate is not in an EqualExpression
		String name = x.getName();
		Parameter paramByName = valConverter.getParamByName(name);
		if (paramByName instanceof Bool) {
			int base = valConverter.get(name);
			int value = base + ParameterElementsGetterAsStrings.instance.doSwitch(paramByName).indexOf(ParameterElementsGetterAsStrings.TRUE_AS_STRING);
			assert value != -1;
			tPList.push(value);
		}
		throw new RuntimeException();
		int newNode =  valConverter.get(null) getTupleFromParameter(value, bounds, nParams, manager);
		tPList.push(newNode);
		// in case the predicate is not in an EqualExpression
		String name = x.getName();
		Parameter paramByName = valConverter.getParamByName(name);
		if (paramByName instanceof Bool) {
			int base = valConverter.get(name);
			int value = base + ParameterElementsGetterAsStrings.instance.doSwitch(paramByName).indexOf(ParameterElementsGetterAsStrings.TRUE_AS_STRING);
			assert value != -1;
			return Integer.toString(value);
		}
		return super.caseAtomicPredicate(x);
	}

	@Override
	public String caseNotExpression(NotExpression x) {
		return doSwitch(x.getPredicate()) + " -";
	}

	@Override
	public String caseImpliesExpression(ImpliesExpression impl) {
		if (impl.getOp() == ImpliesOperator.IMPL) {
			// convert to not A or B
			NotExpression notL = CtWedgeFactory.eINSTANCE.createNotExpression();
			notL.setPredicate(EcoreUtil2.clone(impl.getLeft()));
			OrExpression or = CtWedgeFactory.eINSTANCE.createOrExpression();
			or.setLeft(notL);
			or.setRight(EcoreUtil2.clone(impl.getRight()));
			return doSwitch(or);
		} else {
			// convert to equals 
			EqualExpression eqE = CtWedgeFactory.eINSTANCE.createEqualExpression();
			eqE.setLeft(EcoreUtil2.clone(impl.getLeft()));
			eqE.setOp(Operators.EQ);
			eqE.setRight(EcoreUtil2.clone(impl.getRight()));
			return doSwitch(eqE);
		}
	}
	
	@Override
	public String caseRelationalExpression(RelationalExpression object) {
		throw new NotConvertableModel("relational expression are not supported");
	}
	

	@Override
	public String caseConstraint(Constraint x) {
		return doSwitch(x);
	}
}
