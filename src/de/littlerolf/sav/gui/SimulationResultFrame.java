package de.littlerolf.sav.gui;

import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.littlerolf.sav.simulation.SimulationResult;

public class SimulationResultFrame extends JDialog {
	public SimulationResultFrame(SimulationResult result, JFrame parent) {
		super(parent);

		setResizable(false);
		setTitle("Simulationsergebnis");
		getContentPane().setLayout(null);
		setSize(415, 164);
		setLocationRelativeTo(parent);

		JLabel lblAlgorithmus = new JLabel("Algorithmus:");
		lblAlgorithmus.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAlgorithmus.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAlgorithmus.setBounds(10, 11, 223, 14);
		getContentPane().add(lblAlgorithmus);

		JLabel lblInputarrayGre = new JLabel("Input-Array Größe:");
		lblInputarrayGre.setHorizontalAlignment(SwingConstants.RIGHT);
		lblInputarrayGre.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblInputarrayGre.setBounds(10, 35, 223, 14);
		getContentPane().add(lblInputarrayGre);

		JLabel lblDurchschnittlicheGeschwindigkeit = new JLabel(
				"Durchschnittliche Geschwindigkeit:");
		lblDurchschnittlicheGeschwindigkeit
				.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDurchschnittlicheGeschwindigkeit.setFont(new Font("Tahoma",
				Font.BOLD, 11));
		lblDurchschnittlicheGeschwindigkeit.setBounds(10, 60, 223, 14);
		getContentPane().add(lblDurchschnittlicheGeschwindigkeit);

		JLabel lblAnzahlSchritte = new JLabel("Anzahl Schritte:");
		lblAnzahlSchritte.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAnzahlSchritte.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAnzahlSchritte.setBounds(10, 85, 223, 14);
		getContentPane().add(lblAnzahlSchritte);

		JLabel lblAlgorithmName = new JLabel(result.sorter.getName());
		lblAlgorithmName.setBounds(243, 11, 145, 14);
		getContentPane().add(lblAlgorithmName);

		JLabel lblInputSize = new JLabel(result.sortedArray.length + "");
		lblInputSize.setBounds(243, 35, 145, 14);
		getContentPane().add(lblInputSize);

		JLabel lblAvrgSpeed = new JLabel(result.averageSpeed + "µs");
		lblAvrgSpeed.setBounds(243, 60, 145, 14);
		getContentPane().add(lblAvrgSpeed);

		JLabel lblSteps = new JLabel(result.totalSteps + "");
		lblSteps.setBounds(243, 85, 145, 14);
		getContentPane().add(lblSteps);

		JLabel lblArraySortiert = new JLabel("Array sortiert:");
		lblArraySortiert.setHorizontalAlignment(SwingConstants.RIGHT);
		lblArraySortiert.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblArraySortiert.setBounds(10, 110, 223, 14);
		getContentPane().add(lblArraySortiert);

		JLabel lblSorted = new JLabel(isSorted(result.sortedArray) ? "Yo"
				: "Nö");
		lblSorted.setBounds(243, 110, 145, 14);
		getContentPane().add(lblSorted);
	}

	private boolean isSorted(int[] array) {
		boolean sorted = true;
		for (int i = 1; i < array.length; i++) {
			if (array[i - 1] > array[i]) {
				sorted = false;
				break;
			}
		}
		return sorted;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
