import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.acts.ACTSTranslator;
import ctwedge.generator.exporter.CSVExporter;
import ctwedge.util.TestSuite;
import ctwedge.util.ext.Utility;
import pMedici.main.PMedici;
import pMedici.util.*;

public class CTEvoTest {

	static int[] PERCENTAGE_REMOVAL = { 0, 10, 20, 30, 40, 50, 60, 70, 80, 90 };
	static String PATH = "F:/Dati-Andrea/GitHub/ctwedge/ctwedge/ctwedge.parent/ctwedge.benchmarks/models/ISSTA11/";
	static String OUTPUT_FILE = "ExperimentsResults.csv";
	static int N_REP = 10;

	@Test
	public void test() throws IOException, InterruptedException, CloneNotSupportedException {
		File folder = new File(PATH);
		File[] listOfFiles = folder.listFiles();
		ACTSTranslator actsTranslator = new ACTSTranslator();
		Random random = new Random();
		CSVExporter t = new CSVExporter();

		TestContext.IN_TEST = true;

		for (File f : listOfFiles) {
			CitModel model = Utility.loadModelFromPath(f.getAbsolutePath());
			// Generate test suite with ACTS
			TestSuite ts1 = actsTranslator.getTestSuite(model, 2, false);
			ts1.setGeneratorName("ACTS");
			printStats(ts1, 0, 2);
			// Generate test suite with pMEDICI
			PMedici pMEDICI = new PMedici();
			TestSuite ts2 = pMEDICI.generateTests(f.getAbsolutePath(), 2, 0);
			ts2.setGeneratorName("pMEDICI");
			printStats(ts2, -1, 2);
			// Generate test suite with pMEDICI+
			for (int j = 0; j < N_REP; j++)
				for (int percentage : PERCENTAGE_REMOVAL) {
					TestSuite tsTemp = actsTranslator.getTestSuite(model, 2, false);
					System.out.println(" *** Removing " + percentage + " % of tests *** ");
					int nToBeRemoved = (int) (tsTemp.getTests().size() * (percentage / 100.0));
					System.out.println(
							" *** Removing " + nToBeRemoved + " of " + tsTemp.getTests().size() + " tests *** ");
					// Remove nToBeRemoved tests
					for (int i = 0; i < nToBeRemoved; i++) {
						tsTemp.getTests().remove(random.nextInt(tsTemp.getTests().size()));
					}
					System.out.println(" *** New size " + tsTemp.getTests().size() + "  *** ");
					// Now generate a new test suite starting from the old one
					t.generateOutput(tsTemp, "temp.txt");
					pMEDICI.setOldTs("temp.txt");
					TestSuite ts3 = pMEDICI.generateTests(f.getAbsolutePath(), 2, 0);
					ts3.setGeneratorName("pMEDICI+");
					printStats(ts3, percentage, 2);
				}
		}
	}
	
	@Test
	public void testStrength2To3() throws IOException, InterruptedException, CloneNotSupportedException {
		File folder = new File(PATH);
		File[] listOfFiles = folder.listFiles();
		ACTSTranslator actsTranslator = new ACTSTranslator();
		Random random = new Random();
		CSVExporter t = new CSVExporter();

		TestContext.IN_TEST = true;

		for (File f : listOfFiles) {
			CitModel model = Utility.loadModelFromPath(f.getAbsolutePath());
			// Generate test suite with ACTS
			TestSuite ts1 = actsTranslator.getTestSuite(model, 2, false);
			ts1.setGeneratorName("ACTS");
			printStats(ts1, 0, 2);
			// Generate test suite with pMEDICI
			PMedici pMEDICI = new PMedici();
			pMEDICI.setOldTs("");
			TestSuite ts2 = pMEDICI.generateTests(f.getAbsolutePath(), 3, 0);
			ts2.setGeneratorName("pMEDICI");
			printStats(ts2, -1, 3);
			// Generate test suite with pMEDICI+
			for (int j = 0; j < N_REP; j++)
				for (int percentage : PERCENTAGE_REMOVAL) {
					TestSuite tsTemp = actsTranslator.getTestSuite(model, 2, false);
					System.out.println(" *** Removing " + percentage + " % of tests *** ");
					int nToBeRemoved = (int) (tsTemp.getTests().size() * (percentage / 100.0));
					System.out.println(
							" *** Removing " + nToBeRemoved + " of " + tsTemp.getTests().size() + " tests *** ");
					// Remove nToBeRemoved tests
					for (int i = 0; i < nToBeRemoved; i++) {
						tsTemp.getTests().remove(random.nextInt(tsTemp.getTests().size()));
					}
					System.out.println(" *** New size " + tsTemp.getTests().size() + "  *** ");
					// Now generate a new test suite starting from the old one
					t.generateOutput(tsTemp, "temp.txt");
					pMEDICI.setOldTs("temp.txt");
					TestSuite ts3 = pMEDICI.generateTests(f.getAbsolutePath(), 3, 0);
					ts3.setGeneratorName("pMEDICI+");
					printStats(ts3, percentage, 3);
				}
		}
	}

	public void printStats(TestSuite t, int percentage, int strength) throws IOException {
		FileWriter fw = new FileWriter(OUTPUT_FILE, true);
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

}
