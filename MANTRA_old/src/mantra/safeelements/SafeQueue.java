package mantra.safeelements;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import mantra.util.Pair;


public class SafeQueue<S, K> {
	
	ConcurrentLinkedQueue<Vector<Pair<S, K>>> tupleList;
	Boolean finished_inserting;
	int nTuples;
	
	/**
	 * The maximum size of the queue
	 */
	public static int QUEUE_SIZE = 40;
	
	/**
	 * Creates the SafeQuene in which the producer can add new tuples and the consumer can extract one
	 */
	public SafeQueue() {
		finished_inserting = false;
		tupleList = new ConcurrentLinkedQueue<Vector<Pair<S, K>>>();
		nTuples = 0;
	}
		
	/**
	 * Insert a new tuple in the queue
	 * 
	 * @param tuple: the tuple to be added in the queue
	 */
	public void insert(Vector<Pair<S, K>> tuple) {
		tupleList.add(tuple);
		nTuples++;
	}
	
	/**
	 * Reinsert a tuple in the queue
	 * 
	 * @param tuple: the tuple to be added in the queue
	 */
	public void reinsert(Vector<Pair<S, K>> tuple) {
		tupleList.add(tuple);
	}

	/**
	 * Get the new element if available, otherwise returns null
	 * 
	 * @return the new tuple if available, otherwise returns null
	 */
	public Vector<Pair<S, K>> get() {
		return tupleList.poll();
	}

	/**
	 * Checks whether the tuple is full
	 * 
	 * @return true if the tuple is full, false otherwise
	 */
	public boolean full() {
		return tupleList.size() >= QUEUE_SIZE;
	}

	/**
	 * Checks whether the tuple has finished to be used
	 * 
	 * @return true if the tuple has finished to be used, false otherwise
	 */
	public boolean finished() {
		return finished_inserting && tupleList.size() == 0;
	}

	/**
	 * Set finished inserting
	 */
	public void setFinishedInserting() {
		finished_inserting = true;
	}
	
	/**
	 * Get the size of the queue
	 * 
	 * @return the size of the queue
	 */
	public int getSize() {
		return tupleList.size();
	}
	
	/**
	 * Get the total number of generated tuples
	 * 
	 * @return the total number of generated tuples
	 */
	public int getNTuples() {
		return nTuples;
	}
}
