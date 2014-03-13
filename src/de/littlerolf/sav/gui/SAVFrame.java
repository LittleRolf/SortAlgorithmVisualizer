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
import java.util.List;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.littlerolf.sav.data.BaseSorter;
import de.littlerolf.sav.loader.SorterLoader;
import de.littlerolf.sav.loader.SorterLoaderListener;
import de.littlerolf.sav.simulation.AlgorithmSimulator;
import de.littlerolf.sav.simulation.SimulationListener;
import de.littlerolf.sav.simulation.SimulationResult;

public class SAVFrame extends JFrame {
	/**
	 * 
	 */

	private static final long serialVersionUID = -3474914946760719462L;

	public static final int BENCHMARK_RUNS = 10;

	private List<BaseSorter> sorters = new ArrayList<BaseSorter>();

	private JComboBox sorterComboBox;
	private JLabel lblCurrentStep;
	private JButton btnNextStep;
	private JButton btnLastStep;
	private JRadioButton rdbtnRandom;
	private JRadioButton rdbtnSorted;
	private JRadioButton rdbtnReverse;
	private JSpinner spinnerArraySize;
	private JLabel lblArrayGre;

	private SAVHistoryComponent historyComponent;
	private int currentSpeed = 1500;
	private boolean isSimulationRunning = true;
	private SteppingThread currentSteppingThread;

	private List<JComponent> disableMe = new ArrayList<JComponent>();

	private SorterLoader sorterLoader;

	private String path;

	public SAVFrame() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("SortAlgorithmVisualizer");
		setSize(1022, 419);
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
		lblGeschwindigkeit.setBounds(263, 295, 188, 14);
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
		btnNextStep.setBounds(145, 345, 106, 29);
		getContentPane().add(btnNextStep);
		btnNextStep.setEnabled(false);

		btnLastStep = new JButton("Zurück");
		btnLastStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SAVFrame.this.doSimulationLastStep();
			}
		});
		btnLastStep.setBounds(10, 345, 123, 29);
		getContentPane().add(btnLastStep);
		btnLastStep.setEnabled(false);

		JLabel lblImplementation = new JLabel("Implementation:");
		lblImplementation.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblImplementation.setBounds(655, 295, 117, 14);
		getContentPane().add(lblImplementation);

		JLabel lblKontrolle = new JLabel("Kontrolle:");
		lblKontrolle.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblKontrolle.setBounds(10, 295, 89, 14);
		getContentPane().add(lblKontrolle);

		lblCurrentStep = new JLabel("Aktueller Schritt: 42/42");
		lblCurrentStep.setBounds(461, 295, 182, 14);
		getContentPane().add(lblCurrentStep);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 882, 272);
		getContentPane().add(scrollPane);

		historyComponent = new SAVHistoryComponent();
		scrollPane.setViewportView(historyComponent);

		JButton btnRefresh = new JButton("Neu laden");
		btnRefresh.setBounds(871, 351, 117, 28);
		getContentPane().add(btnRefresh);
		btnRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SAVFrame.this.reloadSorters(path);
			}
		});
		disableMe.add(btnRefresh);

		ButtonGroup arrayModeGroup = new ButtonGroup();

		rdbtnRandom = new JRadioButton("Zufällig");
		rdbtnRandom.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnRandom.setSelected(true);
		rdbtnRandom.setBounds(902, 34, 86, 23);
		getContentPane().add(rdbtnRandom);
		arrayModeGroup.add(rdbtnRandom);
		disableMe.add(rdbtnRandom);

		rdbtnSorted = new JRadioButton("Best Case");
		rdbtnSorted.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnSorted.setBounds(902, 60, 114, 23);
		getContentPane().add(rdbtnSorted);
		arrayModeGroup.add(rdbtnSorted);
		disableMe.add(rdbtnSorted);

		rdbtnReverse = new JRadioButton("Worst Case");
		rdbtnReverse.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbtnReverse.setBounds(902, 86, 114, 23);
		getContentPane().add(rdbtnReverse);
		arrayModeGroup.add(rdbtnReverse);
		disableMe.add(rdbtnReverse);

		JLabel lblArrayInhalt = new JLabel("Array Inhalt:");
		lblArrayInhalt.setHorizontalAlignment(SwingConstants.LEFT);
		lblArrayInhalt.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblArrayInhalt.setBounds(902, 11, 72, 14);
		getContentPane().add(lblArrayInhalt);

		spinnerArraySize = new JSpinner();
		spinnerArraySize.setModel(new SpinnerNumberModel(20, 5, 50, 1));
		spinnerArraySize.setBounds(902, 144, 114, 28);
		getContentPane().add(spinnerArraySize);
		lblCurrentStep.setText("");

		lblArrayGre = new JLabel("Array Größe:");
		lblArrayGre.setHorizontalAlignment(SwingConstants.LEFT);
		lblArrayGre.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblArrayGre.setBounds(902, 119, 86, 14);
		getContentPane().add(lblArrayGre);

		JButton btnOrdnerAuswhlen = new JButton("Ordner auswählen");
		btnOrdnerAuswhlen.setBounds(655, 351, 158, 28);
		getContentPane().add(btnOrdnerAuswhlen);
		btnOrdnerAuswhlen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SAVFrame.this.onSelectFolderPressed();
			}

		});

		for (JComponent c : disableMe)
			c.setEnabled(false);

	}

	private void onSelectFolderPressed() {
		JFileChooser folderChooser = new JFileChooser();
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnedValue = folderChooser.showOpenDialog(this);
		if (returnedValue == JFileChooser.APPROVE_OPTION) {
			path = folderChooser.getSelectedFile().getAbsolutePath();
			reloadSorters(path);
		} else {
			JOptionPane.showMessageDialog(null, "Sheesh.", "Fail.",
					JOptionPane.ERROR_MESSAGE);
		}
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
					public void onSimulationFinished(SimulationResult result) {
						SimulationResultFrame f = new SimulationResultFrame(
								result, SAVFrame.this);
						f.setVisible(true);
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
		int arraySize = (Integer) spinnerArraySize.getValue();

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

	private void reloadSorters(String path) {
		final ProgressFrame f = new ProgressFrame();

		this.sorterLoader = new SorterLoader(path, new SorterLoaderListener() {

			@Override
			public void onSorterLoadingStarted() {
				f.setVisible(true);
			}

			@Override
			public void onSorterLoaded(int progress, int max) {
				f.setMaximum(max);
				f.setValue(progress + 1);
			}

			@Override
			public void onSorterLoadingFinished() {
				f.setVisible(false);
				f.dispose();
				SAVFrame.this.sorterLoader.instanstiateAllClasses();
				SAVFrame.this.setSorters(SAVFrame.this.sorterLoader
						.getAllSorters());

				refreshUI();
				if (SAVFrame.this.getSorters().size() > 0)
					for (JComponent c : disableMe)
						c.setEnabled(true);
			}
		});
		this.sorterLoader.loadAllClasses();

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
		lblCurrentStep.setText("Aktueller Schritt: " + (progress + 1) + " / "
				+ max);
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
