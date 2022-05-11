package pMedici.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.PathSearcher;
import org.colomoto.mddlib.operators.MDDBaseOperators;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.generator.util.ParameterSize;
import ctwedge.util.ParameterValuesToInt;
import pMedici.safeelements.ExtendedSemaphore;

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
		int nParams = 0;
		int strength = 0;
		int[] bounds;
		int nConstraints = 0;
		String tempStr;
		ArrayList<Constraint> constraintList = new ArrayList<Constraint>();

		// Open the model file
		File modelFile = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(modelFile));

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
	 * test mdoel
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
	 * Returns the corresponding index of a value read from file
	 * 
	 * @param value:   the value read in constraints from file
	 * @param bounds:  the bounds of all the parameters
	 * @param nParams: the number of parameters
	 * @param manager: the MDD Manager
	 * @return the corresponding index of a value read from file
	 * @throws InterruptedException
	 */
	private static int getTupleFromParameter(int value, int[] bounds, int nParams, MDDManager manager)
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
		PathSearcher searcher = new PathSearcher(manager,1);
		searcher.setNode(node);
		searcher.countPaths();
		return searcher.getPath();
		
//		int totVars = manager.getAllVariables().length;
//		int[] res = new int[totVars];
//		
//		for (int i = 0; i < totVars; i++) {
//			assert (getCardinality(node, manager) > 0) : "Error on variable " + (i+1) + " of " + totVars;
//			int[] children = manager.getChildren(node);
//
//			if (children != null && children.length > 0) {
//				for (int child : children) {
//					if (leadsToTrue(child, manager)) {
//						res[i] = getValueFromNode(manager, node, child, i);
////						if (res[i] == -1 && test[i] != -1) {
////							res[i] = test[i];
////							node = manager.getChild(node, res[i]);
////						} else {
//							node = child;
////						}
//						break;
//					}
//				}
//			}
//		}
//
//		return res;
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
	 * @throws IOException
	 */
	public static void translateOutput(ArrayList<String> testCases, CitModel model) throws IOException {
		String csv_out = "";
		int[] sizes = new int[model.getParameters().size()];
		int count = 0;

		// First row -> parameter names
		for (Parameter param : model.getParameters()) {
			sizes[count] = ParameterSize.eInstance.doSwitch(param);
			csv_out += param.getName() + ";";
			count++;
		}

		csv_out = csv_out.substring(0, csv_out.length() - 1);
		csv_out += "\n";

		// Other rows -> parameter values
		ParameterValuesToInt valToInt = new ParameterValuesToInt(model);
		for (String s : testCases) {
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
				csv_out += valToInt.convertInt(val).getSecond() + ";";
			}
			csv_out = csv_out.substring(0, csv_out.length() - 1);
			csv_out += "\n";
		}

		// Print the results
		Arrays.stream(csv_out.split("\n")).distinct().forEach(x -> System.out.println(x));
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
}