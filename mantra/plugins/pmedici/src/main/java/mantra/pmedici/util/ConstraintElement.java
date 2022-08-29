package mantra.pmedici.util;

public class ConstraintElement {

	int value;
	String operator;

	public ConstraintElement() {
		value = -1;
		operator = "";
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		assert (operator.equals("+") || operator.equals("*") || operator.equals("-"));
		this.operator = operator;
	}

	public boolean isOperator() {
		return !this.operator.equals("");
	}

	boolean isValue() {
		return this.value != -1;
	}
}