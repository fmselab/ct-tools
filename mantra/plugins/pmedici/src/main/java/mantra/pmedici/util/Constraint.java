package mantra.pmedici.util;

import java.util.Stack;

public class Constraint {
	
	public Stack<ConstraintElement> constraint;
	
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
