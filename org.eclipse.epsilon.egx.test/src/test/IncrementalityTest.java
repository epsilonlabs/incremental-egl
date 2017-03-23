package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashSet;
import org.eclipse.epsilon.common.util.FileUtil;
import org.eclipse.epsilon.egx.test.TestFrame;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.junit.Test;


public class IncrementalityTest {
	
	@Test
	public void testSameContents() throws EolModelLoadingException, EolRuntimeException, Exception {
		TestFrame test = new TestFrame();
		//test.executeTransformation(0);
		//assertEquals(true, sameContents(test.executeTransformation(0), test.executeTransformation(1)));
		//test.executeTransformation(0, "library_1", "library", "example.egx");
		assertEquals(true, sameContents(test.executeTransformation(0, "library", "library", "library", "example.egx"), test.executeTransformation(1, "library", "library", "library", "example.egx")));
	}
	
	public boolean sameContents(File fileExpected, File fileActual) throws EolModelLoadingException, EolRuntimeException, Exception {
		TestFrame test = new TestFrame();
		//System.out.println(fileExpected.getCanonicalPath());
		return test.sameContents(fileExpected, fileActual);
	}
	
	public boolean equalDirs(File incrDir, File nonIncrDir) {
		HashSet<?> firstDir = FileUtil.listFilesAsSet(incrDir);
		HashSet<?> secondDir = FileUtil.listFilesAsSet(nonIncrDir);
		//FileUtil.listFilesAsSet(new File(FileUtil.getAbsolutePath("src/outputs", "non-incr")));
		return firstDir.containsAll(secondDir);	
	}

}
