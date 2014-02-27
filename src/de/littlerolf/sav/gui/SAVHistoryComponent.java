package de.littlerolf.sav.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import de.littlerolf.sav.data.HistoryItem;

public class SAVHistoryComponent extends JComponent {

	private static final long serialVersionUID = 1099284147258735099L;

	private static final int PLAYING_CARD_AMOUNT = 52;

	private BufferedImage[] cardImages;
	{
		cardImages = new BufferedImage[PLAYING_CARD_AMOUNT];
		String[] names = new String[] { "2", "3", "4", "5", "6", "7", "8", "9",
				"10", "J", "Q", "K", "A" };
		String[] colors = new String[] { "Diamonds", "Hearts", "Spades",
				"Clubs" };
		int namesIndex = 0;
		int colorsIndex = 0;

		for (int i = 0; i < cardImages.length; i++) {
			String color = colors[colorsIndex];
			String name = names[namesIndex];
			String path = "playing_cards/" + color + "/" + name
					+ color.charAt(0) + ".png";
			try {
				cardImages[i] = ImageIO.read(getClass().getClassLoader()
						.getResource(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
			namesIndex++;
			if (namesIndex >= names.length) {
				namesIndex = 0;
				colorsIndex++;
			}
		}
	}

	private List<HistoryItem> historyItems = new ArrayList<HistoryItem>();

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public List<HistoryItem> getHistoryItems() {
		return historyItems;
	}

}
