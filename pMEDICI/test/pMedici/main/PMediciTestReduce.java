package pMedici.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.util.TestSuite;
import ctwedge.util.validator.MinimalityTestSuiteValidator;
import ctwedge.util.validator.ParameterSwitchToPairStrings;
import ctwedge.util.validator.SMTTestSuiteValidator;
import pMedici.safeelements.TestContext;
import pMedici.threads.TestBuilder;

public class PMediciTestReduce {

	@Test
	public void test1() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		extracted("examples/CTComp/BOOLC_0.ctw");

	}
	@Test
	public void test2() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		extracted("examples/CTComp/MCAC_0.ctw");

	}
	@Test
	public void testSimple() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		extracted("examples/BOOLC_4_Simple.ctw");

	}

	public void extracted(String fileName) throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		Logger.getLogger(MinimalityTestSuiteValidator.class).setLevel(Level.OFF);
		Logger.getLogger(ParameterSwitchToPairStrings.class).setLevel(Level.OFF);
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		pMedici.verb = true;
		TestBuilder.LockTCOnlyOnWriting = false;
		TestSuite testsuite = pMedici.generateTests(fileName,2, 1);
		assertEquals(2, testsuite.getStrength());
		System.out.println("test suite size " + testsuite.getTests().size());
		MinimalityTestSuiteValidator minimality = new MinimalityTestSuiteValidator(testsuite);
		System.out.println("minimal ?" + minimality.isMinimal());
		TestSuite reducedTS = minimality.reduceSize();
		assertNotNull(reducedTS.getModel());
		System.out.println("reduced test size " + reducedTS.getTests().size() + " original " + testsuite.getTests().size());
		// 
		SMTTestSuiteValidator validator = new SMTTestSuiteValidator(reducedTS);
		assertTrue(validator.isComplete());
		System.out.println("the reduced test suite is complete");		
	}
}
