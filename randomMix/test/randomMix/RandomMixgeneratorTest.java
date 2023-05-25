package randomMix;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.ext.Utility;

public class RandomMixgeneratorTest {

	@Test
	public void testGenerate() throws IOException, InterruptedException {
		// read the model
		CitModel model = Utility.loadModelFromPath("models\\benchmark1.ctw");
		assert model != null;
		RandomMixgenerator generator = new RandomMixgenerator(model);
		
		// generate with an increasin number of random tests
		for (int i = 0; i < 10; i++) {
			generator.generate(i*10, 4);
		}
		
		
		
	}

}
