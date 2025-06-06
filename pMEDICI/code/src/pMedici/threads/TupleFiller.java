package pMedici.threads;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import pMedici.safeelements.SafeQueue;
import pMedici.util.Pair;

public class TupleFiller implements Runnable {

	/**
	 * The object generating the tuples
	 */
	Iterator<List<Pair<Integer, Integer>>> tg;
	
	/**
	 * The queue in which the tuples must be added
	 */
	SafeQueue queue;
	
	/**
	 * Creates a new thread that fills the queue with the tuples
	 * 
	 * @param tg: the tuple generator object
	 * @param queue: the destination queue
	 */
	public TupleFiller(Iterator<List<Pair<Integer, Integer>>> tg, SafeQueue queue) {
		this.tg = tg;
		this.queue = queue;
	}

	@Override
	public void run() {
		while(tg.hasNext()) {
			Vector<Pair<Integer, Integer>> tuple = new Vector<Pair<Integer, Integer>>(tg.next());
			
			// Assuming only one is inserting
			queue.insert(tuple);
		}
		
		queue.setFinishedInserting();		
	}
	
}