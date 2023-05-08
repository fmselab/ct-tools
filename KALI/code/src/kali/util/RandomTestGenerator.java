package kali.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.generator.util.ParameterElementsGetterAsStrings;
import scala.util.Random;

public class RandomTestGenerator {

	static int NTEST = 1000;

	
	public static HashSet<String> generateRandomTests(CitModel m) {
		HashSet<String> resList = new HashSet<String>();
		List<List<String>> paramValues = new ArrayList<List<String>>();
		Random r = new Random();
		
		// Visit the possible values
		for (Parameter p : m.getParameters()) {
			// Get all values
			List<String> valuesList = ParameterElementsGetterAsStrings.instance.doSwitch(p);
			paramValues.add(valuesList);
		}
		
		for (int i=0; i<NTEST; i++) {
			String thisTest = "";
			for (int j=0; j<paramValues.size(); j++) {
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
