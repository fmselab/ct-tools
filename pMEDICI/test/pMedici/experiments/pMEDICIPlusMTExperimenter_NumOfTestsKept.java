package pMedici.experiments;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pMedici.main.PMediciPlusMT;

/**
 * Class used to get the number of test cases which are kept within each
 * evolution from pMEDICI+
 * 
 * @author Luca Parimbelli
 *
 */
public class pMEDICIPlusMTExperimenter_NumOfTestsKept {
	private static final String EVOLUTION_MODELS_INPUT_FOLDER = "../pMEDICI/evolutionModels/";
	private static final String EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER = "../pMEDICI/evolutionModels_TestsCSV/";
	private static final String EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER = "../pMEDICI/evolutionModels_IncrementalTestsCSV/";

	public static void main(String[] args) throws IOException, InterruptedException {

		// For each generation, 1 sheet will be created:
		// Sheet1 = sheetKept = it contains the number of test cases from the old
		// test suite which are kept within the evolution
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheetKept = workbook.createSheet("Num of test cases kept");

		// Creating arrays with the name of all the models
		String[] AmbientAssistedLiving = new String[] { "AmbientAssistedLivingv2" };
		String[] AutomotiveMultimedia = new String[] { "AutomotiveMultimediav2", "AutomotiveMultimediav3" };
		String[] Boeing = new String[] { "Boeingv2", "Boeingv3" };
		String[] CarBody = new String[] { "CarBodyv2", "CarBodyv3", "CarBodyv4" };
		String[] LinuxKernel = new String[] { "LinuxKernelv2", "LinuxKernelv3" };
		String[] ParkingAssistant = new String[] { "ParkingAssistantv2", "ParkingAssistantv3", "ParkingAssistantv4",
				"ParkingAssistantv5" };
		String[] PPU = new String[] { "PPUv2", "PPUv3", "PPUv4", "PPUv5", "PPUv6", "PPUv7", "PPUv8", "PPUv9" };
		String[] SmartHotel = new String[] { "SmartHotelv2" };
		String[] Smartwatch = new String[] { "Smartwatchv2" };
		String[] WeatherStation = new String[] { "WeatherStationv2" };

		// Adding all names of all the models to the same array
		ArrayList<String> evolutionModels = new ArrayList<String>();
		evolutionModels.addAll(Arrays.asList(AmbientAssistedLiving));
		evolutionModels.addAll(Arrays.asList(AutomotiveMultimedia));
		evolutionModels.addAll(Arrays.asList(Boeing));
		evolutionModels.addAll(Arrays.asList(CarBody));
		evolutionModels.addAll(Arrays.asList(LinuxKernel));
		evolutionModels.addAll(Arrays.asList(ParkingAssistant));
		evolutionModels.addAll(Arrays.asList(PPU));
		evolutionModels.addAll(Arrays.asList(SmartHotel));
		evolutionModels.addAll(Arrays.asList(Smartwatch));
		evolutionModels.addAll(Arrays.asList(WeatherStation));

		// Variable set up for managing sheetTime
		int rowKeptCount = 0;
		int columnKeptCount = 0;
		Row rowKept = sheetKept.createRow(++rowKeptCount);
		Cell cellKept;

		// First line .xlsx -> model name
		// Printing the name on both the sheets
		for (String name : evolutionModels) {
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(name);
		}

		// Input data variables
		String oldTestSuiteFilePath = "";
		String evolvedModelPath = "";
		String exportFilePath = "";
		String ModelSubFolder;
		String ModelName;
		String oldTestSuite;
		String newTestSuite;

		long startTime;
		@SuppressWarnings("unused")
		long endTime;

		ConsoleManager consoleManger = new ConsoleManager(System.out);

		System.out.println("-- TEST GENERATION --");

		for (int i = 0; i < 1; i++) {
			System.out.println();
			System.out.println("ITERATION NUMBER : " + (i + 1) + "/1000");
			System.out.println();

			// going to a new row on .xlsx file sheetTime
			rowKept = sheetKept.createRow(++rowKeptCount);
			// reset columnKeptCount on the current row
			columnKeptCount = 0;

			/* INCREMENTAL generation: AmbientAssistedLiving */
			// ------------------------------------------------------------------
			ModelSubFolder = "AmbientAssistedLiving";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_AmbientAssistedLivingv1.csv";
			ModelName = "AmbientAssistedLivingv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_AmbientAssistedLivingv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			// ------------------------------------------------------------------

			/* INCREMENTAL generation: AutomotiveMultimedia */
			// ------------------------------------------------------------------
			ModelSubFolder = "AutomotiveMultimedia";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_AutomotiveMultimediav1.csv";
			ModelName = "AutomotiveMultimediav2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_AutomotiveMultimediav2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_AutomotiveMultimediav2.csv";
			ModelName = "AutomotiveMultimediav3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_AutomotiveMultimediav3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: Boeing */
			// ------------------------------------------------------------------
			ModelSubFolder = "Boeing";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_Boeingv1.csv";
			ModelName = "Boeingv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_Boeingv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_Boeingv2.csv";
			ModelName = "Boeingv3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_Boeingv3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: CarBody */
			// ------------------------------------------------------------------
			ModelSubFolder = "CarBody";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_CarBodyv1.csv";
			ModelName = "CarBodyv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_CarBodyv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();

			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the time on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_CarBodyv2.csv";
			ModelName = "CarBodyv3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_CarBodyv3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v3, new model = v4 */
			oldTestSuite = "CSVTest_CarBodyv3.csv";
			ModelName = "CarBodyv4_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_CarBodyv4.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			// ------------------------------------------------------------------

			/* INCREMENTAL generation: LinuxKernel */
			// ------------------------------------------------------------------
			ModelSubFolder = "LinuxKernel";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_LinuxKernelv1.csv";
			ModelName = "LinuxKernelv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_LinuxKernelv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_LinuxKernelv2.csv";
			ModelName = "LinuxKernelv3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_LinuxKernelv3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: ParkingAssistant */
			// ------------------------------------------------------------------
			ModelSubFolder = "ParkingAssistant";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_ParkingAssistantv1.csv";
			ModelName = "ParkingAssistantv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_ParkingAssistantv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_ParkingAssistantv2.csv";
			ModelName = "ParkingAssistantv3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_ParkingAssistantv3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v3, new model = v4 */
			oldTestSuite = "CSVTest_ParkingAssistantv3.csv";
			ModelName = "ParkingAssistantv4_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_ParkingAssistantv4.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v4, new model = v5 */
			oldTestSuite = "CSVTest_ParkingAssistantv4.csv";
			ModelName = "ParkingAssistantv5_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_ParkingAssistantv5.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);
			
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: PPU */
			// ------------------------------------------------------------------
			ModelSubFolder = "PPU";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_PPUv1.csv";
			ModelName = "PPUv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_PPUv2.csv";
			ModelName = "PPUv3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v3, new model = v4 */
			oldTestSuite = "CSVTest_PPUv3.csv";
			ModelName = "PPUv4_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv4.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v4, new model = v5 */
			oldTestSuite = "CSVTest_PPUv4.csv";
			ModelName = "PPUv5_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv5.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v5, new model = v6 */
			oldTestSuite = "CSVTest_PPUv5.csv";
			ModelName = "PPUv6_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv6.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v6, new model = v7 */
			oldTestSuite = "CSVTest_PPUv6.csv";
			ModelName = "PPUv7_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv7.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v7, new model = v8 */
			oldTestSuite = "CSVTest_PPUv7.csv";
			ModelName = "PPUv8_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv8.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);

			/* old test suite = v8, new model = v9 */
			oldTestSuite = "CSVTest_PPUv8.csv";
			ModelName = "PPUv9_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv9.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: SmartHotel */
			// ------------------------------------------------------------------
			ModelSubFolder = "SmartHotel";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_SmartHotelv1.csv";
			ModelName = "SmartHotelv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_SmartHotelv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: SmartWatch */
			// ------------------------------------------------------------------
			ModelSubFolder = "SmartWatch";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_SmartWatchv1.csv";
			ModelName = "SmartWatchv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_SmartWatchv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: WeatherStation */
			// ------------------------------------------------------------------
			ModelSubFolder = "WeatherStation";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_WeatherStationv1.csv";
			ModelName = "WeatherStationv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_WeatherStationv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Number of test cases kept: " + PMediciPlusMT.tcListInitialSize);

			// printing the size of the initial tcList on the sheet1
			cellKept = rowKept.createCell(++columnKeptCount);
			cellKept.setCellValue(PMediciPlusMT.tcListInitialSize);
			// ------------------------------------------------------------------

		}

		System.out.println();
		System.out.println("**Test generation completed**");
		System.out.println("Number of threads utilized: " + PMediciPlusMT.threadsNum);

		// Exporting the data to the .xlsx file
		FileOutputStream outputStream = new FileOutputStream(
				"experimentData/pMEDICIMT_Incremental_Generation_NumOfTestsKeptWithinTheEvolution.xlsx");
		workbook.write(outputStream);

		workbook.close();
	}

}
