package pMedici.main;

import static org.junit.Assert.assertEquals;
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

public class PMediciCTComp2023Test {
	@Test
	public void testCNF12() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		generateAndCheckValidity("./examples/CTComp2023/CNF_12.ctw");	
	}

	private void generateAndCheckValidity(String fileName) throws IOException, InterruptedException {
		Logger.getLogger(MinimalityTestSuiteValidator.class).setLevel(Level.OFF);
		Logger.getLogger(ParameterSwitchToPairStrings.class).setLevel(Level.OFF);
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		pMedici.verb = true;
		TestBuilder.LockTCOnlyOnWriting = false;
		TestSuite testsuite = pMedici.generateTests(fileName,2, 1);
		assertEquals(2, testsuite.getStrength());
		SMTTestSuiteValidator validator = new SMTTestSuiteValidator(testsuite);
		assertTrue(validator.isValid());
		System.out.println("the produced test suite is valid");
	}
}
