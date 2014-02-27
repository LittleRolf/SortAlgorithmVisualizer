package de.littlerolf.sav.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import de.littlerolf.sav.data.HistoryItem;

public class SAVHistoryComponent extends JComponent {

	private static final long serialVersionUID = 1099284147258735099L;

	public static final int PLAYING_CARD_AMOUNT = 52;

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
	private int currentIndex = 0;

	public SAVHistoryComponent() {
		HistoryItem i = new HistoryItem();
		i.values = new int[] { 4, 10, 5, 20, 50, 10, 3, 48 };
		getHistoryItems().add(i);
	}

	public void nextStep() {
		currentIndex++;
		if (currentIndex >= historyItems.size())
			currentIndex--;
		repaint();
	}

	public void reset() {
		currentIndex = 0;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		HistoryItem currentItem = getHistoryItems().get(currentIndex);
		if (currentItem == null)
			return;

		int valueAmount = currentItem.values.length;
		int width = getWidth();
		int height = getHeight();
		int i = 0;
		int diff = width / 2 / valueAmount;
		for (int value : currentItem.values) {
			if (value > cardImages.length || value < 0)
				continue;

			BufferedImage cardImage = cardImages[value];
			g.drawImage(cardImage,
					(width / 4) + (i * diff) - cardImage.getWidth() / 8, height
							/ 2 - cardImage.getHeight() / 2 / 2,
					cardImage.getWidth() / 2, cardImage.getHeight() / 2, null);
			i++;
		}
	}

	public List<HistoryItem> getHistoryItems() {
		return historyItems;
	}

}
