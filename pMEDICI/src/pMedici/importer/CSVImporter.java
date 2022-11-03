package pMedici.importer;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class CSVImporter {

	public static final String delimiter = ",";

	/**
	 * Import the test suite from a .csv file
	 * 
	 * @param csvFilePath the path to the .csv file containing the test suite
	 * 
	 * @return a {@link Vector} where each position is one test case. A test case is
	 *         a {@link Map} collection that contains a {@link String} as key which
	 *         is the parameter name and {@link String} as value which is the
	 *         corresponding parameter value.
	 * @throws IOException
	 */
	public static Vector<Map<String, String>> read(String csvFilePath) throws IOException {
		// setting the environment for reading the file
		File file = new File(csvFilePath);
		FileReader fr = new FileReader(file);
		return readFromReader(fr);
	}
	
	/**
	 * Import the test suite from a reader
	 * 
	 * @param fr the reader containing the test suite
	 * 
	 * @return a {@link Vector} where each position is one test case. A test case is
	 *         a {@link Map} collection that contains a {@link String} as key which
	 *         is the parameter name and {@link String} as value which is the
	 *         corresponding parameter value.
	 * @throws IOException
	 */
	public static Vector<Map<String, String>> readFromReader(Reader fr) throws IOException {
		// in each position of the vector there is one test case
		Vector<Map<String, String>> tests = new Vector<Map<String, String>>();

		// setting the environment for reading the reader
		BufferedReader br = new BufferedReader(fr);
		String line = " ";

		// first line = parameters
		String[] parametersName;
		line = br.readLine();
		parametersName = line.split(delimiter);

		// other lines = test cases
		String[] testCase;
		int vectorIndex = 0;

		// reading one test case at a time and adding it in a new position of Vector
		while ((line = br.readLine()) != null) {
			testCase = line.split(delimiter);
			// creating a new Map in the current vectorIndex position
			tests.add(new TreeMap<String, String>());
			for (int i = 0; i < testCase.length; i++) {
				// retrieving the Map in position vectorIndex of vector
				// and setting one parameter at a time as pair <ParamName, ParamValue>
				tests.get(vectorIndex).put(parametersName[i], testCase[i]);
			}
			vectorIndex++;
		}

		br.close();

		return tests;

	}

}
