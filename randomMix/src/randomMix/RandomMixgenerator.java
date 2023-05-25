package randomMix;

import java.io.IOException;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.TestSuite;
import pMedici.main.PMedici;



public class RandomMixgenerator {

	private static final int N_THREADS = 4;
	
	PMedici pMedici;
	private CitModel model; 
	
	
	RandomMixgenerator(CitModel model){
		pMedici = new PMedici();
		this.model = model; 
		assert model != null;
	}
	
	
	TestSuite generate(int randomStop, int strength) throws IOException, InterruptedException{
		// generates randomStop random tests
		
		// fill the test suite using pMedici
		
		return pMedici.generateTests(model, strength, N_THREADS);
	}
	
	
}
