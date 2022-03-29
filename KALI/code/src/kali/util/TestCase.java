package kali.util;

public class TestCase{

	String paramName;
	
	Object value;

	public TestCase(String paramName, Object value) {
		super();
		this.paramName = paramName;
		this.value = value;
	}

	public String getParamName() {
		return paramName;
	}
	
	public Object getValue() {
		return value;
	}

}
