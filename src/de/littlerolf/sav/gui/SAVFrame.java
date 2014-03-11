package de.littlerolf.sav.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.littlerolf.sav.data.BaseSorter;
import de.littlerolf.sav.loader.SorterLoader;
import de.littlerolf.sav.simulation.AlgorithmSimulator;
import de.littlerolf.sav.simulation.SimulationListener;

public class SAVFrame extends JFrame {
	/**
	 * 
	 */

	private static final long serialVersionUID = -3474914946760719462L;

	public static final int BENCHMARK_RUNS = 10;

	private List<BaseSorter> sorters = new ArrayList<BaseSorter>();

	private JComboBox sorterComboBox;
	private JLabel lblStepAmount;
	private JLabel lblCurrentStep;
	private JLabel lblSpeed;
	private JButton btnNextStep;
	private JButton btnLastStep;
	private JRadioButton rdbtnRandom;
	private JRadioButton rdbtnSorted;
	private JRadioButton rdbtnReverse;

	private SAVHistoryComponent historyComponent;
	private int currentSpeed = 1500;
	private int arraySize = 10;
	private boolean isSimulationRunning = true;
	private SteppingThread currentSteppingThread;

	private List<JComponent> disableMe = new ArrayList<JComponent>();

	private SorterLoader sorterLoader;

	public SAVFrame(String path) {
		sorterLoader = new SorterLoader(path);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("SortAlgorithmVisualizer");
		setSize(1007, 419);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height
				/ 2 - this.getSize().height / 2);
		getContentPane().setLayout(null);

		JButton btnSimulieren = new JButton("Simulieren");
		btnSimulieren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SAVFrame.this.onStartSimulationButtonPressed();
			}
		});
		btnSimulieren.setBounds(10, 312, 123, 28);
		getContentPane().add(btnSimulieren);
		disableMe.add(btnSimulieren);

		sorterComboBox = new JComboBox();
		sorterComboBox.setBounds(655, 320, 333, 28);
		getContentPane().add(sorterComboBox);
		disableMe.add(sorterComboBox);

		final JSlider slider = new JSlider();
		slider.setToolTipText("gemessen in Fischbr\u00F6tchen pro Sekunde");
		slider.setBounds(263, 311, 380, 63);
		getContentPane().add(slider);
		slider.setMinimum(1);
		slider.setMaximum(3000);
		slider.setValue(1500);
		slider.setMajorTickSpacing(1000);
		slider.setMinorTickSpacing(100);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				SAVFrame.this.currentSpeed = slider.getValue();
			}

		});

		JLabel lblGeschwindigkeit = new JLabel("Geschwindigkeit:");
		lblGeschwindigkeit.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblGeschwindigkeit.setBounds(263, 295, 430, 14);
		getContentPane().add(lblGeschwindigkeit);

		final JToggleButton chkPause = new JToggleButton("Pause");
		chkPause.setBounds(145, 312, 106, 28);
		getContentPane().add(chkPause);
		chkPause.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				SAVFrame.this.isSimulationRunning = (e.getStateChange() == ItemEvent.DESELECTED);
				SAVFrame.this.btnLastStep.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
				SAVFrame.this.btnNextStep.setEnabled((e.getStateChange() == ItemEvent.SELECTED));
			}
		});

		btnNextStep = new JButton("Vor");
		btnNextStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SAVFrame.this.doSimulationNextStep();
			}
		});
		btnNextStep.setBounds(145, 345, 106, 22);
		getContentPane().add(btnNextStep);
		btnNextStep.setEnabled(false);

		btnLastStep = new JButton("Zurück");
		btnLastStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SAVFrame.this.doSimulationLastStep();
			}
		});
		btnLastStep.setBounds(10, 345, 123, 22);
		getContentPane().add(btnLastStep);
		btnLastStep.setEnabled(false);

		JLabel lblImplementation = new JLabel("Implementation:");
		lblImplementation.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblImplementation.setBounds(650, 295, 117, 14);
		getContentPane().add(lblImplementation);

		JLabel lblKontrolle = new JLabel("Kontrolle:");
		lblKontrolle.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblKontrolle.setBounds(10, 295, 89, 14);
		getContentPane().add(lblKontrolle);

		JLabel lblSchritte = new JLabel("Schritte:");
		lblSchritte.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSchritte.setBounds(817, 63, 90, 14);
		getContentPane().add(lblSchritte);

		lblStepAmount = new JLabel("0");
		lblStepAmount.setBounds(919, 63, 69, 14);
		getContentPane().add(lblStepAmount);

		JLabel lblAktuellerSchritte = new JLabel("Aktueller Schritt:");
		lblAktuellerSchritte.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAktuellerSchritte.setBounds(779, 37, 128, 14);
		getContentPane().add(lblAktuellerSchritte);

		lblCurrentStep = new JLabel("0");
		lblCurrentStep.setBounds(919, 37, 69, 14);
		getContentPane().add(lblCurrentStep);

		JLabel lblStatistik = new JLabel("Statistik:");
		lblStatistik.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblStatistik.setHorizontalAlignment(SwingConstants.RIGHT);
		lblStatistik.setBounds(779, 11, 128, 14);
		getContentPane().add(lblStatistik);

		JLabel lblGeschwindigkeit_1 = new JLabel("Ø Geschwindigkeit:");
		lblGeschwindigkeit_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblGeschwindigkeit_1.setBounds(779, 89, 128, 14);
		getContentPane().add(lblGeschwindigkeit_1);

		lblSpeed = new JLabel("0s");
		lblSpeed.setBounds(919, 89, 69, 14);
		getContentPane().add(lblSpeed);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 757, 272);
		getContentPane().add(scrollPane);

		historyComponent = new SAVHistoryComponent();
		scrollPane.setViewportView(historyComponent);

		JButton btnRefresh = new JButton("Neu laden");
		btnRefresh.setBounds(655, 346, 117, 28);
		getContentPane().add(btnRefresh);
		btnRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SAVFrame.this.reloadSorters();
			}
		});
		disableMe.add(btnRefresh);

		ButtonGroup arrayModeGroup = new ButtonGroup();

		rdbtnRandom = new JRadioButton("Zufällig");
		rdbtnRandom.setSelected(true);
		rdbtnRandom.setBounds(817, 151, 117, 23);
		getContentPane().add(rdbtnRandom);
		arrayModeGroup.add(rdbtnRandom);
		disableMe.add(rdbtnRandom);

		rdbtnSorted = new JRadioButton("Best Case");
		rdbtnSorted.setBounds(817, 179, 117, 23);
		getContentPane().add(rdbtnSorted);
		arrayModeGroup.add(rdbtnSorted);
		disableMe.add(rdbtnSorted);

		rdbtnReverse = new JRadioButton("Worst Case");
		rdbtnReverse.setBounds(817, 207, 117, 23);
		getContentPane().add(rdbtnReverse);
		arrayModeGroup.add(rdbtnReverse);
		disableMe.add(rdbtnReverse);
		
		final JSlider arraySizeSlider = new JSlider();
		
		arraySizeSlider.setBounds(787, 235, 200, 63);
		getContentPane().add(arraySizeSlider);
		arraySizeSlider.setMinimum(5);
		arraySizeSlider.setMaximum(50);
		arraySizeSlider.setValue(10);
		arraySizeSlider.setMajorTickSpacing(10);
		arraySizeSlider.setMinorTickSpacing(1);
		arraySizeSlider.setPaintTicks(true);
		arraySizeSlider.setPaintLabels(true);
		arraySizeSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				SAVFrame.this.arraySize = arraySizeSlider.getValue();
			}

		});

		JLabel lblArrayInhalt = new JLabel("Array Inhalt:");
		lblArrayInhalt.setHorizontalAlignment(SwingConstants.LEFT);
		lblArrayInhalt.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblArrayInhalt.setBounds(827, 125, 86, 14);
		getContentPane().add(lblArrayInhalt);

		reloadSorters();
	}

	private void onStartSimulationButtonPressed() {
		final BaseSorter sorter = sorters.get(this.sorterComboBox
				.getSelectedIndex());
		sorter.getHistory().clear();
		final ProgressFrame pFrame = new ProgressFrame();

		AlgorithmSimulator sim = new AlgorithmSimulator(sorter, BENCHMARK_RUNS,
				generateTestingArray(), new SimulationListener() {

					@Override
					public void onSimulationStarted() {
						pFrame.setVisible(true);
					}

					@Override
					public void onSimulationProgress(int progress, int max) {
						pFrame.setValue(progress + 1);
						pFrame.setMaximum(max);
					}

					@Override
					public void onSimulationFinished(double averageSpeed,
							int[] sortedArray) {
						lblSpeed.setText(averageSpeed + "µs");
						historyComponent.getHistoryItems().clear();
						historyComponent.getHistoryItems().addAll(
								sorter.getHistory());
						historyComponent.reset();

						historyComponent.repaint();
						currentSteppingThread = new SteppingThread();
						currentSteppingThread.start();

						for (JComponent c : disableMe)
							c.setEnabled(false);

						pFrame.setVisible(false);
						pFrame.dispose();
					}
				});
		sim.kickOffSimulation();

	}

	private int[] generateTestingArray() {
		Random r = new Random();

		int[] values = new int[arraySize];

		for (int i = 0; i < values.length; i++)
			values[i] = r.nextInt(SAVHistoryComponent.PLAYING_CARD_AMOUNT);

		if (this.rdbtnSorted.isSelected()) {
			Arrays.sort(values);
		} else if (this.rdbtnReverse.isSelected()) {
			Arrays.sort(values);
			for (int i = 0; i < values.length / 2; i++) {
				int temp = values[i];
				values[i] = values[values.length - i - 1];
				values[values.length - i - 1] = temp;
			}
		}
		return values;
	}

	private void reloadSorters() {
		this.sorterLoader.loadAllClasses();
		this.sorterLoader.instanstiateAllClasses();
		this.setSorters(this.sorterLoader.getAllSorters());

		refreshUI();
	}

	private void refreshUI() {
		// Refresh ComboBox
		this.sorterComboBox.removeAllItems();

		for (BaseSorter sorter : sorters)
			this.sorterComboBox.addItem(sorter.getName());
	}

	public List<BaseSorter> getSorters() {
		return sorters;
	}

	public void setSorters(List<BaseSorter> sorters) {
		this.sorters = sorters;
	}

	private class SteppingThread extends Thread {
		@Override
		public void run() {
			SAVFrame.this.onSteppingProgress(
					SAVFrame.this.historyComponent.getCurrentStep(),
					SAVFrame.this.historyComponent.getHistoryItems().size());
			while (!SAVFrame.this.historyComponent.isSimulationEndReached()) {
				try {
					Thread.sleep(SAVFrame.this.currentSpeed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (SAVFrame.this.isSimulationRunning) {
					SAVFrame.this.historyComponent.doNextStep();
					SAVFrame.this.onSteppingProgress(
							SAVFrame.this.historyComponent.getCurrentStep(),
							SAVFrame.this.historyComponent.getHistoryItems()
									.size());
				}
			}
			SAVFrame.this.onSteppingFinished();
		}
	}

	private void onSteppingFinished() {
		currentSteppingThread = null;

		for (JComponent c : disableMe)
			c.setEnabled(true);

	}

	private void onSteppingProgress(int progress, int max) {
		lblStepAmount.setText(max + "");
		lblCurrentStep.setText(progress + 1 + "");
	}

	public void doSimulationNextStep() {
		if (!isSimulationRunning) {
			SAVFrame.this.historyComponent.doNextStep();
			SAVFrame.this.onSteppingProgress(
					SAVFrame.this.historyComponent.getCurrentStep(),
					SAVFrame.this.historyComponent.getHistoryItems().size());
		}
	}

	public void doSimulationLastStep() {
		if (!isSimulationRunning) {
			SAVFrame.this.historyComponent.doPreviousStep();
			SAVFrame.this.onSteppingProgress(
					SAVFrame.this.historyComponent.getCurrentStep(),
					SAVFrame.this.historyComponent.getHistoryItems().size());
		}
	}
}
