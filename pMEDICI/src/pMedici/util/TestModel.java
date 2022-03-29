package pMedici.util;

import java.util.ArrayList;

public class TestModel {
	
	/**
	 * The number of parameters
	 */
	int nParams;
	
	/**
	 * The bounds for each parameter
	 */
	int[] bounds;
	
	/**
	 * The strength
	 */
	int strength;
	
	/**
	 * Use the constraints
	 */
	boolean useConstraints;
	
	/**
	 * List of constraints
	 */
	ArrayList<Constraint> constraintList;
	
	/**
	 * Builds a new test model
	 * 
	 * @param nParams: the number of parameters
	 * @param bounds: the bounds for each parameter
	 * @param strength: the strength for the test
	 * @param useConstraints: use the constraints?
	 * @param constraintList: the list of constraints
	 */
	public TestModel(int nParams, int[] bounds, int strength, boolean useConstraints, ArrayList<Constraint> constraintList) {
		this.bounds = bounds;
		this.nParams = nParams;
		this.strength = strength;
		this.useConstraints = useConstraints;
		this.constraintList = constraintList;
	}

	/**
	 * Returns the number of parameters
	 * 
	 * @return the number of parameters
	 */
	public int getnParams() {
		return nParams;
	}

	/**
	 * Returns the bounds of the parameters
	 * 
	 * @return the bounds of the parameters
	 */
	public int[] getBounds() {
		return bounds;
	}
	
	/**
	 * Returns the strength
	 * 
	 * @return the strength
	 */
	public int getStrength() {
		return strength;
	}

	/**
	 * Returns whether to use the constraints
	 * 
	 * @return whether to use the constraints
	 */
	public boolean getUseConstraints() {
		return this.useConstraints;
	}

	
	/**
	 * Set the strength 
	 * 
	 * @param strength: the strength
	 */
	public void setStrength(int strength) {
		this.strength = strength;		
	}
	
}
