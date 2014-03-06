#SortAlgorithmVisualizer
[![Build Status](https://travis-ci.org/LittleRolf/SortAlgorithmVisualizer.png?branch=master)](https://travis-ci.org/LittleRolf/SortAlgorithmVisualizer)

A program made to visualize different sorting-algorithms which need to be provided manually.
Download the program [here](https://littlerolf.github.io/SortAlgorithmVisualizer/jar/SAV.jar). The API-JAR is available [here](https://littlerolf.github.io/SortAlgorithmVisualizer/jar/SAV_API.jar).
Your class has to look like this:

```java
package examplecompany;
import de.littlerolf.sav.data.*;

public class Sorter extends BaseSorter {
	public int[] sortArray(int[] numbers) {
	//Insert algorithm here. At each step, call:
	saveHistory(numbers);	
	}
}
```
#License
MIT

Playing Card Set Images from http://sourceforge.net/projects/vector-cards/ licensed LGPL 3.0.
