package pMedici.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import pMedici.util.TestContext;
import picocli.CommandLine;

public class PMediciIncrementalStrengthTest {

	@Test
	public void testModel() throws IOException, InterruptedException {
		Path path = Paths.get("examples/CTComp/");
		Files.walk(path).filter(Files::isRegularFile).map(Path::toFile).filter(x -> (x.getName().endsWith(".ctw") && x.getName().startsWith("BOOLC_")))
				.forEach(x -> {
					System.err.println(x.getAbsolutePath());
					try {
						testTraditional(x.getName());
						testIncremental(x.getName());
						testIncrementalStep(x.getName());
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				});
	}
	
	/**
	 * Generates a test suite directly with strength 5 and save the test suite every
	 * 100 milliseconds
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void testTraditional(String modelName) throws IOException, InterruptedException {
		TestContext.IN_TEST = true;
		String[] args = new String[] { "5", "examples/CTComp/" + modelName, "-savePartialStep", "100", "-prefix",
				"traditional_5_", "-output", "../data_experiments/IncrementalStrengthGeneration/", "-verb" };
		PMedici pMedici = new PMedici();
		new CommandLine(pMedici).execute(args);
	}

	/**
	 * Generates a test suite increasing the strength from 2 to 5 and save the test
	 * suite every 100 milliseconds
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void testIncremental(String modelName) throws IOException, InterruptedException {
		TestContext.IN_TEST = true;
		for (int i = 2; i <= 5; i++) {
			String[] args;
			if (i == 2)
				args = new String[] { Integer.toString(i), "examples/CTComp/" + modelName, "-savePartialStep", "100",
						"-prefix", "incremental_" + i + "_", "-output", "../data_experiments/IncrementalStrengthGeneration/", "-verb" };
			else
				args = new String[] { Integer.toString(i), "examples/CTComp/" + modelName, "-savePartialStep", "100",
						"-prefix", "incremental_" + i + "_", "-old",
						"../data_experiments/IncrementalStrengthGeneration/" + modelName + "/incremental_" + (i-1) + "_final.csv", "-output",
						"../data_experiments/IncrementalStrengthGeneration/", "-verb" };
			PMedici pMedici = new PMedici();
			new CommandLine(pMedici).execute(args);
		}
	}

	/**
	 * Generates a test suite with strength 5 by extending the one of strength 2 and
	 * save the test suite every 100 milliseconds
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void testIncrementalStep(String modelName) throws IOException, InterruptedException {
		TestContext.IN_TEST = true;

		// Strength 2
		String[] args = new String[] { "2", "examples/CTComp/" + modelName, "-savePartialStep", "100", "-prefix",
				"incrementalStep_" + 2 + "_", "-output", "../data_experiments/IncrementalStrengthGeneration/", "-verb" };
		PMedici pMedici = new PMedici();
		new CommandLine(pMedici).execute(args);

		// Strength 5
		args = new String[] { "2", "examples/CTComp/" + modelName, "-savePartialStep", "100", "-prefix",
				"incrementalStep_" + 5 + "_", "-old", "../data_experiments/IncrementalStrengthGeneration/" + modelName + "/incrementalStep_2_final.csv",
				"-output", "../data_experiments/IncrementalStrengthGeneration/", "-verb" };
		pMedici = new PMedici();
		new CommandLine(pMedici).execute(args);
	}

}
