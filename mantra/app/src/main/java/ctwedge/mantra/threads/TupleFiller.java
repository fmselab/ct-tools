package ctwedge.mantra.threads;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import ctwedge.mantra.util.Pair;
import ctwedge.mantra.safeelements.SafeQueue;

public class TupleFiller<S, K> implements Runnable {

	/**
	 * The object generating the tuples
	 */
	Iterator<List<Pair<S, K>>> tg;

	/**
	 * The queue in which the tuples must be added
	 */
	SafeQueue<S, K> queue;

	/**
	 * Creates a new thread that fills the queue with the tuples
	 * 
	 * @param tg:    the tuple generator object
	 * @param queue: the destination queue
	 */
	public TupleFiller(Iterator<List<Pair<S, K>>> tg, SafeQueue<S, K> queue) {
		this.tg = tg;
		this.queue = queue;
	}

	@Override
	public void run() {
		while (tg.hasNext()) {
			Vector<Pair<S, K>> tuple = new Vector<Pair<S, K>>(tg.next());

			// Wait if the queue is full
			while (queue.full())
				;

			// Assuming only one is inserting
			queue.insert(tuple);
		}

		queue.setFinishedInserting();
	}

}
