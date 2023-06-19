package randomMix;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.util.ModelUtils;
import ctwedge.util.Test;
import ctwedge.util.TestSuite;
import ctwedge.util.smt.SMTTestSuiteValidator;
import pMedici.main.PMedici;

public class RandomMixgenerator implements Callable<TestSuite> {

	private static final int N_THREADS = 1;

	PMedici pMedici;
	private CitModel model;
	int nSeeds;
	int strength;
	int usedSeeds;

	double cRnd;
	double cInc;
	int totalTuples;

	RandomMixgenerator(CitModel model, int nSeeds, int strength) {
		pMedici = new PMedici();
		this.model = model;
		assert model != null;
		this.nSeeds = nSeeds;
		this.strength = strength;
		this.usedSeeds = 0;
		this.cRnd = 0;
		this.cInc = 0;
		this.totalTuples=0;
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

		// Compute the tuples covered by the random part
		int tplsCovered;
		if (tests.size() > 0) {
			tplsCovered = getTuplesCoveredByTests(tests);
			cRnd = tplsCovered / tests.size();
		} else {
			tplsCovered = 0;
			cRnd = 0;
		}

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
			assert (found);
		}

		// Compute the tuples covered by the other part
		totalTuples = getTuplesCoveredByIncrementalTests(ts);
		if (ts.getTests().size() - tests.size() > 0) {			
			cInc = (totalTuples - tplsCovered) / (ts.getTests().size() - tests.size());
		} else {
			cInc = 0;
		}
		
		return ts;
	}

	private int getTuplesCoveredByIncrementalTests(TestSuite ts) {
		SMTTestSuiteValidator validator = new SMTTestSuiteValidator(ts);
		return validator.howManyTuplesCovers();
	}

	private int getTuplesCoveredByTests(ArrayList<Test> tests) {
		// Build the test suite object
		List<Map<Parameter, ?>> oldTests = new ArrayList<Map<Parameter, ?>>();
		for (Test t : tests) {
			Map<Parameter, String> thisTest = new HashMap<Parameter, String>();
			for (String param : t.getParameters()) {
				Parameter p = model.getParameters().stream().filter(x -> x.getName().equals(param)).findFirst().get();
				String v = t.get(param);
				thisTest.put(p, v);
			}
			oldTests.add(thisTest);
		}
		TestSuite ts = new TestSuite(model, oldTests);
		ts.setStrength(strength);
		SMTTestSuiteValidator validator = new SMTTestSuiteValidator(ts);
		return validator.howManyTuplesCovers();
	}

	/**
	 * Returns the number of actual used seeds
	 * 
	 * @return the number of actual used seeds
	 */
	public int getUsedSeeds() {
		return this.usedSeeds;
	}

	/**
	 * Returns the average number of tuples covered by random tests
	 * 
	 * @return the average number of tuples covered by random tests
	 */
	public double getCRnd() {
		return this.cRnd;
	}

	/**
	 * Returns the average number of tuples covered by incremental tests
	 * 
	 * @return the average number of tuples covered by incremental tests
	 */
	public double getCInc() {
		return this.cInc;
	}
	
	/**
	 * Returns the total number of tuples 
	 * 
	 * @return Returns the total number of tuples 
	 */
	public int getTotalTuples() {
		return this.totalTuples;
	}

}
