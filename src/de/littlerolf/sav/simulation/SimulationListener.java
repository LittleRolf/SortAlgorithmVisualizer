package de.littlerolf.sav.simulation;

public interface SimulationListener {
	public void onSimulationStarted();

	public void onSimulationProgress(int progress, int max);

	public void onSimulationFinished(SimulationResult result);
}
