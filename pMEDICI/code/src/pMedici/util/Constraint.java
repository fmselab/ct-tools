package pMedici.util;

import java.util.Stack;

public class Constraint {
	
	Stack<ConstraintElement> constraint;
	
	public Constraint() {
		constraint = new Stack<ConstraintElement>();
	}
	
	public void addElement(ConstraintElement e) {
		this.constraint.push(e);
	}
	
	public ConstraintElement getElement() {
		return this.constraint.pop();
	}
	
}

class ConstraintElement {
	
	int value;
	String operator;
	
	public ConstraintElement() {
		value = -1;
		operator = "";
	}
	
	void setValue(int value) {
		this.value = value;
	}
	
	void setOperator(String operator) {
		assert (operator.equals("+") || operator.equals("*") || operator.equals("-"));
		this.operator = operator;
	}
	
	boolean isOperator() {
		return !this.operator.equals("");
	}
	
	boolean isValue() {
		return this.value != -1;
	}
}
