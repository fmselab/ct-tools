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
 * Class used to get experimental data from pMEDICI+. It calculates and saves
 * the value of the distances between the old test suite generated with ACTS and
 * the new test suite generated with pMEDICI+. This class generates 1000 tests
 * because ACTS is deterministic (hence the test suites are always the same),
 * meanwhile pMEDICI+ is not and so more values are needed to define a mean
 * value of the distances.
 * 
 * @author Luca Parimbelli
 *
 */
public class pMEDICIPlusMTExperimenter_DistanceCalculator {

	private static final String EVOLUTION_MODELS_INPUT_FOLDER = "../pMEDICI/evolutionModels/";
	private static final String EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER = "../pMEDICI/evolutionModels_TestsCSV/";
	private static final String EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER = "../pMEDICI/evolutionModels_IncrementalTestsCSV/";

	public static void main(String[] args) throws IOException, InterruptedException {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheetDistance = workbook.createSheet("Distances pMEDICI+");

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

		// Variable set up for managing sheetDistance
		int rowDistanceCount = 0;
		int columnDistanceCount = 0;
		Row rowDistance = sheetDistance.createRow(++rowDistanceCount);
		Cell cellDistance;

		// First line .xlsx -> model name
		// Printing the name on both the sheets
		for (String name : evolutionModels) {
			cellDistance = rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(name);
		}

		// Other lines -> distances between evolved and old test suites
		// I'm running 1000 experiments for each model and, for each experiment,
		// new test suites with pMEDICI+ are generated, the distances are calculated and
		// the distances value are set in the corresponding cell of the current sheet.

		// Input data variables
		String oldTestSuiteFilePath = "";
		String evolvedModelPath = "";
		String oldModelPath="";
		String exportFilePath = "";
		String ModelSubFolder;
		String ModelName;
		String oldModelName;
		String oldTestSuite;
		String newTestSuite;
		float distance;
		
		ConsoleManager consoleManger = new ConsoleManager(System.out);

		for (int i = 0; i < 1000; i++) {
			System.out.println();
			System.out.println("ITERATION NUMBER : " + (i + 1) + "/1000");
			System.out.println();

			// going to a new row on .xlsx file sheetDistance
			rowDistance= sheetDistance.createRow(++rowDistanceCount);
			// reset columnDistanceCount on the current row
			columnDistanceCount = 0;

			/* INCREMENTAL generation: AmbientAssistedLiving */
			// ------------------------------------------------------------------
			ModelSubFolder = "AmbientAssistedLiving";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_AmbientAssistedLivingv1.csv";
			oldModelName="AmbientAssistedLivingv1_ctwedge.ctw";
			ModelName = "AmbientAssistedLivingv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_AmbientAssistedLivingv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			// ------------------------------------------------------------------

			/* INCREMENTAL generation: AutomotiveMultimedia */
			// ------------------------------------------------------------------
			ModelSubFolder = "AutomotiveMultimedia";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_AutomotiveMultimediav1.csv";
			oldModelName="AutomotiveMultimediav1_ctwedge.ctw";
			ModelName = "AutomotiveMultimediav2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_AutomotiveMultimediav2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
			
			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_AutomotiveMultimediav2.csv";
			oldModelName="AutomotiveMultimediav2_ctwedge.ctw";
			ModelName = "AutomotiveMultimediav3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_AutomotiveMultimediav3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: Boeing */
			// ------------------------------------------------------------------
			ModelSubFolder = "Boeing";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_Boeingv1.csv";
			oldModelName="Boeingv1_ctwedge.ctw";
			ModelName = "Boeingv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_Boeingv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_Boeingv2.csv";
			oldModelName="Boeingv2_ctwedge.ctw";
			ModelName = "Boeingv3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_Boeingv3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: CarBody */
			// ------------------------------------------------------------------
			ModelSubFolder = "CarBody";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_CarBodyv1.csv";
			oldModelName="CarBodyv1_ctwedge.ctw";
			ModelName = "CarBodyv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_CarBodyv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_CarBodyv2.csv";
			oldModelName="CarBodyv2_ctwedge.ctw";
			ModelName = "CarBodyv3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_CarBodyv3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v3, new model = v4 */
			oldTestSuite = "CSVTest_CarBodyv3.csv";
			oldModelName="CarBodyv3_ctwedge.ctw";
			ModelName = "CarBodyv4_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_CarBodyv4.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			// ------------------------------------------------------------------

			/* INCREMENTAL generation: LinuxKernel */
			// ------------------------------------------------------------------
			ModelSubFolder = "LinuxKernel";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_LinuxKernelv1.csv";
			oldModelName="LinuxKernelv1_ctwedge.ctw";
			ModelName = "LinuxKernelv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_LinuxKernelv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_LinuxKernelv2.csv";
			oldModelName="LinuxKernelv2_ctwedge.ctw";
			ModelName = "LinuxKernelv3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_LinuxKernelv3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: ParkingAssistant */
			// ------------------------------------------------------------------
			ModelSubFolder = "ParkingAssistant";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_ParkingAssistantv1.csv";
			oldModelName="ParkingAssistantv1_ctwedge.ctw";
			ModelName = "ParkingAssistantv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_ParkingAssistantv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_ParkingAssistantv2.csv";
			oldModelName="ParkingAssistantv2_ctwedge.ctw";
			ModelName = "ParkingAssistantv3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_ParkingAssistantv3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v3, new model = v4 */
			oldTestSuite = "CSVTest_ParkingAssistantv3.csv";
			oldModelName="ParkingAssistantv3_ctwedge.ctw";
			ModelName = "ParkingAssistantv4_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_ParkingAssistantv4.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v4, new model = v5 */
			oldTestSuite = "CSVTest_ParkingAssistantv4.csv";
			oldModelName="ParkingAssistantv4_ctwedge.ctw";
			ModelName = "ParkingAssistantv5_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_ParkingAssistantv5.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: PPU */
			// ------------------------------------------------------------------
			ModelSubFolder = "PPU";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_PPUv1.csv";
			oldModelName="PPUv1_ctwedge.ctw";
			ModelName = "PPUv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v2, new model = v3 */
			oldTestSuite = "CSVTest_PPUv2.csv";
			oldModelName="PPUv2_ctwedge.ctw";
			ModelName = "PPUv3_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv3.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v3, new model = v4 */
			oldTestSuite = "CSVTest_PPUv3.csv";
			oldModelName="PPUv3_ctwedge.ctw";
			ModelName = "PPUv4_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv4.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v4, new model = v5 */
			oldTestSuite = "CSVTest_PPUv4.csv";
			oldModelName="PPUv4_ctwedge.ctw";
			ModelName = "PPUv5_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv5.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v5, new model = v6 */
			oldTestSuite = "CSVTest_PPUv5.csv";
			oldModelName="PPUv5_ctwedge.ctw";
			ModelName = "PPUv6_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv6.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v6, new model = v7 */
			oldTestSuite = "CSVTest_PPUv6.csv";
			oldModelName="PPUv6_ctwedge.ctw";
			ModelName = "PPUv7_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv7.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v7, new model = v8 */
			oldTestSuite = "CSVTest_PPUv7.csv";
			oldModelName="PPUv7_ctwedge.ctw";
			ModelName = "PPUv8_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv8.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			/* old test suite = v8, new model = v9 */
			oldTestSuite = "CSVTest_PPUv8.csv";
			oldModelName="PPUv8_ctwedge.ctw";
			ModelName = "PPUv9_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_PPUv9.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: SmartHotel */
			// ------------------------------------------------------------------
			ModelSubFolder = "SmartHotel";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_SmartHotelv1.csv";
			oldModelName="SmartHotelv1_ctwedge.ctw";
			ModelName = "SmartHotelv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_SmartHotelv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: SmartWatch */
			// ------------------------------------------------------------------
			ModelSubFolder = "SmartWatch";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_SmartWatchv1.csv";
			oldModelName="SmartWatchv1_ctwedge.ctw";
			ModelName = "SmartWatchv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_SmartWatchv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);
			// ------------------------------------------------------------------

			/* INCREMENTAL generation: WeatherStation */
			// ------------------------------------------------------------------
			ModelSubFolder = "WeatherStation";

			/* old test suite = v1, new model = v2 */
			oldTestSuite = "CSVTest_WeatherStationv1.csv";
			oldModelName="WeatherStationv1_ctwedge.ctw";
			ModelName = "WeatherStationv2_ctwedge.ctw";
			newTestSuite = "CSVIncrementalTest_WeatherStationv2.csv";

			oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
			oldModelPath =  EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + oldModelName;
			evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
			exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;

			consoleManger.consolePrintingOff();
			PMediciPlusMT.main(new String[] { "2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath });
			consoleManger.consolePrintingOn();
			distance=DistancesCalculatorLight.percTestSuitesDist_FromTestSuites_CTW(oldModelPath, oldTestSuiteFilePath, ",", evolvedModelPath, exportFilePath, ";");
			System.out.println("Distance TS-TS': " + distance + "%");
		
			// printing the time on the sheetDistance
			cellDistance= rowDistance.createCell(++columnDistanceCount);
			cellDistance.setCellValue(distance);

			// ------------------------------------------------------------------

		}

		System.out.println();
		System.out.println("**Test generation completed**");
		System.out.println("Number of threads utilized: " + PMediciPlusMT.threadsNum);

		// Exporting the data to the .xlsx file
		FileOutputStream outputStream = new FileOutputStream("experimentData/pMEDICIMT_Distances.xlsx");
		workbook.write(outputStream);

		workbook.close();
	}
}
