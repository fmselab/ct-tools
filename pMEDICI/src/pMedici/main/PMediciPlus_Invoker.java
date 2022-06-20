package pMedici.main;

import java.io.IOException;

public class PMediciPlus_Invoker {
	
	private static final String EVOLUTION_MODELS_INPUT_FOLDER = "../pMEDICI/evolutionModels/";
	private static final String EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER = "../pMEDICI/evolutionModels_TestsCSV/";
	private static final String EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER = "../pMEDICI/evolutionModels_IncrementalTestsCSV/";

	public static void main(String[] args) throws IOException, InterruptedException {
		
//		Example of different PMedici version invocations:
//		PMedici.main(new String[] {"2", evolvedModelPath});
//		PMediciPlus.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath});
//		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath});
		
		String oldTestSuiteFilePath="";
		String evolvedModelPath="";
		String exportFilePath="";
				
		String ModelSubFolder;
		String ModelName;
		String oldTestSuite;
		String newTestSuite;
	
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
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
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
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v2, new model = v3 */
		oldTestSuite = "CSVTest_AutomotiveMultimediav2.csv";
		ModelName = "AutomotiveMultimediav3_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_AutomotiveMultimediav3.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
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
		
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v2, new model = v3 */
		oldTestSuite = "CSVTest_Boeingv2.csv";
		ModelName = "Boeingv3_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_Boeingv3.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
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
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v2, new model = v3 */
		oldTestSuite = "CSVTest_CarBodyv2.csv";
		ModelName = "CarBodyv3_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_CarBodyv3.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v3, new model = v4 */
		oldTestSuite = "CSVTest_CarBodyv3.csv";
		ModelName = "CarBodyv4_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_CarBodyv4.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
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
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v2, new model = v3 */
		oldTestSuite = "CSVTest_LinuxKernelv2.csv";
		ModelName = "LinuxKernelv3_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_LinuxKernelv3.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
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
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v2, new model = v3 */
		oldTestSuite = "CSVTest_ParkingAssistantv2.csv";
		ModelName = "ParkingAssistantv3_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_ParkingAssistantv3.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v3, new model = v4 */
		oldTestSuite = "CSVTest_ParkingAssistantv3.csv";
		ModelName = "ParkingAssistantv4_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_ParkingAssistantv4.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v4, new model = v5 */
		oldTestSuite = "CSVTest_ParkingAssistantv4.csv";
		ModelName = "ParkingAssistantv5_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_ParkingAssistantv5.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
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
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v2, new model = v3 */
		oldTestSuite = "CSVTest_PPUv2.csv";
		ModelName = "PPUv3_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_PPUv3.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v3, new model = v4 */
		oldTestSuite = "CSVTest_PPUv3.csv";
		ModelName = "PPUv4_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_PPUv4.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v4, new model = v5 */
		oldTestSuite = "CSVTest_PPUv4.csv";
		ModelName = "PPUv5_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_PPUv5.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v5, new model = v6 */
		oldTestSuite = "CSVTest_PPUv5.csv";
		ModelName = "PPUv6_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_PPUv6.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v6, new model = v7 */
		oldTestSuite = "CSVTest_PPUv6.csv";
		ModelName = "PPUv7_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_PPUv7.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v7, new model = v8 */
		oldTestSuite = "CSVTest_PPUv7.csv";
		ModelName = "PPUv8_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_PPUv8.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		
		/* old test suite = v8, new model = v9 */
		oldTestSuite = "CSVTest_PPUv8.csv";
		ModelName = "PPUv9_ctwedge.ctw";
		newTestSuite = "CSVIncrementalTest_PPUv9.csv";
		
		oldTestSuiteFilePath = EVOLUTION_MODELS_OLDTESTS_INPUT_FOLDER + oldTestSuite;
		evolvedModelPath = EVOLUTION_MODELS_INPUT_FOLDER + ModelSubFolder + "/" + ModelName;
		exportFilePath = EVOLUTION_MODELS_INCREMENTALTESTS_OUTPUT_FOLDER + newTestSuite;
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
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
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
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
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
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
		
		PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath, exportFilePath});
		// ------------------------------------------------------------------		
		
		
	}
}