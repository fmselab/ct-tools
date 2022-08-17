package pMedici.main;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.ctWedge.CitModel;
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
	public void testSimple() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		extracted("examples/BOOLC_4_Simple.ctw");

	}

	public void extracted(String fileName) throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		Logger.getLogger(MinimalityTestSuiteValidator.class).setLevel(Level.ALL);
		//Logger.getLogger(ParameterSwitchToPairStrings.class).setLevel(Level.ALL);
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		pMedici.PRINT_DEBUG = true;
		pMedici.verb = true;
		TestBuilder.LockTCOnlyOnWriting = false;
		TestSuite testsuite = pMedici.generateTests(fileName,2);
		assertEquals(2, testsuite.getStrength());
		System.out.println("test suite size " + testsuite.getTests().size());
		MinimalityTestSuiteValidator minimality = new MinimalityTestSuiteValidator(testsuite);
		System.out.println(minimality.isMinimal());
		TestSuite reducedTS = minimality.reduceSize();
		System.out.println(reducedTS.getTests().size());
		SMTTestSuiteValidator validator = new SMTTestSuiteValidator(reducedTS);
		System.out.println(validator.isComplete());
	}
}
