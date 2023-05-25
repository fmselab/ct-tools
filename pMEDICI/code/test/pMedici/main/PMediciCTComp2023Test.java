package pMedici.main;

import static org.junit.Assert.assertEquals;
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

public class PMediciCTComp2023Test {
	@Test
	public void testCNF12() throws IOException, InterruptedException, ValidatorException{
		generateAndCheckValidity("./examples/CTComp2023/CNF_12.ctw");	
	}
	
	@Test
	public void testMCAC18() throws IOException, InterruptedException, ValidatorException{
		generateAndCheckValidity("./examples/CTComp2023/MCAC_18.ctw");	
	}

	private void generateAndCheckValidity(String fileName) throws IOException, InterruptedException, ValidatorException{
		Logger.getLogger(MinimalityTestSuiteValidator.class).setLevel(Level.OFF);
		Logger.getLogger(ParameterSwitchToPairStrings.class).setLevel(Level.OFF);
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		PMedici.verb = true;
		TestContext.LockTCOnlyOnWriting = false;
		TestSuite testsuite = pMedici.generateTests(fileName,2, 1);
		assertEquals(2, testsuite.getStrength());		
		
		SMTTestSuiteValidator validator = new SMTTestSuiteValidator(testsuite);
		assertTrue(validator.howManyTestAreValid() == testsuite.getTests().size());
		assertTrue(validator.isValid());
		System.out.println("the produced test suite is valid");
		assertTrue(validator.isComplete());
		System.out.println("the produced test suite is complete");
	}
}
