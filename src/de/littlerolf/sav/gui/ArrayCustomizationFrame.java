package de.littlerolf.sav.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ArrayCustomizationFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5822914402811670487L;
	private JPanel contentPane;
	private SAVFrame parent;

	/**
	 * Create the frame.
	 */
	public ArrayCustomizationFrame(SAVFrame frame) {
		this.parent = frame;

		setTitle("Array Anpassung");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 210, 130);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JCheckBox chckbxArrayUmdrehen = new JCheckBox("Array umdrehen");
		chckbxArrayUmdrehen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayCustomizationFrame.this.parent.arrayOptions.put(
						SAVFrame.ArrayOption.REVERSE_ARRAY, ((JCheckBox) e.getSource()).isSelected());
			}
		});
		chckbxArrayUmdrehen.setBounds(8, 35, 190, 23);
		chckbxArrayUmdrehen
		.setSelected(ArrayCustomizationFrame.this.parent.arrayOptions
				.get(SAVFrame.ArrayOption.REVERSE_ARRAY));
		contentPane.add(chckbxArrayUmdrehen);

		JCheckBox chckbxArrayVorsortiert = new JCheckBox("Array vorsortiert");
		chckbxArrayVorsortiert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ArrayCustomizationFrame.this.parent.arrayOptions.put(
						SAVFrame.ArrayOption.PRESORT_ARRAY, ((JCheckBox) arg0.getSource()).isSelected());
			}
		});
		chckbxArrayVorsortiert.setBounds(8, 8, 190, 23);
		chckbxArrayVorsortiert
				.setSelected(ArrayCustomizationFrame.this.parent.arrayOptions
						.get(SAVFrame.ArrayOption.PRESORT_ARRAY));
		contentPane.add(chckbxArrayVorsortiert);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ArrayCustomizationFrame.this.setVisible(false);
				ArrayCustomizationFrame.this.dispose();
			}
		});
		btnOk.setBounds(8, 66, 190, 25);
		contentPane.add(btnOk);
	}

}
