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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jsyntaxpane.DefaultSyntaxKit;
import de.littlerolf.sav.data.BaseSorter;
import javax.swing.SwingConstants;

public class SAVFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3474914946760719462L;

	public static final int BENCHMARK_RUNS = 10;

	private List<BaseSorter> sorters = new ArrayList<BaseSorter>();

	private JComboBox<String> sorterComboBox;
	private JLabel lblStepAmount;
	private JLabel lblCurrentStep;
	private JLabel lblSpeed;

	private SAVHistoryComponent historyComponent;
	private int currentSpeed = 1000;
	private SteppingThread currentSteppingThread;

	private List<JComponent> disableMe = new ArrayList<JComponent>();

	public SAVFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("SortAlgorithmVisualizer");
		setSize(906, 400);
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

		sorterComboBox = new JComboBox<String>();
		sorterComboBox.setBounds(596, 333, 294, 28);
		getContentPane().add(sorterComboBox);
		disableMe.add(sorterComboBox);

		JSlider slider = new JSlider();
		slider.setToolTipText("gemessen in Fischbr\u00F6tchen pro Sekunde");
		slider.setBounds(430, 332, 156, 23);
		getContentPane().add(slider);

		JLabel lblGeschwindigkeit = new JLabel("Geschwindigkeit:");
		lblGeschwindigkeit.setBounds(430, 307, 156, 14);
		getContentPane().add(lblGeschwindigkeit);

		JLabel lblImplementation = new JLabel("Implementation:");
		lblImplementation.setBounds(596, 307, 117, 14);
		getContentPane().add(lblImplementation);

		JLabel lblKontrolle = new JLabel("Kontrolle:");
		lblKontrolle
				.setToolTipText("oder auch \"Cockpit\"... h\u00F6h\u00F6, \"Cock\"...");
		lblKontrolle.setBounds(10, 307, 89, 14);
		getContentPane().add(lblKontrolle);

		JLabel lblSchritte = new JLabel("Schritte:");
		lblSchritte.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSchritte.setBounds(228, 327, 134, 14);
		getContentPane().add(lblSchritte);

		lblStepAmount = new JLabel("0");
		lblStepAmount.setBounds(374, 326, 46, 14);
		getContentPane().add(lblStepAmount);

		JLabel lblAktuellerSchritte = new JLabel("Aktueller Schritt:");
		lblAktuellerSchritte.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAktuellerSchritte.setBounds(228, 313, 134, 14);
		getContentPane().add(lblAktuellerSchritte);

		lblCurrentStep = new JLabel("0");
		lblCurrentStep.setBounds(374, 312, 46, 14);
		getContentPane().add(lblCurrentStep);

		JLabel lblStatistik = new JLabel("Statistik:");
		lblStatistik.setBounds(228, 295, 75, 14);
		getContentPane().add(lblStatistik);

		JLabel lblGeschwindigkeit_1 = new JLabel("Ø Geschwindigkeit:");
		lblGeschwindigkeit_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblGeschwindigkeit_1.setBounds(228, 342, 134, 14);
		getContentPane().add(lblGeschwindigkeit_1);

		lblSpeed = new JLabel("0s");
		lblSpeed.setBounds(374, 341, 46, 14);
		getContentPane().add(lblSpeed);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 880, 272);
		getContentPane().add(scrollPane);

		historyComponent = new SAVHistoryComponent();
		scrollPane.setViewportView(historyComponent);

		JButton btnNew = new JButton("Neu...");
		btnNew.setBounds(710, 301, 75, 29);
		getContentPane().add(btnNew);
		disableMe.add(btnNew);

		JButton btnEdit = new JButton("Bearbeiten...");
		btnEdit.setBounds(787, 301, 103, 29);
		getContentPane().add(btnEdit);
		disableMe.add(btnEdit);

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

		lblSpeed.setText(averageSpeed + "�s");

		historyComponent.getHistoryItems().clear();
		historyComponent.getHistoryItems().addAll(sorter.getHistory());
		historyComponent.reset();
		System.out.println(sorter.getHistory().size());

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
		this.getSorters().clear();
		// Temporary test obviously:
		this.getSorters().add(new BaseSorter() {

			@Override
			public int[] sortArray(int[] values) {
				qSort(values, 0, values.length - 1);

				return values;
			}

			public void qSort(int x[], int links, int rechts) {
				this.saveHistory(x);
				if (links < rechts) {
					int i = partition(x, links, rechts);
					qSort(x, links, i - 1);
					qSort(x, i + 1, rechts);
				}
			}

			public int partition(int x[], int links, int rechts) {
				int pivot, i, j, help;
				pivot = x[rechts];
				i = links;
				j = rechts - 1;
				while (i <= j) {
					if (x[i] > pivot) {
						// tausche x[i] und x[j]
						help = x[i];
						x[i] = x[j];
						x[j] = help;
						j--;
					} else
						i++;
				}
				// tausche x[i] und x[rechts]
				help = x[i];
				x[i] = x[rechts];
				x[rechts] = help;

				return i;
			}

			@Override
			public String getName() {
				return "Crazy Ulf";
			}

		});
		// TODO: implement sorter loading (needs
		// SorterLoaderManagerDeviceAbstractFlugzeugManager first)

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

				SAVFrame.this.historyComponent.nextStep();
				SAVFrame.this
						.onSteppingProgress(SAVFrame.this.historyComponent
								.getCurrentStep(),
								SAVFrame.this.historyComponent
										.getHistoryItems().size());
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

	public static void main(String[] args) {
		DefaultSyntaxKit.initKit();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		new SAVFrame().setVisible(true);
		new CodeEditorFrame().setVisible(true);
	}
}
