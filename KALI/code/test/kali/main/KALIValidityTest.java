package kali.main;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.util.TestSuite;
import ctwedge.util.validator.SMTTestSuiteValidator;
import ctwedge.util.validator.ValidatorException;
import kali.threads.TestBuilder;

/* Checks the validity and completeness of all the benchmarks in which the tools
 * failed during the CT-Competition 2023 
 */
public class KALIValidityTest {

	static String CT_COMP_PATH = "../../CIT_Benchmark_Generator/Benchmarks_CITCompetition_2023/EvaluationPhase/CTWedge/";
	
	static {
		TestBuilder.IN_TEST = true;
	}
	
	@Test
	public void testUniform() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		testFileWithPrefix("UNIFORM_");
	}
	
	@Test
	public void testMCA() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		testFileWithPrefix("MCA_");
	}
	
	@Test
	public void testIndustrial() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		testFileWithPrefix("INDUSTRIAL_");
	}
	
	@Test
	public void testBOOLC() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		testFileWithPrefix("BOOLC_");
	}

	@Test
	public void testFM() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		testFileWithPrefix("FM_");
	}
	
	@Test
	public void testHIGHLYCONSTRAINED() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		testFileWithPrefix("HIGHLY_CONSTRAINED_");
	}
	
	@Test
	public void testNUMC() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		testFileWithPrefix("NUMC_");
	}
	
	@Test
	public void testMCAC() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		testFileWithPrefix("MCAC_");
	}
	
	@Test
	public void testCNF() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
		testFileWithPrefix("CNF_");
	}

	private void testFileWithPrefix(String prefix) throws IOException {
		Files.walk(Paths.get(CT_COMP_PATH)).filter(x -> x.getFileName().toString().contains(prefix)).forEach(x -> {
			try {
				System.out.println("Generating test cases for " + x.getFileName().toString());
				testSingleFile(x.getFileName().toString());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (SolverException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			} catch (ValidatorException e) {
				e.printStackTrace();
			}
		});
	}

	private void testSingleFile(String fileName)
			throws IOException, InterruptedException, SolverException, InvalidConfigurationException, ValidatorException {
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
