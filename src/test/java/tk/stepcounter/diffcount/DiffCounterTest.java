package tk.stepcounter.diffcount;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tk.stepcounter.diffcount.object.DiffFolderResult;

public class DiffCounterTest {

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testCount_java() {
		String oldRoot = "test/sample/java/root1";
		String newRoot = "test/sample/java/root2";

		URL url = this.getClass().getResource(
				"DiffCounterTest_testCount_java.txt");

		DiffFolderResult root = DiffCounter.count(new File(oldRoot), new File(newRoot));

		DiffCounterResultUtil.assertEquals(url, root.toString());
	}

}
