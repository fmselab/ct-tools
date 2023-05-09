package org.colomoto.mddlib.logicalfunction.operators;

import java.util.Stack;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.logicalfunction.FunctionNode;
import org.colomoto.mddlib.logicalfunction.OperatorFactory;


/**
 * Factory for the "not" operator.
 * 
 * @author Aurelien Naldi
 */
public class NotOperatorFactory implements OperatorFactory {

	/**
	 * Use this single instance object if you need this factory
	 */
	public static final NotOperatorFactory FACTORY = new NotOperatorFactory();
	
	public static final int PRIORITY = 2;
	public static final String SYMBOL = "!";

	private NotOperatorFactory() {
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
		return new NotOperator(stack);
	}
	
	public FunctionNode getNode(FunctionNode n) {
		return new NotOperator(n);
	}
}

/**
 * The "not" operator itself.
 * 
 * @author Fabrice Lopez: initial implementation
 * @author Aurelien Naldi: adaptation
 */
class NotOperator extends AbstractUnaryOperator {

	public NotOperator(Stack<FunctionNode> stack) {
		super(stack);
	}

	public NotOperator(FunctionNode f) {
		super(f);
	}

	@Override
	public String getSymbol() {
		return NotOperatorFactory.SYMBOL;
	}
  
	@Override
	public int getMDD(MDDManager ddmanager) {
		int mdd = arg.getMDD(ddmanager);
		int ret = ddmanager.not(mdd);
		ddmanager.free(mdd);
		return ret;
	}
}
