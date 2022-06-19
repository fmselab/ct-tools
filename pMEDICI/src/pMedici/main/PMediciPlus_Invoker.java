package pMedici.main;

import java.io.IOException;

public class PMediciPlus_Invoker {

	public static void main(String[] args) throws IOException, InterruptedException {
		String evolvedModelPath = "../pMEDICI/evolutionModels/AmbientAssistedLiving/AmbientAssistedLivingv2_ctwedge.ctw";
		String oldTestSuiteFilePath = "../pMEDICI/evolutionModels_TestsCSV/CSVTest_AmbientAssistedLivingv1.csv";
//		String evolvedModelPath = "../pMEDICI/evolutionModels/Boeing/Boeingv2_ctwedge.ctw";
//		String oldTestSuiteFilePath = "../pMEDICI/evolutionModels_TestsCSV/CSVTest_Boeingv1.csv";
		// PMedici.main(new String[] {"2", evolvedModelPath});
		// PMediciPlus.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath});
		 PMediciPlusMT.main(new String[] {"2", evolvedModelPath, oldTestSuiteFilePath});
	}
}