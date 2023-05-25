package org.colomoto.mddlib.logicalfunction.operators;

import java.util.Stack;

import org.colomoto.mddlib.MDDOperator;
import org.colomoto.mddlib.logicalfunction.FunctionNode;
import org.colomoto.mddlib.logicalfunction.OperatorFactory;
import org.colomoto.mddlib.operators.MDDBaseOperators;


/**
 * Factory for the "or" operator.
 * 
 * @author Aurelien Naldi
 */
public class OrOperatorFactory implements OperatorFactory {

	/**
	 * Use this single instance object if you need this factory
	 */
	public static final OrOperatorFactory FACTORY = new OrOperatorFactory();
	
	public static final int PRIORITY = 0;
	public static final String SYMBOL = "|";

	private OrOperatorFactory() {
		// single-instance: no constructor
	}
	
	@Override
	public String getSymbol() {
		return SYMBOL;
	}

	@Override
	public int getPriority() {
		return PRIORITY;
	}

	@Override
	public FunctionNode getNode(Stack<FunctionNode> stack) {
		return new OrOperator(stack);
	}
	
	public FunctionNode getNode(FunctionNode n1, FunctionNode n2) {
		return new OrOperator(n1, n2);
	}
}

/**
 * The "or" operator itself.
 * 
 * @author Fabrice Lopez: initial implementation
 * @author Aurelien Naldi: adaptation
 */
class OrOperator extends AbstractBinaryOperator {

	public OrOperator(Stack<FunctionNode> stack) {
		super(stack);
	}

	public OrOperator(FunctionNode leftArg, FunctionNode rightArg) {
		super(leftArg, rightArg);
	}

	@Override
	public String getSymbol() {
		return OrOperatorFactory.SYMBOL;
	}
	
	@Override
	protected MDDOperator getMDDOperation() {
		return MDDBaseOperators.OR;
	}
}
