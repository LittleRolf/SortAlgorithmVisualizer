package de.littlerolf.sav.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ArrayDiff {

	/**
	 * A method to find out which numbers were moved in the array. the runtime
	 * in Landau-Notation is approximately O(n+n²), so do not use it on big
	 * arrays
	 * 
	 * @param source
	 *            The source array (aka the previous version)
	 * @param result
	 *            The array resulting from the movement (aka the current
	 *            version)
	 * @return An array containing the movements. The array is build up as
	 *         follows: from1, to1, from2, to2 ... fromN, toN .If there is no
	 *         diff, null is returned
	 */
	public static int[] getArrayDiff(int[] source, int[] result) {
		List<Integer> diff = new ArrayList<Integer>();

		if (source.length != result.length) {
			throw new IllegalArgumentException(
					"The arrays need to have the same length");
		}
		boolean[] diffs = new boolean[source.length];
		Arrays.fill(diffs, false);
		boolean somethingChanged = false;
		for (int i = 0; i < source.length; i++) {
			if (source[i] != result[i]) {
				diffs[i] = true;
				somethingChanged = true;
			}
		}
		if (somethingChanged) {
			for (int i = 0; i < diffs.length; i++) {
				if (diffs[i]) {
					int diffValue = source[i];
					for (int j = 0; j < diffs.length; j++) {
						if (diffs[j] && diffValue == result[j]) {
							diff.add(i);
							diff.add(j);
							diffs[i] = false;
							diffs[j] = false;
						}
					}
				}
			}

			return convertIntegers(diff);
		}
		return null;

	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}
}
