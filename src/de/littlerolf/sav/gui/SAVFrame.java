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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jsyntaxpane.DefaultSyntaxKit;
import de.littlerolf.sav.data.BaseSorter;

public class SAVFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3474914946760719462L;

	private List<BaseSorter> sorters = new ArrayList<BaseSorter>();
	private JComboBox<String> sorterComboBox;
	private SAVHistoryComponent historyComponent;
	private int currentSpeed = 1000;

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
		btnSimulieren.setBounds(10, 332, 260, 23);
		getContentPane().add(btnSimulieren);

		sorterComboBox = new JComboBox<String>();
		sorterComboBox.setBounds(596, 333, 294, 20);
		getContentPane().add(sorterComboBox);

		JSlider slider = new JSlider();
		slider.setToolTipText("gemessen in Fischbr\u00F6tchen pro Sekunde");
		slider.setBounds(430, 332, 156, 23);
		getContentPane().add(slider);

		JLabel lblGeschwindigkeit = new JLabel("Geschwindigkeit:");
		lblGeschwindigkeit.setBounds(430, 307, 89, 14);
		getContentPane().add(lblGeschwindigkeit);

		JLabel lblImplementation = new JLabel("Implementation:");
		lblImplementation.setBounds(596, 307, 89, 14);
		getContentPane().add(lblImplementation);

		JLabel lblKontrolle = new JLabel("Kontrolle:");
		lblKontrolle
				.setToolTipText("oder auch \"Cockpit\"... h\u00F6h\u00F6, \"Cock\"...");
		lblKontrolle.setBounds(10, 307, 89, 14);
		getContentPane().add(lblKontrolle);

		JLabel lblSchritte = new JLabel("Schritte:");
		lblSchritte.setBounds(280, 326, 61, 14);
		getContentPane().add(lblSchritte);

		JLabel lblStepAmount = new JLabel("0");
		lblStepAmount.setBounds(374, 326, 46, 14);
		getContentPane().add(lblStepAmount);

		JLabel lblAktuellerSchritte = new JLabel("Aktueller Schritt:");
		lblAktuellerSchritte.setBounds(280, 312, 89, 14);
		getContentPane().add(lblAktuellerSchritte);

		JLabel lblCurrentStep = new JLabel("0");
		lblCurrentStep.setBounds(374, 312, 46, 14);
		getContentPane().add(lblCurrentStep);

		JLabel lblStatistik = new JLabel("Statistik:");
		lblStatistik.setBounds(280, 294, 61, 14);
		getContentPane().add(lblStatistik);

		JLabel lblGeschwindigkeit_1 = new JLabel("Geschwindigkeit:");
		lblGeschwindigkeit_1.setBounds(280, 341, 89, 14);
		getContentPane().add(lblGeschwindigkeit_1);

		JLabel lblSpeed = new JLabel("42ms");
		lblSpeed.setBounds(374, 341, 46, 14);
		getContentPane().add(lblSpeed);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 880, 272);
		getContentPane().add(scrollPane);

		historyComponent = new SAVHistoryComponent();
		scrollPane.setViewportView(historyComponent);

		JButton btnNew = new JButton("Neu...");
		btnNew.setBounds(710, 303, 75, 23);
		getContentPane().add(btnNew);

		JButton btnEdit = new JButton("Bearbeiten...");
		btnEdit.setBounds(787, 303, 103, 23);
		getContentPane().add(btnEdit);

		reloadSorters();
	}

	private void onStartSimulationButtonPressed() {
		BaseSorter sorter = sorters.get(this.sorterComboBox.getSelectedIndex());

		int[] testingArray = generateTestingArray();
		sorter.sortArray(testingArray);

		historyComponent.getHistoryItems().clear();
		historyComponent.getHistoryItems().addAll(sorter.getHistory());
		System.out.println(sorter.getHistory().size());

		historyComponent.repaint();

		new SteppingThread().start();
	}

	private int[] generateTestingArray() {
		Random r = new Random();

		int[] values = new int[r.nextInt(20)];

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
			while (!SAVFrame.this.historyComponent.isSimulationEndReached()) {
				try {
					Thread.sleep(SAVFrame.this.currentSpeed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				SAVFrame.this.historyComponent.nextStep();
			}
		}
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
