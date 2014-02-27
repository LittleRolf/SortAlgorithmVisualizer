package de.littlerolf.sav.data;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSorter {
	
	private List<HistoryItem> history = new ArrayList<HistoryItem>();

	public abstract int[] sortArray(int[] values);
	
	public void saveHistory(int values[]) {
		HistoryItem i = new HistoryItem();
		i.values = values;
		history.add(i);
	}
	
	public List<HistoryItem> getHistory() {
		return history;
	}
}
