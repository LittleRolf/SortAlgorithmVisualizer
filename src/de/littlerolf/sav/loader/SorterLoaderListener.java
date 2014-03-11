package de.littlerolf.sav.loader;

public interface SorterLoaderListener {
	void onSorterLoadingStarted();
	void onSorterLoaded(int progress, int max);
	void onSorterLoadingFinished();
}
