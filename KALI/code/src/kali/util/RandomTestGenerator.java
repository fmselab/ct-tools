package kali.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.util.ParameterElementsGetterAsStrings;
import scala.util.Random;

public class RandomTestGenerator {

	static int NTEST = 1000;
	List<List<String>> paramValues;

	public RandomTestGenerator(CitModel m) {
		paramValues = new ArrayList<List<String>>();
		// Visit the possible values
		for (Parameter p : m.getParameters()) {
			// Get all values
			List<String> valuesList = ParameterElementsGetterAsStrings.instance.doSwitch(p);
			paramValues.add(valuesList);
		}
	}

	public HashSet<String> generateRandomTests() {
		HashSet<String> resList = new HashSet<String>();
		Random r = new Random();

		for (int i = 0; i < NTEST; i++) {
			String thisTest = "";
			for (int j = 0; j < paramValues.size(); j++) {
				// Get all values
				List<String> valuesList = paramValues.get(j);

				// Extract a random value
				thisTest += valuesList.get(r.nextInt(valuesList.size())) + ";";
			}
			resList.add(thisTest);
		}
		return resList;
	}

}
