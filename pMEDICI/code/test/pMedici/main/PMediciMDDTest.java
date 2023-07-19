package pMedici.main;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.util.TestSuite;
import ctwedge.util.smt.SMTTestSuiteValidator;
import ctwedge.util.validator.ValidatorException;
import pMedici.safeelements.SafeQueue;
import pMedici.threads.TestBuilder;
import pMedici.util.TestContext;

public class PMediciMDDTest {

	@Test
	public void test() throws IOException, InterruptedException {
		// For avoid the AssertionError
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		pMedici.generateTests("examples/CTComp/UNIFORM_ALL_18.ctw", 2, 0);
	}

	@Test
	public void test2() throws IOException, InterruptedException {
		// For avoid the AssertionError
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		pMedici.generateTests("examples/BOOLC_4_Simple.ctw", 2, 0);
	}

	@Test
	public void test3() throws IOException, InterruptedException {
		// For avoid the AssertionError
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		SafeQueue.QUEUE_SIZE = 40;
		pMedici.generateTests("examples/MVM.ctw", 2, 0);
	}

	@Test
	public void test4() throws IOException, InterruptedException {
		// For avoid the AssertionError
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		SafeQueue.QUEUE_SIZE = 40;
		PMedici.verb = true;
		pMedici.generateTests("examples/CTComp/MCAC_5.ctw", 2, 0);
		SafeQueue.QUEUE_SIZE = 100;
		pMedici.generateTests("examples/CTComp/MCAC_5.ctw", 2, 0);
	}

	@Test
	public void test5() throws IOException, InterruptedException {
		// For avoid the AssertionError
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		PMedici.verb = false;
		pMedici.generateTests("examples/CTComp/MCAC_5.ctw", 2, 0);
	}

	@Test
	public void test6() throws IOException, InterruptedException {
		// For avoid the AssertionError
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		PMedici.verb = false;
		pMedici.generateTests(
				"E:/GitHub/CIT_Benchmark_Generator/Benchmarks_FollowUp_CITCompetition_2022/CTWedge/MCAC_16.ctw", 2, 0);
	}

	@Test
	public void testValidity() throws IOException, InterruptedException, ValidatorException {
		// Read the model
		String filename = "examples/CTComp/MCAC_4.ctw";
		generateAndCheck(filename, false);
	}

	@Test
	public void testValidity2() throws IOException, InterruptedException, ValidatorException {
		// Read the model
		String filename = "examples/BOOLC_4_Simple.ctw";
		generateAndCheck(filename, false);
	}

	@Test
	public void testValidity3() throws IOException, InterruptedException, ValidatorException {
		// Read the model
		String filename = "examples/BOOLC_4.ctw";
		generateAndCheck(filename, false);
	}

	@Test
	public void testAllFilesInCTComp() throws IOException {
		Path path = Paths.get("examples/CTComp/");
		Files.walk(path).filter(Files::isRegularFile).map(Path::toFile).filter(x -> x.getName().endsWith(".ctw"))
				.forEach(x -> {
					System.err.println(x.getAbsolutePath());
					try {
						generateAndCheck(x.getAbsolutePath(), false);
					} catch (IOException | InterruptedException | ValidatorException e) {
						e.printStackTrace();
					}
				});
	}

	@Test
	public void test7() throws IOException, InterruptedException {
		// For avoid the AssertionError
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		PMedici.verb = false;
		pMedici.generateTests(
				"/Users/andrea/Documents/GitHub/CIT_Benchmark_Generator/Benchmarks_FollowUp_CITCompetition_2022/CTWedge/UNIFORM_BOOLEAN_24.ctw",
				2, 0);
	}

	@Test
	public void testAllFilesInNewCTCompLinux() throws IOException {
		Path path = Paths
				.get("/Users/andrea/Documents/GitHub/CIT_Benchmark_Generator/Benchmarks_FollowUp_CITCompetition_2022/");
		Files.walk(path).filter(Files::isRegularFile).map(Path::toFile).filter(x -> (x.getName().endsWith(".ctw")
				&& x.getName().startsWith("MCAC_") && !x.getName().startsWith("MCAC_43"))).forEach(x -> {
					System.err.println(x.getAbsolutePath());
					if (!x.getAbsolutePath().endsWith("MCAC_0.ctw")) {
						try {
							generateAndCheck(x.getAbsolutePath(), false);
						} catch (IOException | InterruptedException | ValidatorException e) {
							e.printStackTrace();
						}
					}
				});
	}

	@Test
	public void testAllFilesInNewCTCompWindows() throws IOException {
		Path path = Paths.get(
				"F:\\Dati-Andrea\\GitHub\\CIT_Benchmark_Generator\\Benchmarks_CITCompetition_2023\\EvaluationPhase\\CTWedge");
		Files.walk(path).filter(Files::isRegularFile).map(Path::toFile).filter(x -> (x.getName().endsWith(".ctw")))
				.forEach(x -> {
					System.err.println(x.getAbsolutePath());
					try {
						generateAndCheck(x.getAbsolutePath(), false);
					} catch (IOException | InterruptedException | ValidatorException e) {
						e.printStackTrace();
					}
				});
	}

	@Test
	public void testCNF0() throws IOException, InterruptedException, ValidatorException {
		generateAndCheck("F:\\Dati-Andrea\\GitHub\\CIT_Benchmark_Generator\\Benchmarks_CITCompetition_2023\\EvaluationPhase\\CTWedge\\CNF_0.ctw", false);
	}
	
	@Test
	public void testOption() throws IOException, InterruptedException, ValidatorException {
		int nExec = 10;

		TestContext.LockTCOnlyOnWriting = false;
		for (int i = 1; i <= nExec; i++) {
			System.out.println("RecycleUnusedTestContexts = false && LockTCOnlyOnWriting = false");
			TestContext.LockTCOnlyOnWriting = false;
			TestBuilder.RecycleUnusedTestContexts = false;
			executeGenaration();
			System.out.println("RecycleUnusedTestContexts = true && LockTCOnlyOnWriting = false");
			TestContext.LockTCOnlyOnWriting = false;
			TestBuilder.RecycleUnusedTestContexts = true;
			executeGenaration();

			System.out.println("RecycleUnusedTestContexts = false && LockTCOnlyOnWriting = true");
			TestContext.LockTCOnlyOnWriting = true;
			TestBuilder.RecycleUnusedTestContexts = false;
			executeGenaration();
			System.out.println("RecycleUnusedTestContexts = true && LockTCOnlyOnWriting = true");
			TestContext.LockTCOnlyOnWriting = true;
			TestBuilder.RecycleUnusedTestContexts = true;
			executeGenaration();
		}
	}

	private void executeGenaration()
			throws FileNotFoundException, IOException, InterruptedException, ValidatorException {
		Logger.getLogger(SMTTestSuiteValidator.class).setLevel(Level.DEBUG);
		int nrun = 50;
		// Read the model
		String filename = "examples/CTComp/BOOLC_4.ctw";
		int[] sizes = new int[nrun];
		long start2 = System.currentTimeMillis();
		for (int i = 0; i < nrun; i++) {
			TestSuite ts = generateAndCheck(filename, false);
			sizes[i] = ts.getTests().size();
		}
		long end2 = System.currentTimeMillis();
		System.out.println("Elapsed Time in milli seconds: " + (end2 - start2));
		System.out.println(Arrays.toString(sizes));
		System.out.println(Arrays.stream(sizes).sum() / (double) nrun);
	}

	/**
	 *
	 * @param filename:     file containing the model (ctwedge or medici)
	 * @param saveandprint: print the test suite
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ValidatorException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	private TestSuite generateAndCheck(String filename, boolean saveandprint)
			throws FileNotFoundException, IOException, InterruptedException, ValidatorException {
		// For avoid the AssertionError
		TestContext.IN_TEST = true;
		TestContext.CloseTestContexts = false;
		PMedici pMedici = new PMedici();
		PMedici.verb = saveandprint;
		// generate the tests (as lines in a csv format)
		TestSuite ts = pMedici.generateTests(filename, 2, 0);
		ts.populateTestSuite();
		if (saveandprint) {
			PrintStream consoleStream = new PrintStream(new FileOutputStream(FileDescriptor.out));
			System.setOut(consoleStream);
			System.out.println(ts.toString());
		}
		// Define the validator
		SMTTestSuiteValidator tsv = new SMTTestSuiteValidator(ts);
		// tsv.setTestSuite(ts);
		// Save the number of covered tuples
		int covTuples = tsv.howManyTuplesCovers();
		//
		// The test suite must be valid and complete
		if (tsv.isValid()) {
			assertTrue(tsv.howManyTestAreValid() == ts.getTests().size());
		} else {
			fail("Not valid test suite");
		}
		if (tsv.isComplete()) {
			// Now remove tests until the covered tuples decreases
			while (ts.getTests().size() > 0) {
				ts.getTests().remove(0);
				// tsv.setTestSuite(ts);
				if (tsv.howManyTuplesCovers() < covTuples)
					break;
			}

			// If we still have tests
			if (ts.getTests().size() > 0 && tsv.isValid()) {
				// Check all the tests are valid
				assertTrue(tsv.howManyTestAreValid() == ts.getTests().size());
				// The test suite must be valid but not complete
				assertTrue(tsv.isValid());
			}
		} else {
			fail("Not complete test suite: " + filename);
		}

		System.err.println("Generated " + ts.getTests().size() + " tests");

		// Force the garbage collector
		System.gc();
		return ts;
	}

}
