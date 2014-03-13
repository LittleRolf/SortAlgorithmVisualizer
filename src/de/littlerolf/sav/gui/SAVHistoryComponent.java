package de.littlerolf.sav.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import de.littlerolf.sav.data.HistoryItem;
import de.littlerolf.sav.simulation.ArrayDiff;

public class SAVHistoryComponent extends JComponent {

	private static final long serialVersionUID = 1099284147258735099L;

	public static final int PLAYING_CARD_AMOUNT = 52;

	private BufferedImage[] cardImages;
	private BufferedImage awesomeImage;
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

		try {
			awesomeImage = ImageIO.read(getClass().getClassLoader()
					.getResource("doge.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<HistoryItem> historyItems = new ArrayList<HistoryItem>();
	private int currentIndex = 0;

	public SAVHistoryComponent() {
	}

	public void doNextStep() {
		currentIndex++;
		if (currentIndex >= historyItems.size())
			currentIndex--;

		repaint();
	}

	public void doPreviousStep() {
		currentIndex--;
		if (currentIndex < 0)
			currentIndex++;

		repaint();
	}

	public boolean isSimulationEndReached() {
		return currentIndex + 1 >= historyItems.size();
	}

	public int getCurrentStep() {
		return currentIndex;
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

		if (getHistoryItems().isEmpty())
			return;

		HistoryItem currentItem = getHistoryItems().get(currentIndex);

		int valueAmount = currentItem.values.length;
		int width = getWidth();
		int height = getHeight();
		int i = 0;
		int diff = width / 2 / valueAmount;
		for (int value : currentItem.values) {
			BufferedImage cardImage;
			if (value < 0 || value > cardImages.length) {
				cardImage = this.awesomeImage;
			} else {
				cardImage = cardImages[value];
			}

			int x = (width / 4) + (i * diff) - cardImage.getWidth() / 8;
			int y = height / 2 - cardImage.getHeight() / 2 / 2;
			int imgWidth = cardImage.getWidth() / 2;
			int imgHeight = cardImage.getHeight() / 2;

			g.setStroke(new BasicStroke(3));

			if (i == currentItem.index)
				g.drawRect(x, y, imgWidth, imgHeight);

			g.drawImage(cardImage, x, y, imgWidth, imgHeight, null);
			i++;
		}
		if (currentIndex > 0) {
			int[] arrayDiff = ArrayDiff.getArrayDiff(
					getHistoryItems().get(currentIndex - 1).values,
					currentItem.values);
			if (arrayDiff == null)
				return;
			for (int j = 0; j < arrayDiff.length / 2; j += 2) {
				int from = arrayDiff[j];
				int to = arrayDiff[j + 1];

				int y = height / 2 - cardImages[0].getHeight() / 2 / 2;
				int xFrom = (width / 4) + (from * diff)
						- cardImages[0].getWidth() / 8 + diff / 2;
				int xTo = (width / 4) + (to * diff) - cardImages[0].getWidth()
						/ 8 + diff / 2;
				Point p1 = new Point(xFrom, y);
				Point p2 = new Point(xTo, y);

				double angle = Math.atan2(0, xTo - xFrom);
				int diameter = (int) Math.round(p1.distance(p2));
				g.setStroke(new BasicStroke(1));
				Graphics2D g2d = (Graphics2D) g;
				g2d.translate(p1.x, p1.y);
				g2d.rotate(angle);
				g2d.drawArc(0, -25, diameter, 50, 0, 180);
				g2d.fill(new Polygon(new int[] { 0, 5, -5 }, new int[] { 0, -5,
						-5 }, 3));
				g2d.translate(-p1.x, -p1.y);
				g2d.translate(p2.x, p2.y);
				g2d.fill(new Polygon(new int[] { 0, 5, -5 }, new int[] { 0, -5,
						-5 }, 3));
				g.translate(-p2.x, -p2.y);
			}
		}
	}

	public BufferedImage colorImage(BufferedImage loadImg, int red, int green,
			int blue) {
		BufferedImage img = new BufferedImage(loadImg.getWidth(),
				loadImg.getHeight(), BufferedImage.TRANSLUCENT);
		Graphics2D graphics = img.createGraphics();
		Color newColor = new Color(red, green, blue, 0);
		graphics.setXORMode(newColor);
		graphics.drawImage(loadImg, null, 0, 0);
		graphics.dispose();
		return img;
	}

	public List<HistoryItem> getHistoryItems() {
		return historyItems;
	}

}
