package de.littlerolf.sav.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SAVFrame extends JFrame {
	public SAVFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("SortAlgorithmVisualizer");
		setSize(800, 400);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height
				/ 2 - this.getSize().height / 2);
		getContentPane().setLayout(null);

		JButton btnSimulieren = new JButton("Simulieren");
		btnSimulieren.setBounds(10, 332, 89, 23);
		getContentPane().add(btnSimulieren);

		JButton btnAbspielen = new JButton("Abspielen");
		btnAbspielen.setBounds(109, 332, 89, 23);
		getContentPane().add(btnAbspielen);

		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(596, 333, 178, 20);
		getContentPane().add(comboBox);

		JSlider slider = new JSlider();
		slider.setBounds(430, 332, 156, 23);
		getContentPane().add(slider);

		JLabel lblGeschwindigkeit = new JLabel("Geschwindigkeit:");
		lblGeschwindigkeit.setBounds(430, 294, 89, 14);
		getContentPane().add(lblGeschwindigkeit);

		JLabel lblImplementation = new JLabel("Implementation:");
		lblImplementation.setBounds(596, 294, 89, 14);
		getContentPane().add(lblImplementation);

		JLabel lblKontrolle = new JLabel("Kontrolle:");
		lblKontrolle.setBounds(10, 294, 89, 14);
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
		scrollPane.setBounds(10, 11, 774, 272);
		getContentPane().add(scrollPane);

		SAVHistoryComponent historyComponent = new SAVHistoryComponent();
		scrollPane.setViewportView(historyComponent);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		new SAVFrame().setVisible(true);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3474914946760719462L;
}
