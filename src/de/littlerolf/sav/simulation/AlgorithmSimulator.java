package de.littlerolf.sav.simulation;

import de.littlerolf.sav.data.BaseSorter;

public class AlgorithmSimulator implements Runnable {

	private SimulationListener listener;
	private BaseSorter sorter;
	private int simulations;
	private int[] inputArray;

	public AlgorithmSimulator(BaseSorter sorter, int simulations,
			int[] inputArray, SimulationListener listener) {
		this.listener = listener;
		this.sorter = sorter;
		this.simulations = simulations;
		this.inputArray = inputArray;
	}

	public void kickOffSimulation() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		int[] benchmarkResults = new int[simulations];
		int[] sortedArray = null;

		listener.onSimulationStarted();

		try {
			for (int i = 0; i < benchmarkResults.length; i++) {
				sorter.getHistory().clear();
				sortedArray = inputArray.clone();

				// Wohoo, sorting!
				long start = System.nanoTime();
				int[] result = sorter.sortArray(sortedArray);
				benchmarkResults[i] = (int) ((System.nanoTime() - start) / 1000);
				sorter.saveHistory(result);
				listener.onSimulationProgress(i, simulations);
			}
		} catch (Exception e) {
			e.printStackTrace();
			listener.onSimulationFailed();
		}

		double averageSpeed = 0;

		for (int i = 0; i < benchmarkResults.length; i++)
			averageSpeed += benchmarkResults[i];
		averageSpeed /= benchmarkResults.length;

		SimulationResult result = new SimulationResult();
		result.averageSpeed = averageSpeed;
		result.sortedArray = sortedArray.clone();
		result.sorter = sorter;
		result.totalSteps = sorter.getHistory().size();

		listener.onSimulationFinished(result);
	}

}
