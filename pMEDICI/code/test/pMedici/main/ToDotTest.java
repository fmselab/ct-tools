package pMedici.main;

import static org.junit.Assert.*;

import org.colomoto.mddlib.MDDManager;
import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.ext.Utility;
import pMedici.util.ModelToMDDConverter;
import pMedici.util.Operations;

public class ToDotTest {

	@Test
	public void test() throws InterruptedException {
		CitModel model = Utility.loadModel(
				"Model prova\nParameters:\n a: Boolean; b: Boolean; c: Boolean;\nConstraints:\n # a -> b #\n");
		ModelToMDDConverter mc = new ModelToMDDConverter(model);
		MDDManager manager = mc.getMDD();
		int baseMDD = mc.getStartingNode();

		// Add to the baseNode the constraints
		baseMDD = Operations.updateMDDWithConstraints(manager, model, baseMDD);
		
		System.out.println(manager.dumpMDD(baseMDD));
	}

}
