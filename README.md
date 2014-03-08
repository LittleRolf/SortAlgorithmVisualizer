#SortAlgorithmVisualizer
[![Build Status](https://travis-ci.org/LittleRolf/SortAlgorithmVisualizer.png?branch=master)](https://travis-ci.org/LittleRolf/SortAlgorithmVisualizer)

A program made to visualize different sorting-algorithms which need to be provided manually.
Download the program [here](https://littlerolf.github.io/SortAlgorithmVisualizer/jar/SAV.jar). The API-JAR is available [here](https://littlerolf.github.io/SortAlgorithmVisualizer/jar/SAV_API.jar).

Your class has to look like this:

```java
package examplecompany;
import de.littlerolf.sav.data.*;

public class Sorter extends BaseSorter {
	@Override	
	public int[] sortArray(int[] numbers) {
		//Insert algorithm here. At each step, call:
		saveHistory(numbers);	
	}
	
	@Override
	public String getName() {
		return "AwesomeSort";
	}
}
```

Note that the package name must not contain any othe character than a-z & A-Z. So package name `com.epicsort.sort` is invalid, but `epicsort` would be valid.

In the main program, select the folder, where all the package folders containing the Sorter.class files are.

#License
MIT

Playing Card Set Images from http://sourceforge.net/projects/vector-cards/ licensed LGPL 3.0.
