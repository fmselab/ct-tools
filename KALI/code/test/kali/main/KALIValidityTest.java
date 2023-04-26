package kali.main;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.util.TestSuite;
import ctwedge.util.validator.SMTTestSuiteValidator;
import kali.threads.TestBuilder;

public class KALIValidityTest {

	static String CT_COMP_PATH = "../../CIT_Benchmark_Generator/Benchmarks_CITCompetition_2023/EvaluationPhase/CTWedge/";
	
	static {
		TestBuilder.IN_TEST = true;
	}
	
	/* Checks the validity and completeness of all the benchmarks in which the tools
	 * failed during the CT-Competition 2023 
	 */
	@Test
	public void test1() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		TestBuilder.IN_TEST = true;
		KALI kali_tool = new KALI();
		TestSuite ts = kali_tool.doMain(new String[]{"2", CT_COMP_PATH + "NUMC_0.ctw"});
		SMTTestSuiteValidator validator = new SMTTestSuiteValidator(ts);
		assertTrue(validator.howManyTestAreValid() == ts.getTests().size());
		assertTrue(validator.isValid());
		System.out.println("the produced test suite is valid");
		assertTrue(validator.isComplete());
		System.out.println("the produced test suite is complete");
	}
	
}
