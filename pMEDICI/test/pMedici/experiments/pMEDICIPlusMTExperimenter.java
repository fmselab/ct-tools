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
 * Class used to get experimental data from pMEDICI+. It gets:
 * <ul>
 * <li> the time required for the whole generation </li>
 * <li> the time required for the filling of the initial list of test contexts </li>
 * <li> the size of the generated test suite (without reduction technique) </li>
 * <li> the size of the generated and reduced test suite </li>
 * </ul>
 * 
 * @author Luca Parimbelli
 *
 */
@SuppressWarnings("deprecation")
public class pMEDICIPlusMTExperimenter {
	private static final String EVOLUTION_MODELS_INPUT_FOLDER = "../pMEDICI/evolutionModels/";
	private static final String EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER = "../pMEDICI/evolutionModels_TestsCSV/";
	private static final String EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER = "../pMEDICI/evolutionModels_IncrementalTestsCSV/";

	public static void main(String[] args) throws IOException, InterruptedException {

		// For each generation, 3 sheets will be created:
		// Sheet1 = sheetTime = it contains the execution time required by the
		// generations
		// Sheet2 = sheetSize = it containts the size of the generated TS not reduced
		// (it may containts duplicate tests)
		// Sheet3 = sheetSizeReduced = it containts the size of the reduced generated TS
		// (no duplicates)
		// Sheet4 = sheetInitialTime = it containts the time required by the
		// first part of the algorithm of pMEDICI+ (filling the initial test suite)
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheetTime = workbook.createSheet("TIME (ms) for generating TS");
		XSSFSheet sheetSize = workbook.createSheet("SIZE of the generated TS");
		XSSFSheet sheetReducedSize = workbook.createSheet("REDUCED SIZE of the TS");
		XSSFSheet sheetInitialTime = workbook.createSheet("TIME (ms) early filling");

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
		int rowTimeCount = 0;
		int columnTimeCount = 0;
		Row rowTime = sheetTime.createRow(++rowTimeCount);
		Cell cellTime;

		// Variable set up for managing sheetSize
		int rowSizeCount = 0;
		int columnSizeCount = 0;
		Row rowSize = sheetSize.createRow(++rowSizeCount);
		Cell cellSize;

		// Variable set up for managing sheetReducedSize
		int rowReducedSizeCount = 0;
		int columnReducedSizeCount = 0;
		Row rowReducedSize = sheetReducedSize.createRow(++rowReducedSizeCount);
		Cell cellReducedSize;

		// Variable set up for managing sheetInitialTime
		int rowInitialTimeCount = 0;
		int columnInitialTimeCount = 0;
		Row rowInitialTime = sheetInitialTime.createRow(++rowInitialTimeCount);
		Cell cellInitialTime;

		// First line .xlsx -> model name
		// Printing the name on both the sheets
		for (String name : evolutionModels) {
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(name);

			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(name);
			
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(name);

			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(name);
		}

		// Other lines -> time required by TS generations
		// I'm running 1000 generations for each model and, for each generation,
		// the time is set in the corresponding cell of the current sheet.

		// The first generations require more time for setting up the environment.
		// For this reason, I will execute 100 generations at the beginning without
		// writing anything in the sheet. The real experiment starts after this 100
		// generations.

		// Input data variables
		String oldTestSuiteFilePath = "";
		String evolvedModelPath = "";
		String exportFilePath = "";
		String ModelSubFolder;
		String ModelName;
		String oldTestSuite;
		String newTestSuite;

		long startTime;
		long endTime;

		ConsoleManager consoleManger = new ConsoleManager(System.out);

		/* INITIAL GENERATIONS FOR ENVIRONMENT SET UP */
		System.out.println("------------------- **ENVIRONMENT SET UP GENERATIONS** -------------------");
		for (int i = 0; i < 200; i++) {
			ModelSubFolder = "AmbientAssistedLiving";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_AmbientAssistedLivingv1.csv";
			ModelName = "AmbientAssistedLivingv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_AmbientAssistedLivingv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			startTime = System.currentTimeMillis();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			endTime = System.currentTimeMillis() - startTime;
			System.out.println(
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);

		}
		System.out.println();
		System.out.println("------------------- **END OF ENVIRONMENT SET UP GENERATIONS** -------------------");

		System.out.println();
		System.out.println("-- REAL TEST GENERATION --");

		for (int i = 0; i < 1000; i++) {
			System.out.println();
			System.out.println("ITERATION NUMBER : " + (i + 1) + "/1000");
			System.out.println();

			// going to a new row on .xlsx file sheetTime
			rowTime = sheetTime.createRow(++rowTimeCount);
			// reset columnTimeCount on the current row
			columnTimeCount = 0;

			// going to a new row on .xlsx file sheetSize
			rowSize = sheetSize.createRow(++rowSizeCount);
			// reset columnSizeCount on the current row
			columnSizeCount = 0;

			// going to a new row on .xlsx file sheetReducedSize
			rowReducedSize = sheetReducedSize.createRow(++rowReducedSizeCount);
			// reset columnSizeCount on the current row
			columnReducedSizeCount = 0;

			// going to a new row on .xlsx file sheetInitialTime
			rowInitialTime = sheetInitialTime.createRow(++rowInitialTimeCount);
			// reset columnSizeCount on the current row
			columnInitialTimeCount = 0;

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);

			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);

			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);
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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);
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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);
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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);

			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);
			
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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);
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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);

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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);
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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);
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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);
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
					"\n ** " + newTestSuite + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize not reduced:"
							+ PMediciPlusMT.testSuiteSize + "\nSize reduced:" + PMediciPlusMT.reducedTestSuiteSize +"\nTime testEarlyFillerThread: "+PMediciPlusMT.timeForOldTSFilling);
			
			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMediciPlusMT.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMediciPlusMT.reducedTestSuiteSize);
			
			// printing the reduced size on the sheet4
			cellInitialTime = rowInitialTime.createCell(++columnInitialTimeCount);
			cellInitialTime.setCellValue(PMediciPlusMT.timeForOldTSFilling);
			// ------------------------------------------------------------------

		}

		System.out.println();
		System.out.println("**Test generation completed**");
		System.out.println("Number of threads utilized: " + PMediciPlusMT.threadsNum);

		// Exporting the data to the .xlsx file
		FileOutputStream outputStream = new FileOutputStream(
				"experimentData/pMEDICIMT_Incremental_Generation_2Threads_noMergeDuplicatedTests5.xlsx");
		workbook.write(outputStream);

		workbook.close();
	}

}
