package pMedici.util;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;

import pMedici.safeelements.ExtendedSemaphore;

/**
 * Utility class for converting a tuple in the corresponding MDD
 */
public class TupleConverter {
	
	MDDManager manager;

	public TupleConverter(MDDManager manager) {
		this.manager = manager;
	}	
	
	/**
	 * Converts a tuple in the corresponding MDD 
	 * 
	 * @param tuple: the tuple to be converted in an MDD
	 * @return the Integer representing the starting node of the MDD equivalent to the tuple
	 * @throws InterruptedException 
	 */
	public int getMDDFromTuple(Vector<Pair<Integer, Integer>> tuple) throws InterruptedException {
		MDDVariable[] vars = manager.getAllVariables();
		int newMDD = 1;
		
		// Order the tuple elements in reverse order
		Collections.sort(tuple, new TupleComparator());
		
		// Fetch all the elements in the tuple
		for (Pair<Integer, Integer> p : tuple) {
			ExtendedSemaphore.OPERATION_SEMAPHORE.acquire();
			newMDD = vars[p.getFirst()].getNode(getChildrenList(vars[p.getFirst()].nbval, p.getSecond(), newMDD));
			ExtendedSemaphore.OPERATION_SEMAPHORE.release();
		}
		
		return newMDD;
	}	
	
	/**
	 * Returns the list of children for a given node
	 * 
	 * @param dim: the size of the variable
	 * @param elem: the element to be set as accepted
	 * @param nextNode: the next node 
	 * @return the list of children for a given node
	 */
	private int[] getChildrenList(int dim, int elem, int nextNode) {
		int[] childrenList = new int[dim];
		
		for (int i=0; i<dim; i++) {
			if (i == elem) {
				childrenList[i] = nextNode;
			} else {
				childrenList[i] = 0;
			}
		}
		
		return childrenList;
	}
	
}


/**
 * Comparator class for sorting the elements in the tuple
 */
class TupleComparator implements Comparator<Pair<Integer, Integer>> {
	@Override
	public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
		return -(o1.getFirst().compareTo(o2.getFirst()));
	}
}
