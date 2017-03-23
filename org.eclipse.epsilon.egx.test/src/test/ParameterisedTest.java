package test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.epsilon.egx.test.TestFrame;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ParameterisedTest {
	
	private String mFile;
	private String modelName = "gmfgraph";
	private String mmFile = "";
	private String egxTemplate = "pongo.egx";
	
	public ParameterisedTest(String modelFile) {
		this.mFile = modelFile;
	}
	

	@Parameters(name = "{index}: model({0})")
	public static Collection<Object[]> data() throws EolModelLoadingException, EolRuntimeException, Exception {
		Object[][] data = new Object[][] { {"gmfgraph_1.23"}, {"gmfgraph_1.24"}, {"gmfgraph_1.25"}, {"gmfgraph_1.26"},
											{"gmfgraph_1.27"}, {"gmfgraph_1.28"}, {"gmfgraph_1.29"}, {"gmfgraph_1.30"},
											{"gmfgraph_1.31"}, {"gmfgraph_1.32"}, {"gmfgraph_1.33"} };
		//Object[][] data = new Object[][] { {"library_1"}, {"library_2"}, {"library_3"} };
		//TestFrame tester = new TestFrame();
		//tester.executeTransformation(0,"library_1","library");
		return Arrays.asList(data);
	}
	
	@Test
	public void testTransException() throws EolModelLoadingException, EolRuntimeException, Exception {
		TestFrame tester = new TestFrame();
		//tester.executeTransformation(0,this.model,"library");
		assertTrue(tester.sameContents(tester.executeTransformation(0, this.modelName, this.mFile, this.mmFile, this.egxTemplate), tester.executeTransformation(1, this.modelName, this.mFile, this.mmFile, this.egxTemplate)));
	}
	
}
