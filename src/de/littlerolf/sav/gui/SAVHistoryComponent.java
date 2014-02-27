package de.littlerolf.sav.gui;

import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JComponent;

import de.littlerolf.sav.data.HistoryItem;

public class SAVHistoryComponent extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1099284147258735099L;

	private List<HistoryItem> historyItems = new ArrayList<HistoryItem>();

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public List<HistoryItem> getHistoryItems() {
		return historyItems;
	}

}
