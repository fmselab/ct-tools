package randomMix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.TestSuite;
import ctwedge.util.ext.Utility;
import pMedici.main.PMedici;
import pMedici.util.TestContext;

public class RandomMixgeneratorTest {

	private static final int STRENGTH = 3;
	int nErrors = 0;

	@Test
	public void testGenerate() throws IOException, InterruptedException {
		TestContext.IN_TEST = true;
		PMedici.verb = false;

		File f = new File(
				"experimentsdata/experiments_" + new SimpleDateFormat("yyyyMMddhhmmss'.csv'").format(new Date()));
		// Output file
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write("FileName,t,k,SeedSize,UsedSeedSize,TSSize,TSTime,cRnd,cInc,totTuples,relCRnd,relCInc\n");

		Files.walk(Paths.get("./models")).forEach(x -> {
			try {
				if (x.toFile().getAbsolutePath().endsWith(".ctw"))
					experimentsOnModel(x.toFile().getAbsolutePath(), bw);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		});

		bw.close();

		System.err.println("Errors: " + nErrors);
	}

	static long fact(int i) {
		if (i <= 1) {
			return 1;
		}
		return i * fact(i - 1);
	}

	private void experimentsOnModel(String modelPath, BufferedWriter bw) throws IOException, InterruptedException {
		// Read the model
		CitModel model = Utility.loadModelFromPath(modelPath);
		assert model != null;

		// Generate with an increasing number of random tests
		for (int i = 0; i < 200; i++) {

			ExecutorService executor = Executors.newSingleThreadExecutor();
			RandomMixgenerator generator = new RandomMixgenerator(model, i, STRENGTH);
			Future<TestSuite> future = executor.submit(generator);
			try {
				TestSuite ts = future.get(300, TimeUnit.SECONDS);
				int k = model.getParameters().size();
				int t = ts.getStrength();
				bw.write(model.getName() + "," + t + "," + k + "," + i + "," + generator.getUsedSeeds() + ","
						+ ts.getTests().size() + "," + ts.getGeneratorTime() + "," + generator.getCRnd() + ","
						+ generator.getCInc() + "," + generator.getTotalTuples() + ","
						+ generator.getCRnd() / (fact(k) / (fact(t) * fact(k - t))) + ","
						+ generator.getCInc() / (fact(k) / (fact(t) * fact(k - t))) + "\n");
			} catch (TimeoutException e) {
				System.out.println("Time out has occurred");
				future.cancel(true);
				bw.write(model.getName() + ",timeout,timeout," + i
						+ ",timeout,timeout,timeout,timeout,timeout,timeout,timeout,timeout\n");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				nErrors++;
			}

			bw.flush();
		}
	}

}
