package mantra.kali;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import org.pf4j.Extension;

import ctwedge.ctWedge.Bool;
import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Element;
import ctwedge.ctWedge.Enumerative;
import ctwedge.ctWedge.Parameter;
import ctwedge.ctWedge.Range;
import ctwedge.generator.util.ParameterSize;
import ctwedge.generator.util.Utility;
import ctwedge.util.Pair;
import mantra.model.Model;
import mantra.util.Order;

@Extension
public class KaliModel implements Model {
	
	CitModel citModel;
	

	/**
	 * Loads the model from file
	 * @param filename: the path of the file where the model is stored
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Override
	public void loadModelFromPath(String filename) {
		this.citModel =  Utility.loadModelFromPath(filename);
	}

	/**
	 * Convert the elements of this model in a MAP [ParameterIdentifier, List(Values)]
	 * 
	 * @param order: the order for parameter consideration during tuple generation
	 * @return the elements in a MAP [ParameterIdentifier, List(Values)]
	 */
	@Override
	public Map<Object, List<Object>> getElements(Order order) {
		assert order != null;
		Map<String, List<Object>> elementsMap = new HashMap<String, List<Object>>();		

		List<Parameter> parameters = new ArrayList<Parameter>();
		for(Parameter p: citModel.getParameters())
			parameters.add(p);
		
		// Order the parameters
		switch (order) {
		case RANDOM:
			Collections.shuffle(parameters);
			break;
		case IN_ORDER_SIZE_ASC:
			Collections.sort(parameters, new Comparator<Parameter>() {
				@Override
				public int compare(Parameter o1, Parameter o2) {
					return Integer.compare(ParameterSize.eInstance.doSwitch(o1), ParameterSize.eInstance.doSwitch(o2));
				}
			});
			break;
		case IN_ORDER_SIZE_DESC:
			Collections.sort(parameters, new Comparator<Parameter>() {
				@Override
				public int compare(Parameter o1, Parameter o2) {
					return Integer.compare(ParameterSize.eInstance.doSwitch(o2), ParameterSize.eInstance.doSwitch(o1));
				}
			});
			break;
		default:
			break;
		}

		// Fetch all the parameters
		for (Parameter p : parameters) {
			List<Object> s = new ArrayList<Object>();
			
			// If enumerative
			if (p instanceof Enumerative) {
				Enumerative e = (Enumerative)p;
				for (Element element : e.getElements()) {
					s.add(element.getName());
				}
			} else {
				// If boolean
				if (p instanceof Bool) {
					s.add(Boolean.TRUE);
					s.add(Boolean.FALSE);
				} else {
					// If range
					if (p instanceof Range) {
						Range e = (Range)p;
						int step = e.getStep() > 0 ? e.getStep() : 1;
						for (int i=e.getBegin(); i<=e.getEnd(); i=i+step)  {
							s.add(i);
						}
					} else {
						throw new RuntimeException ("Unknown type of parameter");
					}
				}
			}
			elementsMap.put(p.getName(), s);
		}
		
		// Returns the elements map
		return elementsMap.entrySet().stream().collect(Collectors.toMap(
				e -> e.getKey(), 
				e -> e.getValue()));
	}

	/**
	 * Returns the number of parameters of this model
	 * @return the number of parameters of this model
	 */
	@Override
	public int getNParams() {
		return citModel.getParameters().size();
	}

	/**
	 * Returns true if the model has constraints, otherwise false
	 * @return true if the model has constraints, otherwise false
	 */
	@Override
	public boolean getUseConstraints() {
		return getNParams() > 0;
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
		String header = "";
		for (Parameter param : citModel.getParameters()) {
			header += param.getName() + ";";
		}
		
		return header.substring(0, header.length() - 1) + "\n" + String.join("\n", tests);
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
	 * Returns the model in ctw format
	 * @return the model in ctw format
	 */
	@Override
	public CitModel getCitModel() {
		return citModel;
	}
}
