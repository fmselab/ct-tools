package mantra.pmedici.util;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDManagerFactory;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.MDDVariableFactory;

import mantra.safeelements.ExtendedSemaphore;

public class ModelToMDDConverter {

	TestModel model;
	MDDManager manager;

	/**
	 * Builds a ModelToMDDConverter
	 * 
	 * @param model: the model
	 */
	public ModelToMDDConverter(TestModel model) {
		this.model = model;
		this.manager = null;
	}

	/**
	 * Returns the MDD manager for the loaded combinatorial model
	 * 
	 * @return the MDD manager for the loaded combinatorial model
	 */
	public MDDManager getMDD() {
		MDDVariableFactory mvf = new MDDVariableFactory();

		// Creates all the variables
		for (int i = 0; i < model.getnParams(); i++) {
			mvf.add("var" + i, (byte) model.getBounds()[i]);
		}

		// Returns the MDD manager with the needed variable factory and with 2 leaves (T
		// -> 1, F -> 0)
		this.manager = MDDManagerFactory.getManager(mvf, 2);
		return manager;
	}

	/**
	 * Returns the starting node of the MDD
	 * 
	 * @return the starting node of the MDD
	 * @throws InterruptedException
	 */
	public int getStartingNode() throws InterruptedException {
		int newNode = 1;

		if (manager == null)
			throw new RuntimeException(
					"You need to create an MDDManager with getMDD() before obtaining the starting node");

		// Build the MDD structure
		MDDVariable[] vars = manager.getAllVariables();
		for (int i = model.getnParams() - 1; i >= 0; i--) {
			ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
			newNode = vars[i].getNode(getChildrenList(model.getBounds()[i], newNode));
			ExtendedSemaphore.OPERATION_SEMAPHORE.release();
		}

		return newNode;
	}

	/**
	 * Returns the list of children for a given node
	 * 
	 * @param dim:      the size of the variable
	 * @param elem:     the element to be set as accepted
	 * @param nextNode: the next node
	 * @return the list of children for a given node
	 */
	private int[] getChildrenList(int dim, int nextNode) {
		int[] childrenList = new int[dim];

		for (int i = 0; i < dim; i++) {
			childrenList[i] = nextNode;
		}

		return childrenList;
	}

}
