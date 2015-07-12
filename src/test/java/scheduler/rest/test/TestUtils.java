package scheduler.rest.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A class utilities with method helpers
 * @author Sandrine Ben Mabrouk
 *
 */
public class TestUtils {

	/**
	 * Reads the content of a file that is in the resources test folder.
	 * @param filename the file path from the resources test folder.
	 * @return the content of the file
	 * @throws IOException occurs if the file does not exists or there is an error during the reading.
	 */
	public static String readScriptFile(String filename) throws IOException{
		File inputFile = new File(TestUtils.class.getClassLoader().getResource(filename).getFile());
		StringBuilder result = new StringBuilder();
		String currentLine = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile)))
		{
			while ((currentLine = br.readLine()) != null) {
				result.append(currentLine);
				result.append("\n");
			}
		} 
		return result.toString();
	}
}
