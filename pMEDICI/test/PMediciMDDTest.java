import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.util.Utility;
import ctwedge.util.TestSuite;
import ctwedge.util.validator.SMTTestSuiteValidator;
import pMedici.main.PMedici;
import pMedici.safeelements.TestContext;

public class PMediciMDDTest {

	@Test
	public void test() throws IOException, InterruptedException {
		// For avoid the AssertionError
		TestContext.IN_TEST = true;
		PMedici.main(new String[] {"2", "C:\\Users\\Andrea_PC\\Desktop\\CTComp\\CTComp\\UNIFORM_ALL_18.ctw"});
	}
	
	
	@Test
	public void test2() throws IOException, InterruptedException {
		// For avoid the AssertionError
		TestContext.IN_TEST = true;
		PMedici.main(new String[] {"2", "examples/BOOLC_4_Simple.ctw"});
	}
	
	
	
	@Test
	public void testValidity() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
	    // Read the model
	    String filename = "C:\\Users\\Andrea_PC\\Desktop\\CTComp\\CTComp\\MCAC_4.ctw";
		generateAndCheck(filename);
	}

	@Test
	public void testValidity2() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
	    // Read the model
	    String filename = "examples/BOOLC_4_Simple.ctw";
		generateAndCheck(filename);
	}

	@Test
	public void testValidity3() throws IOException, InterruptedException, SolverException, InvalidConfigurationException {
	    // Read the model
	    String filename = "examples/BOOLC_4.ctw";
		generateAndCheck(filename);
	}
	

	private void generateAndCheck(String filename) throws FileNotFoundException, IOException, InterruptedException,
			SolverException, InvalidConfigurationException {
		// For avoid the AssertionError
		TestContext.IN_TEST = true;
		// Change the output
	    File file = new File("ts_out.txt");
	    PrintStream stream = new PrintStream(file);
	    System.setOut(stream);
	    CitModel model = Utility.loadModelFromPath(filename);
	    PMedici.main(new String[] {"2", filename});
	    
	    // Read the file containig the test suite
	    File file2 = new File("ts_out.txt");
	    String csvModel = "";
	    Scanner sc = new Scanner(file2);
	    // Skip the first line
	    sc.nextLine();
	    while (sc.hasNextLine())
	    	csvModel += sc.nextLine() + "\n";
	    sc.close();
	    
	    // Produced test suite
	    TestSuite ts = new TestSuite(csvModel, model); 
	    ts.populateTestSuite();
	    ts.setStrength(2);
	    PrintStream consoleStream = new PrintStream(new FileOutputStream(FileDescriptor.out));
	    System.setOut(consoleStream);
	    System.out.println(ts.toString());
	    
		// Define the validator
		SMTTestSuiteValidator tsv = new SMTTestSuiteValidator();
		tsv.setTestSuite(ts);
				
		// Save the number of covered tuples
		int covTuples = tsv.howManyTuplesCovers();
				
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
				tsv.setTestSuite(ts);
					
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
			fail("Not complete test suite");
		}
	}
	
}
