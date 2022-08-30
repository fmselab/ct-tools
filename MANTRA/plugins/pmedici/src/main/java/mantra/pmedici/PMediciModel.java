package mantra.pmedici;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.colomoto.mddlib.MDDManager;
import org.pf4j.Extension;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.generator.util.Utility;
import ctwedge.util.Pair;
import mantra.model.Model;
import mantra.pmedici.util.ModelToMDDConverter;
import mantra.pmedici.util.Operations;
import mantra.pmedici.util.TestModel;
import mantra.util.Order;

@Extension
public class PMediciModel implements Model {

	int baseMDD;

	MDDManager manager;

	CitModel citModel;

	TestModel testModel;

	@Override
	public void loadModelFromPath(String filename) throws InterruptedException, IOException {
		citModel = Utility.loadModelFromPath(filename);

		MediciCITGenerator gen = new MediciCITGenerator();
		MediciCITGenerator.OUTPUT_ON_STD_OUT_DURING_TRANSLATION = false;

		String mediciModel = gen.translateModel(citModel, false);

		testModel = Operations.readModelFromReader(new BufferedReader(new StringReader(mediciModel)));
		ModelToMDDConverter mc = new ModelToMDDConverter(testModel);
		manager = mc.getMDD();
		baseMDD = mc.getStartingNode();
	}

	@Override
	public Map<Object, List<Object>> getElements(Order order) {
		Map<Object, List<Object>> map = new HashMap<>();
		int[] bounds = testModel.getBounds();
		for (int param = 0; param < bounds.length; param++) {
			Vector<Object> values = new Vector<>();
			for (int val = 0; val < bounds[param]; val++) {
				values.add(val);
			}
			map.put(param, values);
		}

		return map;
	}

	@Override
	public int getNParams() {
		return testModel.getnParams();
	}

	@Override
	public boolean getUseConstraints() {
		return testModel.getUseConstraints();
	}

	@Override
	public String translateOutputToString(Collection<String> tests) {
		return Operations.translateOutputToString(tests, citModel);
	}

	@Override
	public String printTuple(Vector<Pair<Object, Object>> tuple) {
		String res = "";
		for (Pair<Object, Object> t : tuple) {
			res += ("<" + t.getFirst() + "," + t.getSecond() + ">");
		}
		return res;

	}

	public int getBaseMDD() {
		return baseMDD;
	}

	public void setBaseMDD(int baseMDD) {
		this.baseMDD = baseMDD;
	}

	public MDDManager getManager() {
		return manager;
	}

	public void setManager(MDDManager manager) {
		this.manager = manager;
	}

	public TestModel getTestModel() {
		return testModel;
	}

	public void setTestModel(TestModel testModel) {
		this.testModel = testModel;
	}

	@Override
	public CitModel getCitModel() {
		return citModel;
	}

}