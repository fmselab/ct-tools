package pMedici.main;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.TestSuite;
import ctwedge.util.validator.MinimalityTestSuiteValidator;
import pMedici.safeelements.TestContext;
import pMedici.threads.TestBuilder;

public class PMediciTestReduce {

	@Test
	public void test() throws IOException, InterruptedException {
		Logger.getLogger(MinimalityTestSuiteValidator.class).setLevel(Level.ALL);
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		pMedici.PRINT_DEBUG = true;
		pMedici.verb = true;
		TestBuilder.LockTCOnlyOnWriting = true;
		TestSuite testsuite = pMedici.generateTests("examples/CTComp/BOOLC_0.ctw",2);
		assertEquals(2, testsuite.getStrength());
		System.out.println("test suite size " + testsuite.getTests().size());
		TestSuite testsuite = pMedici.generateTests("examples/CTComp/BOOLC_0.ctw",2);
		MinimalityTestSuiteValidator minimality = new MinimalityTestSuiteValidator(testsuite);
		System.out.println(minimality.isMinimal());
		TestSuite ts = minimality.reduceSize();
		System.out.println(ts.getTests().size());

	}
}
