package pMedici.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;
import java.util.stream.Collectors;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.PathSearcher;
import org.colomoto.mddlib.operators.MDDBaseOperators;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.util.NotConvertableModel;
import ctwedge.util.ParameterSize;
import ctwedge.util.ParameterValuesToInt;

public class Operations {

	/**
	 * Return the tuple given as parameter as a string
	 * 
	 * @param tuple: the tuple to be printed
	 * @return the string representing the tuple
	 */
	public static String printTuple(Vector<Pair<Integer, Integer>> tuple) {
		String res = "";
		for (Pair<Integer, Integer> t : tuple) {
			res += ("<" + t.getFirst() + "," + t.getSecond() + ">");
		}
		return res;
	}

	/**
	 * Reads the file containing the combinatorial model and converts it into a
	 * TestModel object
	 * 
	 * @param path: the path of the file
	 * @return the TestModel object
	 * @throws IOException
	 */
	public static TestModel readFile(String path) throws IOException {
		// Open the model file
		File modelFile = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(modelFile));
		return readModelFromReader(reader);
	}

	/**
	 * Reads the combinatorial model from a reader and converts it into a TestModel
	 * object
	 * 
	 * @param reader: the reader
	 * @return the TestModel object
	 * @throws IOException
	 */
	public static TestModel readModelFromReader(BufferedReader reader) throws IOException {
		int nParams = 0;
		int strength = 0;
		int[] bounds;
		int nConstraints = 0;
		String tempStr;
		ArrayList<Constraint> constraintList = new ArrayList<Constraint>();

		// The first line contains the strength
		strength = Integer.parseInt(reader.readLine());

		// The second line contains the number of parameters
		nParams = Integer.parseInt(reader.readLine());

		// The third line contains the bounds
		bounds = tokenizeBounds(reader.readLine(), nParams);

		// The fourth row contains the number of constraints
		tempStr = reader.readLine();
		if (tempStr != null) {
			nConstraints = Integer.parseInt(tempStr);

			// Now read all the constraints
			for (int i = 0; i < nConstraints; i++) {
				tempStr = reader.readLine();
				constraintList.add(getConstraintFromString(tempStr));
			}
		}

		reader.close();

		return new TestModel(nParams, bounds, strength, constraintList.size() > 0, constraintList);
	}

	/**
	 * Converts the string in a Constraint
	 * 
	 * @param tempStr: the string representing the constraint
	 * @return the constraint
	 */
	private static Constraint getConstraintFromString(String tempStr) {
		String[] tokens = tempStr.split("\\s");
		Constraint c = new Constraint();

		for (int i = tokens.length - 1; i >= 0; i--) {
			ConstraintElement ce = new ConstraintElement();
			if (tokens[i].equals("+") || tokens[i].equals("*") || tokens[i].equals("-"))
				ce.setOperator(tokens[i]);
			else
				ce.setValue(Integer.parseInt(tokens[i]));
			c.addElement(ce);
		}

		return c;
	}

	/**
	 * Returns the bounds vector
	 * 
	 * @param readLine: the line read from the model
	 * @return the array containing the bounds
	 */
	private static int[] tokenizeBounds(String readLine, int nParams) {
		int[] bounds = new int[nParams];
		String[] tokens = readLine.split("\\s");

		if (tokens.length != nParams)
			throw new RuntimeException("Error in bounds definition in file");

		for (int i = 0; i < nParams; i++) {
			bounds[i] = Integer.parseInt(tokens[i]);
		}

		return bounds;
	}

	/**
	 * Updates the MDD by inserting all the constraints previously loaded in the
	 * test model
	 * 
	 * @param manager:  the MDD manager
	 * @param m:        the test model
	 * @param baseNode: the base MDD
	 * @return the identifier of the MDD
	 * @throws InterruptedException
	 */
	public static int updateMDDWithConstraints(MDDManager manager, TestModel m, int baseNode)
			throws InterruptedException {
		// Fetch all the constraints
		for (Constraint c : m.constraintList) {
			// Fetch all the elements inside the constraint
			Stack<Integer> tPList = new Stack<Integer>();
			while (!c.constraint.isEmpty()) {
				ConstraintElement ce = c.getElement();
				if (ce.isOperator()) {
					int newNode;
					int n1 = -1;
					int n2 = -1;
					switch (ce.operator) {
					case "+":
						// OR Operation
						assert (tPList.size() >= 2);
						n1 = tPList.pop();
						n2 = tPList.pop();
						ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
						newNode = MDDBaseOperators.OR.combine(manager, n1, n2);
						ExtendedSemaphore.OPERATION_SEMAPHORE.release();
						tPList.push(newNode);
						break;
					case "*":
						// AND Operation
						assert (tPList.size() >= 2);
						n1 = tPList.pop();
						n2 = tPList.pop();
						ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
						newNode = MDDBaseOperators.AND.combine(manager, n1, n2);
						ExtendedSemaphore.OPERATION_SEMAPHORE.release();
						tPList.push(newNode);
						break;
					case "-":
						// NOT Operation
						assert (tPList.size() >= 1);
						n1 = tPList.pop();
						ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
						newNode = manager.not(n1);
						ExtendedSemaphore.OPERATION_SEMAPHORE.release();
						tPList.push(newNode);
						break;
					}
				} else {
					// Convert the value in a MDD and store it into a list
					int newNode = getTupleFromParameter(ce.value, m.bounds, m.nParams, manager);
					tPList.push(newNode);
				}
			}

			// At the end of the single constraint management, each constraint must
			// correspond to a single node
			if (tPList.size() != 1) {
				System.out.println(tPList.size() + " - ERROR IN CONSTRAINTS DEFINITION \n");
				return -1;
			}

			// Now the top of the stack must contain the complete constraint representation
			// and we can update the base node
			ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
			baseNode = MDDBaseOperators.AND.combine(manager, baseNode, tPList.pop());
			ExtendedSemaphore.OPERATION_SEMAPHORE.release();
		}
		return baseNode;
	}

	/**
	 * Updates the MDD by inserting all the constraints previously loaded in the
	 * test model
	 * 
	 * @param manager:  the MDD manager
	 * @param m:        the test model
	 * @param baseNode: the base MDD
	 * @return the identifier of the MDD
	 * @throws InterruptedException
	 */
	public static int updateMDDWithConstraints(MDDManager manager, CitModel m, int baseNode)
			throws InterruptedException, NotConvertableModel {
		// Translator
		ConstraintToMDD translator = new ConstraintToMDD(m, manager);
		// Fetch all the constraints
		for (ctwedge.ctWedge.Constraint c : m.getConstraints()) {
			translator.reset();
			translator.doSwitch(c);

			// Now the top of the stack must contain the complete constraint representation
			// and we can update the base node
			ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
			baseNode = MDDBaseOperators.AND.combine(manager, baseNode, translator.returnMdd());
			ExtendedSemaphore.OPERATION_SEMAPHORE.release();
		}
		return baseNode;
	}

	/**
	 * Returns the bounds vector from a model
	 * 
	 * @param m the cit model
	 * @return the bounds vector from a model
	 */
	public static int[] getBounds(CitModel m) {
		int[] res = new int[m.getParameters().size()];
		int i = 0;
		for (Parameter p : m.getParameters()) {
			res[i] = ParameterSize.eInstance.doSwitch(p);
			i++;
		}
		return res;
	}

	/**
	 * Returns the corresponding index of a value read from file
	 * 
	 * @param value:   the value read in constraints from file
	 * @param bounds:  the bounds of all the parameters
	 * @param nParams: the number of parameters
	 * @param manager: the MDD Manager
	 * @return the corresponding index of a value read from file
	 * @throws InterruptedException
	 */
	static int getTupleFromParameter(int value, int[] bounds, int nParams, MDDManager manager)
			throws InterruptedException {
		int index = 0;
		TupleConverter tc = new TupleConverter(manager);

		for (int i = 0; i < nParams; i++) {
			if (value < (index + bounds[i])) {
				Vector<Pair<Integer, Integer>> val = new Vector<Pair<Integer, Integer>>();
				val.add(new Pair<Integer, Integer>(i, value - index));
				return tc.getMDDFromTuple(val);
			}
			index += bounds[i];
		}

		return -1;
	}

	/**
	 * Returns the cardinality of a node
	 * 
	 * @param node:    the node identifier
	 * @param manager: the MDD Manager
	 * @return the cardinality of a node
	 */
	public static long getCardinality(int node, MDDManager manager) {
		PathSearcher searcher = new PathSearcher(manager, 1);
		searcher.setNode(node);
		return searcher.countPaths();
	}

	/**
	 * Returns the first available path in the MDD
	 * 
	 * @param node:    the starting node
	 * @param manager: the MDD Manager
	 * @param test:    the vector containing the test with the values set
	 * @return the first available path in the MDD
	 */
	public static int[] getPathInMDD(int node, MDDManager manager, int[] test) {
		PathSearcher searcher = new PathSearcher(manager, 1);
		searcher.setNode(node);
		searcher.countPaths();
		return searcher.getPath();
	}

	/**
	 * Returns the value corresponding to a node ID
	 * 
	 * @param manager:  the MDD Manager
	 * @param node:     the MDD node
	 * @param child:    the MDD node to be reached
	 * @param indexVar: the index of the variable
	 * @return the value corresponding to a node ID
	 */
	public static int getValueFromNode(MDDManager manager, int node, int child, int indexVar) {
		for (int i = 0; i < manager.getAllVariables()[indexVar].nbval; i++)
			if (manager.getChild(node, i) == child)
				return i;

		return -1;
	}

	/**
	 * Checks if a path starting from the node leads to the true leaf
	 * 
	 * @param node:    the node we want to check
	 * @param manager: the MDD manager
	 * @return true if the path starting from the node leads to the true leaf, false
	 *         otherwise
	 */
	public static boolean leadsToTrue(int node, MDDManager manager) {
		// The last element can be the one we are looking for, or the opposite
		if (manager.isleaf(node) && node == 1) {
			return true;
		} else if (manager.isleaf(node)) {
			return false;
		}

		// It is not the last element. Call it recursively
		int[] children = manager.getChildren(node);
		for (int child : children) {
			if (leadsToTrue(child, manager)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Translates the output from MEDICI format to a CSV one
	 * 
	 * @param testCases: the test cases list
	 * @param model:     the CIT Model
	 * @return a list of strings, one for each row (test) and header as first row
	 * @throws IOException
	 */
	public static List<String> translateOutput(ArrayList<String> testCases, CitModel model) throws IOException {
		List<String> csv_out = new ArrayList<>();
		// creating an array of integers with size equal to the number of the parameters
		// of CitModel
		// in each position we will have the size of the corresponding parameter
		int[] sizes = new int[model.getParameters().size()];
		int count = 0;
		String header = "";
		// First row -> parameter names
		for (Parameter param : model.getParameters()) {
			sizes[count] = ParameterSize.eInstance.doSwitch(param);
			header += param.getName() + ";";
			count++;
		}
		// remove
		header = header.substring(0, header.length() - 1);
		csv_out.add(header);

		// Other rows -> parameter values
		// 1) Questo crea un oggetto che permette di mappare i parametri del
		// modello a numeri interi
		ParameterValuesToInt valToInt = new ParameterValuesToInt(model);
		for (String s : testCases) {
			String row = "";
			String[] values = s.split(" ");
			for (int i = 0; i < values.length; i++) {
				int previousCount = 0;
				int val = 0;
				for (int j = 0; j < i; j++) {
					previousCount += sizes[j];
				}
				if (Integer.parseInt(values[i]) == -1) {
					val = randInt(previousCount, previousCount + sizes[i] - 1);
				} else {
					val = previousCount + Integer.parseInt(values[i]);
				}
				// 2) Questo permette, da un valore intero, di ottenere
				// il valore della stringa ad esso corrispondente e lo
				// aggiunge a csv_out
				row += valToInt.convertInt(val).getSecond() + ";";
			}
			row = row.substring(0, row.length() - 1);
			csv_out.add(row);
		}
		// remove duplicates if they are exactly the same
		csv_out = csv_out.stream().distinct().collect(Collectors.toList());
		return csv_out;
	}

	/**
	 * Translates the output from MEDICI format to a CSV one and returns it as a
	 * String
	 * 
	 * @param testCases: the test cases list
	 * @param model:     the CIT Model
	 * 
	 * @return a {@link String} containing the test suite in a CSV format
	 * @throws IOException
	 */
	public static String translateOutputToString(ArrayList<String> testCases, CitModel model) throws IOException {
		return String.join("\n", translateOutput(testCases, model));
	}

	/**
	 * Generates random integer values between a minimum and a maximum bound
	 * 
	 * @param min: the minimum bound
	 * @param max: the maximum bound
	 * @return an integer randomly generated between a minimum and a maximum bound
	 */
	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	/**
	 * Deletes duplicates from a test suite in CSV format
	 * 
	 * @param testSuite: the test suite in CSV format
	 * @return the new test suite in CSV format without duplicates
	 */
	public static String deleteDuplicates(String testSuite) {
		ArrayList<String> tests = new ArrayList<String>();
		tests.addAll(Arrays.asList(testSuite.split("\n")));

		ArrayList<String> reducedTests = new ArrayList<String>();

		for (String element : tests) {
			// If this element is not present in reducedTests we add it
			if (!reducedTests.contains(element)) {
				reducedTests.add(element);
			}
		}

		String reducedTestSuite = "";
		for (String element : reducedTests) {
			reducedTestSuite += element + "\n";
		}

		return reducedTestSuite;

	}

	/**
	 * Removes empty tests contexts, that do not cover any tuple
	 * 
	 * @param tcList the list of the test contexts
	 * @return the polished list of test contexts
	 */
	public static Vector<TestContext> removeEmpty(Vector<TestContext> tcList) {
		tcList.removeIf(x -> x.getNCovered() == 0);
		return tcList;
	}

	/**
	 * Removes useless tests contexts, that are implied by other test contexts
	 * 
	 * @param tcList   the list of the test contexts
	 * @param manager: the MDD manager
	 * @return the polished list of test contexts
	 */
	public static Vector<TestContext> removeImplied(Vector<TestContext> tcList, MDDManager manager) {
		tcList.removeIf(x -> isImpliedByTc(x, tcList, manager));
		return tcList;
	}

	/**
	 * Checks whether the test context x is implied by one of the context in the
	 * tcList
	 * 
	 * @param x:       the test context to be checked
	 * @param tcList:  the list of test contexts
	 * @param manager: the MDD manager
	 * @return true if at least one testcontext implying x is found
	 */
	private static boolean isImpliedByTc(TestContext x, Vector<TestContext> tcList, MDDManager manager) {
		int mddX = x.getMDD();
		for (TestContext tc : tcList) {
			int mddTC = tc.getMDD();
			int mddNotX = manager.mnot(mddX, 1);
			int mddTcAndNotX = MDDBaseOperators.AND.combine(manager, mddTC, mddNotX);
			int mddImplies = manager.mnot(mddTcAndNotX, 1);
			PathSearcher searcher = new PathSearcher(manager, 1);
			searcher.setNode(mddImplies);
			if (searcher.countPaths() == 0)
				return true;
		}

		return false;
	}

	/**
	 * Given a CITModel, it returns the ratio (i.e., the ratio between all tuples
	 * and those applicable)
	 * 
	 * @param model the CIT model
	 * @return the ratio
	 * @throws InterruptedException
	 */
	public static double getTupleValidityRatioFromModel(CitModel model) throws InterruptedException {
		ModelToMDDConverter mc = new ModelToMDDConverter(model);
		MDDManager manager = mc.getMDD();
		int baseMDD = mc.getStartingNode();
		int totTuple = 0;
		int compatiblePairs = 0;
		// Add to the baseNode the constraints
		baseMDD = Operations.updateMDDWithConstraints(manager, model, baseMDD);
		// All tuples
		Iterator<List<Pair<Integer, Integer>>> tg = TupleGenerator.getAllKWiseCombination(model, 2);
		TestContext tc = new TestContext(baseMDD, model.getParameters().size(), true, manager, model);
		while (tg.hasNext()) {
			Vector<Pair<Integer, Integer>> tuple = new Vector<Pair<Integer, Integer>>(tg.next());
			if (tc.verifyWithMDD(tuple))
				compatiblePairs++;
			totTuple++;
		}
		return (double) compatiblePairs / totTuple;
	}

	/**
	 * Given a CITModel, it returns the ratio (i.e., the ratio between initial
	 * cardinality and cardinality after having applied the constraints)
	 * 
	 * @param model the CIT model
	 * @return the ratio
	 * @throws InterruptedException
	 */
	public static double getTestValidityRatioFromModel(CitModel model)
			throws InterruptedException, NotConvertableModel {
		ModelToMDDConverter mc = new ModelToMDDConverter(model);
		MDDManager manager = mc.getMDD();
		int baseMDD = mc.getStartingNode();

		// Compute the initial cardinality
		PathSearcher searcher = new PathSearcher(manager, 1);
		double initialCardinality = 1;
		for (Parameter p : model.getParameters()) {
			initialCardinality *= ParameterSize.eInstance.doSwitch(p);
		}

		// Add to the baseNode the constraints
		baseMDD = Operations.updateMDDWithConstraints(manager, model, baseMDD);

		searcher = new PathSearcher(manager, 1);
		searcher.setNode(baseMDD);
		double finalCardinality = searcher.countPaths();

		return finalCardinality / initialCardinality;
	}

	/**
	 * Convert the parameters in a Cit Model in a MAP [Integer, List(Integer)]
	 * 
	 * @param m:     the combinatorial model
	 * @param order: the order for parameter consideration during tuple generation
	 * @return the elements in a MAP [Integer, List(Integer)]
	 */
	public static LinkedHashMap<Integer, List<Integer>> getElementsMap(CitModel m, Order order) {
		assert order != null;

		List<Parameter> parameters = new ArrayList<>();
		for (Parameter p : m.getParameters())
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

		LinkedHashMap<Integer, List<Integer>> map = new LinkedHashMap<>();
		
		for (int param = 0; param < parameters.size(); param++) {
			Vector<Integer> values = new Vector<>();
			for (int val = 0; val < ParameterSize.eInstance.doSwitch(parameters.get(param)); val++) {
				values.add(val);
			}
			map.put(m.getParameters().indexOf(parameters.get(param)), values);
		}
		
		return map;
	}
}