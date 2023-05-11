package pMedici.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import ctwedge.util.TestSuite;
import ctwedge.util.validator.MinimalityTestSuiteValidator;
import ctwedge.util.validator.ParameterSwitchToPairStrings;
import ctwedge.util.validator.SMTTestSuiteValidator;
import ctwedge.util.validator.ValidatorException;
import pMedici.util.TestContext;

public class PMediciTestReduce {

	@Test
	public void test1() throws IOException, InterruptedException, ValidatorException{
		generateAndCheck("examples/CTComp/BOOLC_0.ctw");

	}
	@Test
	public void test2() throws IOException, InterruptedException, ValidatorException{
		generateAndCheck("examples/CTComp/MCAC_0.ctw");

	}
	@Test
	public void testSimple() throws IOException, InterruptedException, ValidatorException{
		generateAndCheck("examples/BOOLC_4_Simple.ctw");

	}

	public void generateAndCheck(String fileName) throws IOException, InterruptedException, ValidatorException {
		Logger.getLogger(MinimalityTestSuiteValidator.class).setLevel(Level.OFF);
		Logger.getLogger(ParameterSwitchToPairStrings.class).setLevel(Level.OFF);
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		PMedici.verb = true;
		TestContext.LockTCOnlyOnWriting = false;
		TestSuite testsuite = pMedici.generateTests(fileName,2, 1);
		assertEquals(2, testsuite.getStrength());
		SMTTestSuiteValidator validator = new SMTTestSuiteValidator(testsuite);
		assertTrue(validator.isValid());
		System.out.println("the produced test suite is valid");	
		assertTrue(validator.isComplete());
		System.out.println("the produced test suite is complete");	
		System.out.println("test suite size " + testsuite.getTests().size());
		MinimalityTestSuiteValidator minimality = new MinimalityTestSuiteValidator(testsuite);
		System.out.println("minimal ?" + minimality.isMinimal());
		TestSuite reducedTS = minimality.reduceSize();
		assertNotNull(reducedTS.getModel());
		System.out.println("reduced test size " + reducedTS.getTests().size() + " original " + testsuite.getTests().size());
		// 
		validator = new SMTTestSuiteValidator(reducedTS);
		assertTrue(validator.isComplete());
		System.out.println("the reduced test suite is complete");		
	}
}
