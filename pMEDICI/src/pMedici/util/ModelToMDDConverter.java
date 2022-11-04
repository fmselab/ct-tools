package pMedici.util;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDManagerFactory;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.MDDVariableFactory;

import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Parameter;
import ctwedge.generator.util.ParameterSize;
import pMedici.safeelements.ExtendedSemaphore;

public class ModelToMDDConverter {

	CitModel model;
	MDDManager manager;

	/**
	 * Builds a ModelToMDDConverter
	 * 
	 * @param model:  the model
	 */
	public ModelToMDDConverter(CitModel model) {
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
		for (Parameter p : model.getParameters())  {
			Integer nValues = ParameterSize.eInstance.doSwitch(p);
			// TODO: Fix me in a better way?
			// When only a value is available for the parameter, hack the MDD by faking a second one
			if (nValues == 1)
				nValues++;
			mvf.add(p.getName(), nValues.byteValue());
		}		
		
		// Returns the MDD manager with the needed variable factory and with 2 leaves (T -> 1, F -> 0)
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
		for (int i = model.getParameters().size() - 1; i>= 0; i--) {
			ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
			int size = ParameterSize.eInstance.doSwitch(model.getParameters().get(i));
			// When only a value is available for the parameter, hack the MDD by faking a second one
			if (size == 1)
				size++;
			newNode = vars[i].getNode(getChildrenList(size, newNode));
			ExtendedSemaphore.OPERATION_SEMAPHORE.release();
		}
		
		return newNode;
	}
	
	/**
	 * Returns the list of children for a given node
	 * 
	 * @param dim: the size of the variable
	 * @param nextNode: the next node 
	 * @return the list of children for a given node
	 */
	private int[] getChildrenList(int dim, int nextNode) {
		int[] childrenList = new int[dim];
		
		for (int i=0; i<dim; i++) {
			childrenList[i] = nextNode;
		}
		
		return childrenList;
	}

}
