package de.littlerolf.sav.simulation;

import java.io.Serializable;

import de.littlerolf.sav.data.BaseSorter;

public class SimulationResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int totalSteps;
	public int[] sortedArray;
	public double averageSpeed;
	public BaseSorter sorter;
}
