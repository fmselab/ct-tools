package mantra.model;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.pf4j.ExtensionPoint;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.Pair;
import mantra.util.Order;

public interface Model extends ExtensionPoint {

	/**
	 * Loads the model from file
	 * @param filename: the path of the file where the model is stored
	 * @throws InterruptedException
	 * @throws IOException
	 */
	void loadModelFromPath(String filename) throws InterruptedException, IOException;

	/**
	 * Convert the elements of this model in a MAP [ParameterIdentifier, List(Values)]
	 * 
	 * @param order: the order for parameter consideration during tuple generation
	 * @return the elements in a MAP [ParameterIdentifier, List(Values)]
	 */
	Map<Object, List<Object>> getElements(Order order);

	/**
	 * Returns the number of parameters of this model
	 * @return the number of parameters of this model
	 */
	int getNParams();

	/**
	 * Returns true if the model has constraints, otherwise false
	 * @return true if the model has constraints, otherwise false
	 */
	boolean getUseConstraints();

	/**
	 * Returns the model in ctw format
	 * @return the model in ctw format
	 */
	CitModel getCitModel();

	/**
	 * Translates the output of this model format to a CSV one and returns it as a String
	 * 
	 * @param testCases: the test cases list
	 * 
	 * @return a {@link String} containing the test suite in a CSV format
	 * @throws IOException
	 */
	String translateOutputToString(Collection<String> tests);

	/**
	 * Prints the current tuple and returns it as string
	 * @param tuple: tuple to be printed
	 * @return a string with the printed tuple 
	 */
	String printTuple(Vector<Pair<Object, Object>> tuple);
}