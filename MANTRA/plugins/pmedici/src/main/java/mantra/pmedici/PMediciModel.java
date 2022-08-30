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
public class PMediciModel implements Model{

	int baseMDD;

	MDDManager manager;

	CitModel citModel;

	TestModel testModel;

	/**
	 * Loads the model from file
	 * @param filename: the path of the file where the model is stored
	 * @throws InterruptedException
	 * @throws IOException
	 */
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

	/**
	 * Convert the elements of this model in a MAP [ParameterIdentifier, List(Values)]
	 * 
	 * @param order: the order for parameter consideration during tuple generation
	 * @return the elements in a MAP [ParameterIdentifier, List(Values)]
	 */
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

	/**
	 * Returns the number of parameters of this model
	 * @return the number of parameters of this model
	 */
	@Override
	public int getNParams() {
		return testModel.getnParams();
	}

	/**
	 * Returns true if the model has constraints, otherwise false
	 * @return true if the model has constraints, otherwise false
	 */
	@Override
	public boolean getUseConstraints() {
		return testModel.getUseConstraints();
	}

	/**
	 * Translates the output of this model format to a CSV one and returns it as a String
	 * 
	 * @param testCases: the test cases list
	 * 
	 * @return a {@link String} containing the test suite in a CSV format
	 * @throws IOException
	 */
	@Override
	public String translateOutputToString(Collection<String> tests) {
		return Operations.translateOutputToString(tests, citModel);
	}

	/**
	 * Prints the current tuple and returns it as string
	 * @param tuple: tuple to be printed
	 * @return a string with the printed tuple 
	 */
	@Override
	public String printTuple(Vector<Pair<Object, Object>> tuple) {
		String res = "";
		for (Pair<Object, Object> t : tuple) {
			res += ("<" + t.getFirst() + "," + t.getSecond() + ">");
		}
		return res;

	}

	/**
	 * Returns the MDD base
	 * @return the MDD base
	 */
	public int getBaseMDD() {
		return baseMDD;
	}

	/**
	 * Sets the MDD base
	 * @param baseMDD
	 */
	public void setBaseMDD(int baseMDD) {
		this.baseMDD = baseMDD;
	}

	/**
	 * Returns the MDD manager
	 * @return the manager
	 */
	public MDDManager getManager() {
		return manager;
	}

	/**
	 * Sets the MDD manager
	 * @param manager
	 */
	public void setManager(MDDManager manager) {
		this.manager = manager;
	}

	/**
	 * Returns the Test Model
	 * @return the test model
	 */
	public TestModel getTestModel() {
		return testModel;
	}

	/**
	 * Sets the Test Model
	 * @param testModel
	 */
	public void setTestModel(TestModel testModel) {
		this.testModel = testModel;
	}

	/**
	 * Returns the model in ctw format
	 * @return the model in ctw format
	 */
	@Override
	public CitModel getCitModel() {
		return citModel;
	}

}