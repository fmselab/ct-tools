package randomMix;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import ctwedge.ctWedge.CitModel;
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
	int usedSeeds;

	RandomMixgenerator(CitModel model, int nSeeds, int strength) {
		pMedici = new PMedici();
		this.model = model;
		assert model != null;
		this.nSeeds = nSeeds;
		this.strength = strength;
		this.usedSeeds = 0;
	}

	@Override
	public TestSuite call() throws Exception {
		// Generate old tests
		long start = System.currentTimeMillis();
		String fileName = "experimentsdata/testsuite_"
				+ new SimpleDateFormat("yyyyMMddhhmmss'.csv'").format(new Date());
		ArrayList<Test> tests = new ArrayList<Test>();
		TreeSet<String> testsString = new TreeSet<>();
		if (nSeeds > 0) {
			ModelUtils mu = new ModelUtils(model);
			for (int i = 0; i < nSeeds; i++) {
				Test t = mu.getRandomTestFromModel();
				if (!testsString.contains(t.toString())) {
					testsString.add(t.toString());
					tests.add(t);					
				}				
			}
			pMedici.setSeeds(tests);
			usedSeeds = tests.size();
		} else {
			pMedici.setSeeds(null);
		}
		long preProcessingDuration = System.currentTimeMillis() - start;
		
		// Generate test suite
		TestSuite ts = pMedici.generateTests(model, strength, N_THREADS);
		
		// Update generation time
		ts.setGeneratorTime(ts.getGeneratorTime() + preProcessingDuration);
		if (nSeeds > 0)
			new File(fileName).delete();
		
		// Check that the seeds are in the test suite
		for (Test t : tests) {
			boolean found = false;
			for (Test tTs : ts.getTests()) {
				if (tTs.toString().equals(t.toString())) {					
					found = true;
					break;
				}
			}
			assert(found);
		}
		
		return ts;
	}
	
	/**
	 * Returns the number of actual used seeds
	 * 
	 * @return the number of actual used seeds
	 */
	public int getUsedSeeds() {
		return this.usedSeeds;
	}

}
