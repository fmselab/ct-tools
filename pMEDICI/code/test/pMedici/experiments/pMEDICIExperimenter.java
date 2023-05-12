package pMedici.experiments;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pMedici.main.PMedici;

/**
 * Class used to get experimental data from pMEDICI. It gets:
 * <ul>
 * <li> the time required for the generation </li>
 * <li> the size of the generated test suite (without reduction technique) </li>
 * <li> the size of the generated and reduced test suite </li>
 * </ul>
 * 
 * @author Luca Parimbelli
 *
 */
public class pMEDICIExperimenter {

	public static void main(String[] args) throws IOException, InterruptedException {

		// For each generation, 2 sheets will be created:
		// Sheet1 = sheetTime = it contains the execution time required by the
		// generations
		// Sheet2 = sheetSize = it containts the size of the generated TS
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheetTime = workbook.createSheet("TIME (ms) for generating TS");
		XSSFSheet sheetSize = workbook.createSheet("SIZE of the generated TS");
		XSSFSheet sheetReducedSize = workbook.createSheet("REDUCED SIZE of the TS");

		// Creating arrays with the name of all the models
		String[] AmbientAssistedLiving = new String[] { "AmbientAssistedLivingv1", "AmbientAssistedLivingv2" };
		String[] AutomotiveMultimedia = new String[] { "AutomotiveMultimediav1", "AutomotiveMultimediav2",
				"AutomotiveMultimediav3" };
		String[] Boeing = new String[] { "Boeingv1", "Boeingv2", "Boeingv3" };
		String[] CarBody = new String[] { "CarBodyv1", "CarBodyv2", "CarBodyv3", "CarBodyv4" };
		String[] LinuxKernel = new String[] { "LinuxKernelv1", "LinuxKernelv2", "LinuxKernelv3" };
		String[] ParkingAssistant = new String[] { "ParkingAssistantv1", "ParkingAssistantv2", "ParkingAssistantv3",
				"ParkingAssistantv4", "ParkingAssistantv5" };
		String[] PPU = new String[] { "PPUv1", "PPUv2", "PPUv3", "PPUv4", "PPUv5", "PPUv6", "PPUv7", "PPUv8", "PPUv9" };
		String[] SmartHotel = new String[] { "SmartHotelv1", "SmartHotelv2" };
		String[] Smartwatch = new String[] { "Smartwatchv1", "Smartwatchv2" };
		String[] WeatherStation = new String[] { "WeatherStationv1", "WeatherStationv2" };

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

		// First line .xlsx -> model name
		// Printing the name on both the sheets
		for (String name : evolutionModels) {
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(name);

			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(name);

			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(name);
		}

		// Other lines -> time required by TS generations
		// I'm running 1000 generations for each model and, for each generation,
		// the time is set in the corresponding cell of the current sheet.

		// The first generations require more time for setting up the environment.
		// For this reason, I will execute 100 generations at the beginning without
		// writing anything in the sheet. The real experiment starts after this 100
		// generations.

		// Input data variables
		String FMPath;
		String FMName;
		long startTime;
		long endTime;
		ConsoleManager consoleManger = new ConsoleManager(System.out);

		/* INITIAL GENERATIONS FOR ENVIRONMENT SET UP */
		FMName = "AmbientAssistedLivingv1";
		FMPath = "../pMEDICI/evolutionModels/AmbientAssistedLiving/AmbientAssistedLivingv1_ctwedge.ctw";
		System.out.println("------------------- **ENVIRONMENT SET UP GENERATIONS** -------------------");
		for (int i = 0; i < 200; i++) {
			// consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			// consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);
		}
		System.out.println();
		System.out.println("------------------- **END OF ENVIRONMENT SET UP GENERATIONS** -------------------");

		System.out.println();
		System.out.println("-- REAL TEST GENERATION --");

		/* REAL EXPERIMENT 1000 TEST GENERATIONS */
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

			/* EVOLUTION MODEL: AmbientAssistedLiving */
			// ------------------------------------------------------------------
			System.out.println("----------------------------------------------");

			/* v1 */
			FMName = "AmbientAssistedLivingv1";
			FMPath = "../pMEDICI/evolutionModels/AmbientAssistedLiving/AmbientAssistedLivingv1_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v2 */
			FMName = "AmbientAssistedLivingv2";
			FMPath = "../pMEDICI/evolutionModels/AmbientAssistedLiving/AmbientAssistedLivingv2_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			// ------------------------------------------------------------------

			/* EVOLUTION MODEL: AutomotiveMultimedia */
			// ------------------------------------------------------------------
			System.out.println("----------------------------------------------");

			/* v1 */
			FMName = "AutomotiveMultimediav1";
			FMPath = "../pMEDICI/evolutionModels/AutomotiveMultimedia/AutomotiveMultimediav1_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v2 */
			FMName = "AutomotiveMultimediav2";
			FMPath = "../pMEDICI/evolutionModels/AutomotiveMultimedia/AutomotiveMultimediav2_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v3 */
			FMName = "AutomotiveMultimediav3";
			FMPath = "../pMEDICI/evolutionModels/AutomotiveMultimedia/AutomotiveMultimediav3_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);
			// ------------------------------------------------------------------

			/* EVOLUTION MODEL: Boeing */
			// ------------------------------------------------------------------
			System.out.println("----------------------------------------------");

			/* v1 */
			FMName = "Boeingv1";
			FMPath = "../pMEDICI/evolutionModels/Boeing/Boeingv1_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v2 */
			FMName = "Boeingv2";
			FMPath = "../pMEDICI/evolutionModels/Boeing/Boeingv2_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v3 */
			FMName = "Boeingv3";
			FMPath = "../pMEDICI/evolutionModels/Boeing/Boeingv3_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);
			// ------------------------------------------------------------------

			/* EVOLUTION MODEL: CarBody */
			// ------------------------------------------------------------------
			System.out.println("----------------------------------------------");

			/* v1 */
			FMName = "CarBodyv1";
			FMPath = "../pMEDICI/evolutionModels/CarBody/CarBodyv1_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v2 */
			FMName = "CarBodyv2";
			FMPath = "../pMEDICI/evolutionModels/CarBody/CarBodyv2_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v3 */
			FMName = "CarBodyv2";
			FMPath = "../pMEDICI/evolutionModels/CarBody/CarBodyv3_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v4 */
			FMName = "CarBodyv2";
			FMPath = "../pMEDICI/evolutionModels/CarBody/CarBodyv4_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);
			// ------------------------------------------------------------------

			/* EVOLUTION MODEL: LinuxKernel */
			// ------------------------------------------------------------------
			System.out.println("----------------------------------------------");

			/* v1 */
			FMName = "LinuxKernelv1";
			FMPath = "../pMEDICI/evolutionModels/LinuxKernel/LinuxKernelv1_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v2 */
			FMName = "LinuxKernelv2";
			FMPath = "../pMEDICI/evolutionModels/LinuxKernel/LinuxKernelv2_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v3 */
			FMName = "LinuxKernelv3";
			FMPath = "../pMEDICI/evolutionModels/LinuxKernel/LinuxKernelv3_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);
			// ------------------------------------------------------------------

			/* EVOLUTION MODEL: ParkingAssistant */
			// ------------------------------------------------------------------
			System.out.println("----------------------------------------------");

			/* v1 */
			FMName = "ParkingAssistantv1";
			FMPath = "../pMEDICI/evolutionModels/ParkingAssistant/ParkingAssistantv1_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v2 */
			FMName = "ParkingAssistantv2";
			FMPath = "../pMEDICI/evolutionModels/ParkingAssistant/ParkingAssistantv2_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v3 */
			FMName = "ParkingAssistantv3";
			FMPath = "../pMEDICI/evolutionModels/ParkingAssistant/ParkingAssistantv3_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v4 */
			FMName = "ParkingAssistantv4";
			FMPath = "../pMEDICI/evolutionModels/ParkingAssistant/ParkingAssistantv4_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v5 */
			FMName = "ParkingAssistantv5";
			FMPath = "../pMEDICI/evolutionModels/ParkingAssistant/ParkingAssistantv5_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);
			// ------------------------------------------------------------------

			/* EVOLUTION MODEL: PPU */
			// ------------------------------------------------------------------
			System.out.println("----------------------------------------------");

			/* v1 */
			FMName = "PPUv1";
			FMPath = "../pMEDICI/evolutionModels/PPU/PPUv1_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v2 */
			FMName = "PPUv2";
			FMPath = "../pMEDICI/evolutionModels/PPU/PPUv2_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v3 */
			FMName = "PPUv3";
			FMPath = "../pMEDICI/evolutionModels/PPU/PPUv3_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v4 */
			FMName = "PPUv4";
			FMPath = "../pMEDICI/evolutionModels/PPU/PPUv4_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v5 */
			FMName = "PPUv5";
			FMPath = "../pMEDICI/evolutionModels/PPU/PPUv5_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v6 */
			FMName = "PPUv6";
			FMPath = "../pMEDICI/evolutionModels/PPU/PPUv6_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v7 */
			FMName = "PPUv7";
			FMPath = "../pMEDICI/evolutionModels/PPU/PPUv7_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v8 */
			FMName = "PPUv8";
			FMPath = "../pMEDICI/evolutionModels/PPU/PPUv8_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v9 */
			FMName = "PPUv9";
			FMPath = "../pMEDICI/evolutionModels/PPU/PPUv9_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);
			// ------------------------------------------------------------------

			/* EVOLUTION MODEL: SmartHotel */
			// ------------------------------------------------------------------
			System.out.println("----------------------------------------------");

			/* v1 */
			FMName = "SmartHotelv1";
			FMPath = "../pMEDICI/evolutionModels/SmartHotel/SmartHotelv1_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v2 */
			FMName = "SmartHotelv2";
			FMPath = "../pMEDICI/evolutionModels/SmartHotel/SmartHotelv2_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);
			// ------------------------------------------------------------------

			/* EVOLUTION MODEL: Smartwatch */
			// ------------------------------------------------------------------
			System.out.println("----------------------------------------------");

			/* v1 */
			FMName = "Smartwatchv1";
			FMPath = "../pMEDICI/evolutionModels/Smartwatch/Smartwatchv1_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v2 */
			FMName = "Smartwatchv2";
			FMPath = "../pMEDICI/evolutionModels/Smartwatch/Smartwatchv2_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* EVOLUTION MODEL: WeatherStation */
			// ------------------------------------------------------------------
			System.out.println("----------------------------------------------");

			/* v1 */
			FMName = "WeatherStationv1";
			FMPath = "../pMEDICI/evolutionModels/WeatherStation/WeatherStationv1_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

			/* v2 */
			FMName = "WeatherStationv2";
			FMPath = "../pMEDICI/evolutionModels/WeatherStation/WeatherStationv2_ctwedge.ctw";
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms" + "\nSize:"
					+ PMedici.testSuiteSize + "\nSize reduced:" + PMedici.reducedTestSuiteSize);

			// printing the time on the sheet1
			cellTime = rowTime.createCell(++columnTimeCount);
			cellTime.setCellValue(endTime);

			// printing the size on the sheet2
			cellSize = rowSize.createCell(++columnSizeCount);
			cellSize.setCellValue(PMedici.testSuiteSize);

			// printing the reduced size on the sheet3
			cellReducedSize = rowReducedSize.createCell(++columnReducedSizeCount);
			cellReducedSize.setCellValue(PMedici.reducedTestSuiteSize);

		}

		System.out.println();
		System.out.println("**Tests generation completed**");
		System.out.println("Number of threads utilized: " + PMedici.threadsNum);

		// Exporting the data to the .xlsx file
		FileOutputStream outputStream = new FileOutputStream(
				"../data_experiments/IncrementalGeneration/pMEDICI_Generation_from_scratch_noMergeDuplicatedTests.xlsx");
		workbook.write(outputStream);

		workbook.close();
	}

}
