package kali.safeelements;

import java.util.Comparator;

abstract public class TestContextComparator implements Comparator<TestContext> {

	@Override
	abstract public int compare(TestContext o1, TestContext o2);

}
