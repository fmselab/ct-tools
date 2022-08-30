package mantra.util;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.TestSuite;

public class StatsTestSuite extends TestSuite {
	
	private int totTuples;
	private int coveredTuples;
	private int testNumber;
	private int threadNumber;
	private Order order;
	
	public StatsTestSuite(String ts, CitModel model, int strength, int totTuples, int coveredTuples, int testNumber,
			long generationTime, int threadNumber, Order order, String pluginId) {
		super(ts, model);
		setStrength(strength);
		setGeneratorName(pluginId);
		setGeneratorTime(generationTime);
		this.totTuples = totTuples;
		this.coveredTuples = coveredTuples;
		this.testNumber = testNumber;
		this.threadNumber = threadNumber;
		this.order = order;
	}

	public int getTotTuples() {
		return totTuples;
	}

	public int getCoveredTuples() {
		return coveredTuples;
	}
	
	public int getNotCoveredTuples() {
		return totTuples - coveredTuples;
	}

	public int getTestNumber() {
		return testNumber;
	}

	public int getThreadNumber() {
		return threadNumber;
	}

	public Order getOrder() {
		return order;
	}
	
}
