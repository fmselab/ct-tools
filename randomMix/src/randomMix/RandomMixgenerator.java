package randomMix;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.exporter.CSVExporter;
import ctwedge.util.ModelUtils;
import ctwedge.util.Test;
import ctwedge.util.TestSuite;
import pMedici.main.PMedici;

public class RandomMixgenerator implements Callable<TestSuite> {

	private static final int N_THREADS = 1;

	PMedici pMedici;
	private CitModel model;
	int nSeeds;
	int strength;

	RandomMixgenerator(CitModel model, int nSeeds, int strength) {
		pMedici = new PMedici();
		this.model = model;
		assert model != null;
		this.nSeeds = nSeeds;
		this.strength = strength;
	}

	@Override
	public TestSuite call() throws Exception {
		// Generate old tests
		long start = System.currentTimeMillis();
		ModelUtils mu = new ModelUtils(model);
		TestSuite tsOld = new TestSuite(model, null);
		List<Test> tests = new ArrayList<Test>();
		for (int i = 0; i < nSeeds; i++) {
			tests.add(mu.getRandomTestFromModel());
		}
		tsOld.setTests(tests);
		// Save old tests to temp file
		CSVExporter exporter = new CSVExporter();
		String fileName = "experimentsdata/testsuite_" + new SimpleDateFormat("yyyyMMddhhmmss'.csv'").format(new Date());
		exporter.generateOutput(tsOld, fileName);
		// Load old seeds
		pMedici.setOldTs(fileName);
		long preProcessingDuration = System.currentTimeMillis() - start;
		// Generate test suite
		TestSuite ts = pMedici.generateTests(model, strength, N_THREADS);
		// Update generation time
		ts.setGeneratorTime(ts.getGeneratorTime() + preProcessingDuration);
		new File(fileName).delete();
		return ts;
	}

}
