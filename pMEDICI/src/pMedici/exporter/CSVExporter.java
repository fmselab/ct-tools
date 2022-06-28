package pMedici.exporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Allows to export a testSuite in {@link String} format to an output file
 * 
 * @author Luca Parimbelli
 *
 */
public class CSVExporter {

	/**
	 * Export the test suite to a file
	 * 
	 * @param testSuite the {@link String} containing the generated test suite
	 * @param filePath  the path to the file where the test suite has to be exported
	 * 
	 * @throws IOException
	 */
	public static void export(String testSuite, String exportFilePath) throws IOException {
		File file = new File(exportFilePath);
		FileWriter fw;
		try {
			fw = new FileWriter(file);
			fw.append(testSuite);
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
