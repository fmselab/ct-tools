import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.acts.ACTSTranslator;
import ctwedge.generator.exporter.CSVExporter;
import ctwedge.generator.util.Utility;
import ctwedge.util.TestSuite;
import pMedici.main.PMedici;
import pMedici.safeelements.TestContext;

public class IWCT2023Test {

	static int N_REP = 10;
	static String PATH = "../ctwedge/ctwedge.parent/ctwedge.benchmarks/models/EMSE10/";
	static int[] PERCENTAGE_REMOVAL = { 0, 10, 20, 30, 40, 50, 60, 70, 80, 90 };
	static String TEMP_FILE_NAME = "temp.txt";
	
	@Test
	public void testCompleteTestSuite() throws IOException, InterruptedException {
		File folder = new File(PATH);
		File[] listOfFiles = folder.listFiles();
		Random random = new Random();
		CSVExporter t = new CSVExporter();
		String output_file = "ResultsCompleteTestSuite.csv";
		PMedici pMEDICI = new PMedici();

		ACTSTranslator.PRINT = false;
		TestContext.IN_TEST = true;
		
		for (File f : listOfFiles) {
			CitModel model = Utility.loadModelFromPath(f.getAbsolutePath());
			// Repeat the experiments N_REP times
			for (int i=0; i<N_REP; i++) {
				// Generate test suite with ACTS
				TestSuite ts1 = getACTSTestSuite(model, 2);
				// Remove a percentage of test cases
				for (int percentage : PERCENTAGE_REMOVAL) {
					List<ctwedge.util.Test> tempTs = ts1.getTests();
					int nToBeRemoved = (int) (tempTs.size() * (percentage / 100.0));
					// Remove nToBeRemoved tests
					for (int j = 0; j < nToBeRemoved; j++) {
						tempTs.remove(random.nextInt(tempTs.size()));
					}
					// Save the test suite to file
					TestSuite tsTemp = new TestSuite(toCSVcode(tempTs), model);
					tsTemp.populateTestSuite();
					t.generateOutput(tsTemp, TEMP_FILE_NAME);
					// --------------------------------
					// INCREMENTAL APPROACH
					// --------------------------------
					// Load the test suite into pMEDICI+ and generate the new test suite incrementally
					pMEDICI.setOldTs("temp.txt");
					TestSuite ts2 = pMEDICI.generateTests(f.getAbsolutePath(), 2, 0);
					ts2.setGeneratorName("pMEDICI+");
					printStats(ts2, percentage, 2, output_file);
					// --------------------------------
					// TRADITIONAL APPROACH
					// --------------------------------
					// Generate the test suite from scratch with pMEDICI
					pMEDICI.setOldTs("");
					TestSuite ts3 = pMEDICI.generateTests(f.getAbsolutePath(), 2, 0);
					ts3.setGeneratorName("pMEDICI");
					// Add the tests of the previous test suite and remove duplicates
					ts3.getTests().addAll(tempTs);
					// TODO: Remove duplicates
					printStats(ts3, percentage, 2, output_file);
				}
			}
		}
	}
	
	@Test
	public void testIncreaseStrength() throws IOException, InterruptedException {
		File folder = new File(PATH);
		File[] listOfFiles = folder.listFiles();
		CSVExporter t = new CSVExporter();
		String output_file = "ResultsIncreaseStrength.csv";
		PMedici pMEDICI = new PMedici();

		TestContext.IN_TEST = true;
		
		for (File f : listOfFiles) {
			// Repeat the experiments N_REP times
			for (int i=0; i<N_REP; i++) {
				// Generate test suite with pMEDICI for strength 2
				pMEDICI.setOldTs("");
				TestSuite ts = pMEDICI.generateTests(f.getAbsolutePath(), 2, 0);
				// Save the test suite to file
				t.generateOutput(ts, TEMP_FILE_NAME);
				// --------------------------------
				// INCREMENTAL APPROACH
				// --------------------------------
				// Load the test suite into pMEDICI+ and generate the new test suite incrementally
				pMEDICI.setOldTs("temp.txt");
				TestSuite ts2 = pMEDICI.generateTests(f.getAbsolutePath(), 3, 0);
				ts2.setGeneratorName("pMEDICI+");
				printStats(ts2, 0, 3, output_file);
				// --------------------------------
				// TRADITIONAL APPROACH
				// --------------------------------
				// Generate the test suite from scratch with pMEDICI with strength 3
				pMEDICI.setOldTs("");
				TestSuite ts3 = pMEDICI.generateTests(f.getAbsolutePath(), 3, 0);
				ts3.setGeneratorName("pMEDICI");
				printStats(ts3, 0, 3, output_file);
			}
		}
	}
	
	@Test
	public void testIncreaseCITCoverage() {
		fail("Not yet implemented");
	}
	
	public void printStats(TestSuite t, int percentage, int strength, String output_file) throws IOException {
		FileWriter fw = new FileWriter(output_file, true);
		BufferedWriter bw = new BufferedWriter(fw);

		if (t == null || t.getTests() == null)
			bw.write(t.getGeneratorName() + "," + t.getModel().getName() + "," + percentage + ",0,"
					+ t.getGeneratorTime() + "," + strength + ",");
		else
			bw.write(t.getGeneratorName() + "," + t.getModel().getName() + "," + percentage + "," + t.getTests().size()
					+ "," + t.getGeneratorTime() + "," + strength + ",");

		bw.newLine();
		bw.close();
	}

	private TestSuite getACTSTestSuite(CitModel model, int strength) {
		ACTSTranslator actsTranslator = new ACTSTranslator();
		TestSuite ts1 = actsTranslator.getTestSuite(model, strength, false);
		ts1.setGeneratorName("ACTS");
		return ts1;
	}
	
	private String toCSVcode(List<ctwedge.util.Test> input) {
		String s = "";
		int i=0;
		for (Entry<String,String> assignment : input.get(0).entrySet()) {
			if (i>0) {
				s+=","+assignment.getKey();
			} else {
				s+=assignment.getKey();
			}
			i++;
		}
		s+="\n";
		i=0;
		for (ctwedge.util.Test test : input) {
			i=0;
			for (Entry<String,String> assignment: test.entrySet()) {
				if (i>0) {
					s+=","+assignment.getValue();
				} else {
					s+=assignment.getValue();
				}
				i++;
			}
			s+="\n";
		}
		return s;
	}

}
