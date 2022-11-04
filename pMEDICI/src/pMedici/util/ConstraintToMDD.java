package pMedici.util;

import java.util.List;
import java.util.Stack;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.operators.MDDBaseOperators;
import org.eclipse.xtext.EcoreUtil2;

import ctwedge.ctWedge.AndExpression;
import ctwedge.ctWedge.AtomicPredicate;
import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Constraint;
import ctwedge.ctWedge.CtWedgeFactory;
import ctwedge.ctWedge.EqualExpression;
import ctwedge.ctWedge.Expression;
import ctwedge.ctWedge.ImpliesExpression;
import ctwedge.ctWedge.ImpliesOperator;
import ctwedge.ctWedge.NotExpression;
import ctwedge.ctWedge.Operators;
import ctwedge.ctWedge.OrExpression;
import ctwedge.ctWedge.Parameter;
import ctwedge.ctWedge.RelationalExpression;
import ctwedge.ctWedge.util.CtWedgeSwitch;
import ctwedge.generator.util.ParameterElementsGetterAsStrings;
import ctwedge.util.Pair;
import ctwedge.util.ParameterValuesToInt;
import ctwedge.util.ext.NotConvertableModel;
import pMedici.safeelements.ExtendedSemaphore;

/**
 * 
 */
public class ConstraintToMDD extends CtWedgeSwitch<Void> {

	private MDDManager manager;
	private CitModel model; 
	private Stack<Integer> tPList;
	private int[] bounds;
	private ParameterValuesToInt valConverter;
	
	public ConstraintToMDD(CitModel citModel, MDDManager manager) {
		this.model = citModel;
		this.manager = manager;
		this.tPList = new Stack<Integer>();	
		this.bounds = Operations.getBounds(model);
		this.valConverter = new ParameterValuesToInt(citModel);
	}
	
	@Override
	public Void caseConstraint(Constraint object) {
		//tPList = new Stack<Integer>();
		//System.out.println("Clean");
		//doSwitch(object);
		return null; 
	}

	@Override
	public Void caseAndExpression(AndExpression and){
		doSwitch(and.getLeft());
		doSwitch(and.getRight());
		
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
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Void caseOrExpression(OrExpression or) {
		doSwitch(or.getLeft());
		doSwitch(or.getRight());
		
		// OR Operation
		assert (tPList.size() >= 2) : tPList.size();
		Integer n1 = tPList.pop();
		Integer n2 = tPList.pop();
		try {
			ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
			int newNode = MDDBaseOperators.OR.combine(manager, n1, n2);
			ExtendedSemaphore.OPERATION_SEMAPHORE.release();
			tPList.push(newNode);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Void caseNotExpression(NotExpression x) {
		doSwitch(x.getPredicate());
		
		// NOT Operation
		assert (tPList.size() >= 1);
		Integer n1 = tPList.pop();
		try {
			ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
			int newNode = manager.not(n1);
			ExtendedSemaphore.OPERATION_SEMAPHORE.release();
			tPList.push(newNode);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Void caseImpliesExpression(ImpliesExpression impl) {
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
	public Void caseRelationalExpression(RelationalExpression object) {
		throw new NotConvertableModel("Relational expression are not supported");
	}

	@SuppressWarnings("deprecation")
	@Override
	public Void caseEqualExpression(EqualExpression x) {
		if (x.getLeft() instanceof AtomicPredicate && x.getRight() instanceof AtomicPredicate) {
			// Only the right part needs to be translated
			Pair<Character, Integer> eqToInt = valConverter.eqToInt((AtomicPredicate) x.getLeft(), x.getOp(),
					(AtomicPredicate) x.getRight());
			if (eqToInt.getFirst() == '-') {
				NotExpression notL = CtWedgeFactory.eINSTANCE.createNotExpression();
				notL.setPredicate(EcoreUtil2.clone(x.getRight()));
				doSwitch(notL);
			} else {
				doSwitch(x.getRight());
			}
		} else {
			if (x.getOp() != Operators.EQ) throw new RuntimeException("equal expected"); 
			// If they are not atomic predicates, it means that the equal has been derived
			// from a double implication a <=> b
			// Let's convert it as (a and b) or (not a and not b)
			OrExpression orE = CtWedgeFactory.eINSTANCE.createOrExpression();
			AndExpression andER = CtWedgeFactory.eINSTANCE.createAndExpression();
			AndExpression andEL = CtWedgeFactory.eINSTANCE.createAndExpression();
			NotExpression notA = CtWedgeFactory.eINSTANCE.createNotExpression();
			NotExpression notB = CtWedgeFactory.eINSTANCE.createNotExpression();
			Expression left = EcoreUtil2.clone(x.getLeft());
			Expression right = EcoreUtil2.clone(x.getRight());
			Expression left2 = EcoreUtil2.clone(left);
			Expression right2 = EcoreUtil2.clone(right);

			andER.setLeft(left);
			andER.setRight(right);
			notA.setPredicate(left2);
			notB.setPredicate(right2);
			andEL.setLeft(notA);
			andEL.setRight(notB);
			orE.setLeft(andEL);
			orE.setRight(andER);
			doSwitch(orE);
		}
		return null;
	}

	@Override
	public Void caseAtomicPredicate(AtomicPredicate x) {
		int count = 0;
		int index = 0;
		
		// FIXME: How to map on the correct value? It depends on the parameter, which is normally in the left side,
		// while we here deal only with the right one
		// It is wrong to use the following code, since the same value in different parameters (e.g., true or false 
		// for booleans) has to be mapped differently
		
		for (Parameter p : model.getParameters()) {
			List<String> values = ParameterElementsGetterAsStrings.instance.doSwitch(p);
			int value = values.indexOf(x.getName());
			if (value == -1) {
				value = values.indexOf(x.getBoolConst());
			}
			int newNode;
			if (value != -1) {
				try {
					newNode = Operations.getTupleFromParameter(count + value, bounds, model.getParameters().size(), manager);
					tPList.push(newNode);
					System.out.println(newNode);
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			count += bounds[index];
			index++;
		}
		return null;
	}
	
	public int returnMdd() {
		if (tPList.size() != 1) {
			throw new RuntimeException(tPList.size() + " - ERROR IN CONSTRAINTS DEFINITION \n");
		}
		return tPList.pop();
	}

	public void reset() {
		tPList = new Stack<Integer>();
	}
}
