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

public class pMEDICIExperimenter {

	public static void main(String[] args) throws IOException, InterruptedException {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("ACTS_Generation_from_scratch");

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

		int rowCount = 0;
		int columnCount = 0;
		Row row = sheet.createRow(++rowCount);

		// First line .xlsx -> model name
		for (String name : evolutionModels) {
			Cell cell = row.createCell(++columnCount);
			cell.setCellValue(name);
		}

		// Other lines -> time required by TS generations
		// I'm running 1000 generations for each model and, for each generation,
		// the time is set in the corresponding cell of the current sheet

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
		FMName = "AmbientAssistedLiving";
		FMPath = "../pMEDICI/evolutionModels/AmbientAssistedLiving/AmbientAssistedLivingv1_ctwedge.ctw";
		System.out.println("------------------- **ENVIRONMENT SET UP GENERATIONS** -------------------");
		for (int i = 0; i < 100; i++) {
			consoleManger.consolePrintingOff();
			startTime = System.currentTimeMillis();
			PMedici.main(new String[] { "2", FMPath });
			endTime = System.currentTimeMillis() - startTime;
			consoleManger.consolePrintingOn();
			System.out.println("\n ** " + FMName + "**\n" + "Elapsed time: " + endTime + " ms");
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

			// going to a new row on .xlsx file
			row = sheet.createRow(++rowCount);
			// reset columnCount on the current row
			columnCount = 0;
			
			// ------------------------------------------------------------------
			/* EVOLUTION MODEL: AmbientAssistedLiving */

		}

		// Exporting the data to the .xlsx file
		FileOutputStream outputStream = new FileOutputStream("experimentData/pMEDICI_Generation_from_scratch.xlsx");
		workbook.write(outputStream);

		workbook.close();
	}

}
