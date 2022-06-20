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
		
		
		
	}
}