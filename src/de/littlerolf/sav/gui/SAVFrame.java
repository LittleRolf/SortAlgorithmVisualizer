package de.littlerolf.sav.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import de.littlerolf.sav.data.BaseSorter;
import de.littlerolf.sav.loader.SorterLoader;

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

	private SAVHistoryComponent historyComponent;
	private int currentSpeed = 1500;
	private boolean isSimulationRunning = true;
	private SteppingThread currentSteppingThread;

	private List<JComponent> disableMe = new ArrayList<JComponent>();

	private SorterLoader sorterLoader;

	public SAVFrame(String path) {
		sorterLoader = new SorterLoader(path);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("SortAlgorithmVisualizer");
		setSize(1007, 400);
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
		btnSimulieren.setBounds(10, 332, 215, 29);
		getContentPane().add(btnSimulieren);
		disableMe.add(btnSimulieren);

		sorterComboBox = new JComboBox();
		sorterComboBox.setBounds(703, 333, 294, 28);
		getContentPane().add(sorterComboBox);
		disableMe.add(sorterComboBox);

		final JSlider slider = new JSlider();
		slider.setToolTipText("gemessen in Fischbr\u00F6tchen pro Sekunde");
		slider.setBounds(455, 311, 156, 50);
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
		lblGeschwindigkeit.setBounds(455, 295, 156, 14);
		getContentPane().add(lblGeschwindigkeit);

		final JCheckBox chkPause = new JCheckBox("Pause");
		chkPause.setBounds(623, 320, 90, 30);
		getContentPane().add(chkPause);
		chkPause.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				SAVFrame.this.isSimulationRunning = (e.getStateChange() == ItemEvent.DESELECTED);
			}
		});

		JLabel lblImplementation = new JLabel("Implementation:");
		lblImplementation.setBounds(703, 307, 117, 14);
		getContentPane().add(lblImplementation);

		JLabel lblKontrolle = new JLabel("Kontrolle:");
		lblKontrolle.setBounds(10, 307, 89, 14);
		getContentPane().add(lblKontrolle);

		JLabel lblSchritte = new JLabel("Schritte:");
		lblSchritte.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSchritte.setBounds(228, 327, 134, 14);
		getContentPane().add(lblSchritte);

		lblStepAmount = new JLabel("0");
		lblStepAmount.setBounds(374, 326, 69, 14);
		getContentPane().add(lblStepAmount);

		JLabel lblAktuellerSchritte = new JLabel("Aktueller Schritt:");
		lblAktuellerSchritte.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAktuellerSchritte.setBounds(228, 313, 134, 14);
		getContentPane().add(lblAktuellerSchritte);

		lblCurrentStep = new JLabel("0");
		lblCurrentStep.setBounds(374, 312, 69, 14);
		getContentPane().add(lblCurrentStep);

		JLabel lblStatistik = new JLabel("Statistik:");
		lblStatistik.setBounds(228, 295, 75, 14);
		getContentPane().add(lblStatistik);

		JLabel lblGeschwindigkeit_1 = new JLabel("Ø Geschwindigkeit:");
		lblGeschwindigkeit_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblGeschwindigkeit_1.setBounds(228, 342, 134, 14);
		getContentPane().add(lblGeschwindigkeit_1);

		lblSpeed = new JLabel("0s");
		lblSpeed.setBounds(374, 341, 69, 14);
		getContentPane().add(lblSpeed);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 987, 272);
		getContentPane().add(scrollPane);

		historyComponent = new SAVHistoryComponent();
		scrollPane.setViewportView(historyComponent);

		JButton btnRefresh = new JButton("Neu laden");
		btnRefresh.setBounds(830, 301, 167, 29);
		getContentPane().add(btnRefresh);
		btnRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SAVFrame.this.reloadSorters();
			}
		});
		disableMe.add(btnRefresh);

		reloadSorters();
	}

	private void onStartSimulationButtonPressed() {
		BaseSorter sorter = sorters.get(this.sorterComboBox.getSelectedIndex());
		sorter.getHistory().clear();

		int[] testingArray = generateTestingArray();

		int[] benchmarkResults = new int[BENCHMARK_RUNS];

		for (int i = 0; i < benchmarkResults.length; i++) {
			sorter.getHistory().clear();
			int[] testingArrayReal = testingArray.clone();
			// Wohoo, sorting!
			long start = System.nanoTime();
			sorter.sortArray(testingArrayReal);
			benchmarkResults[i] = (int) ((System.nanoTime() - start) / 1000);
		}

		double averageSpeed = 0;

		for (int i = 0; i < benchmarkResults.length; i++)
			averageSpeed += benchmarkResults[i];
		averageSpeed /= benchmarkResults.length;

		lblSpeed.setText(averageSpeed + "µs");

		historyComponent.getHistoryItems().clear();
		historyComponent.getHistoryItems().addAll(sorter.getHistory());
		historyComponent.reset();

		historyComponent.repaint();

		currentSteppingThread = new SteppingThread();
		currentSteppingThread.start();

		for (JComponent c : disableMe)
			c.setEnabled(false);
	}

	private int[] generateTestingArray() {
		Random r = new Random();

		int[] values = new int[r.nextInt(15) + 5];

		for (int i = 0; i < values.length; i++)
			values[i] = r.nextInt(SAVHistoryComponent.PLAYING_CARD_AMOUNT);

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
					SAVFrame.this.historyComponent.nextStep();
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

}
