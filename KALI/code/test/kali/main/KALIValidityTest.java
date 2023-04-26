package kali.main;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
		Files.walk(Paths.get(CT_COMP_PATH)).filter(Files::isRegularFile).forEach(x -> {
			try {
				System.out.println("Generating test cases for " + x.getFileName().toString());
				testSingleFile(x.getFileName().toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SolverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	private void testSingleFile(String fileName)
			throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		TestBuilder.IN_TEST = true;
		KALI kali_tool = new KALI();
		TestSuite ts = kali_tool.doMain(new String[]{"2", CT_COMP_PATH + fileName});
		SMTTestSuiteValidator validator = new SMTTestSuiteValidator(ts);
		System.out.println(validator.howManyTestAreValid() + " valid tests out of " + ts.getTests().size());
		assertTrue(validator.howManyTestAreValid() == ts.getTests().size());
		assertTrue(validator.isValid());
		System.out.println("the produced test suite is valid");
		assertTrue(validator.isComplete());
		System.out.println("the produced test suite is complete");
	}
	
}
