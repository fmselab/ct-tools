package kali.threads;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import ctwedge.util.Pair;
import kali.safeelements.SafeQueue;

public class TupleFiller implements Runnable {

	/**
	 * The object generating the tuples
	 */
	Iterator<List<Pair<String, Object>>> tg;
	
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
	public TupleFiller(Iterator<List<Pair<String, Object>>> tg, SafeQueue queue) {
		this.tg = tg;
		this.queue = queue;
	}

	@Override
	public void run() {
		while(tg.hasNext()) {
			Vector<Pair<String, Object>> tuple = new Vector<Pair<String, Object>>(tg.next());
			
			// Wait if the queue is full
			while(queue.full());
			
			// Assuming only one is inserting
			queue.insert(tuple);
		}
		
		queue.setFinishedInserting();		
	}
	
}
