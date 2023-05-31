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

	@Test
	public void testGenerate() throws IOException, InterruptedException {
		TestContext.IN_TEST = true;
		PMedici.verb = false;

		File f = new File(
				"experimentsdata/experiments_" + new SimpleDateFormat("yyyyMMddhhmmss'.csv'").format(new Date()));
		// Output file
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write("FileName,Strength,SeedSize,TSSize,TSTime\n");

		Files.walk(Paths.get("./models")).forEach(x -> {
			try {
				if (x.toFile().getAbsolutePath().endsWith(".ctw"))
					experimentsOnModel(x.toFile().getAbsolutePath(), bw);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		});

		bw.close();
	}

	private void experimentsOnModel(String modelPath, BufferedWriter bw) throws IOException, InterruptedException {
		// Read the model
		CitModel model = Utility.loadModelFromPath(modelPath);
		assert model != null;

		// Generate with an increasing number of random tests
		for (int i = 0; i < 20; i++) {
			
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<TestSuite> future = executor.submit(new RandomMixgenerator(model, i*10, 4));
			try {
				TestSuite ts = future.get(300, TimeUnit.SECONDS);
				bw.write(model.getName() + "," + ts.getStrength() + "," + i * 10 + "," + ts.getTests().size() + ","
						+ ts.getGeneratorTime() + "\n");
			} catch (TimeoutException e) {
			    System.out.println("Time out has occurred");
			    future.cancel(true);
			    bw.write(model.getName() + ",timeout," + i * 10 + ",timeout,timeout\n");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			
			bw.flush();
		}
	}

}
