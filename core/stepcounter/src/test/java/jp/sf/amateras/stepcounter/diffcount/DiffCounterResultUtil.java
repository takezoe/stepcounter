package jp.sf.amateras.stepcounter.diffcount;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.junit.Assert;

public class DiffCounterResultUtil {

	public static String encode	= System.getProperty("file.encoding");

	private DiffCounterResultUtil() {}

	public static String load(URL url) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(url.getFile())), encode));
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			return null;
		}

		StringBuilder strBuilder = new StringBuilder();
		try {
			while (reader.ready()) {
				String line = reader.readLine();
				strBuilder.append(line).append("\n");
			}

			strBuilder.deleteCharAt(strBuilder.lastIndexOf("\n"));
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		return strBuilder.toString();
	}

	public static void assertEquals(String expected, String actual) {
		Assert.assertEquals(expected, actual);
	}

	public static void assertEquals(URL expectedFileUrl, String actual) {
		String expected = load(expectedFileUrl);
		assertEquals(expected, actual);
	}
}
