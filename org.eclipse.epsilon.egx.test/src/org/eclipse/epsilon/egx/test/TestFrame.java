package org.eclipse.epsilon.egx.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.epsilon.common.util.FileUtil;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.egx.incremental.EgxModuleInc;
import org.eclipse.epsilon.emc.emf.EmfModelFactory;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;

import templates.Templates;

public class TestFrame {
	
	public static void main(String[] args) throws Exception {
	
		TestFrame test = new TestFrame();
		//test.executeTransformation("0");
		//test.executeTransformation(0, "library", "library");
		//test.executeTransformation(1, "library_3", "library");
		//test.executeTransformation(0);
		//test.compareFileContent(new File("src/outputs"));
		//test.executeTransformation(0, "gmfgraph", "gmfgraph_1.24", "", "pongo.egx");
		System.out.println(test.sameContents(new File("src/outputs/incr-tal"), new File("src/outputs/non-incr")));
		
	}
	
	public File executeTransformation(int mode, String model, String modelFile, String mmFile, String egxTemplate) throws EolModelLoadingException,Exception, EolRuntimeException {
		
		IModel inputModel = null;
		String modelPath;
		
		if(mmFile == "") {
			modelPath = "../models/" + modelFile + ".ecore";
			inputModel = EmfModelFactory.getInstance().loadEmfModel(model, new File(FileUtil.getFile(modelPath, Templates.class).toString()), EcorePackage.eINSTANCE);
		}
		else {
			modelPath = "../models/" + modelFile + ".model";
			String metamodelPath = "../metamodels/" + mmFile + ".ecore";
			inputModel = EmfModelFactory.getInstance().loadEmfModel(model, new File(FileUtil.getFile(modelPath, Templates.class).toString()), new File(FileUtil.getFile(metamodelPath, Templates.class).toString()));
		}
		
		
		EglFileGeneratingTemplateFactory templateFactory = new EglFileGeneratingTemplateFactory();	
		EgxModule module = null;

		if(mode == 0) {
			System.out.println("INCR");
			module = new EgxModuleInc(templateFactory);
			((EgxModuleInc)module).setLaunchConfigName("foo");
			templateFactory.setOutputRoot(FileUtil.getAbsolutePath("src/outputs", "incr-tal"));
			}
		else {
			System.err.println("NON-INCR");
			deleteDir("src/outputs/non-incr");
			module = new EgxModule(templateFactory);
			templateFactory.setOutputRoot(FileUtil.getAbsolutePath("src/outputs", "non-incr"));
			}

		module.getContext().getModelRepository().addModel(inputModel);

		module.parse(FileUtil.getFile(egxTemplate, Templates.class));

		module.execute();
		
		if(mode == 0)
			return new File(FileUtil.getAbsolutePath("src/outputs", "incr-tal"));
		else 
			return new File(FileUtil.getAbsolutePath("src/outputs", "non-incr"));
		
		}
	
	
	public boolean sameContents(File fileExpected, File fileActual) throws IOException {
		deleteDir("src/outputs/non-incr/.DS_Store");
		deleteDir("src/outputs/incr-tal/.DS_Store");
		
		if (fileExpected.isDirectory() != fileActual.isDirectory()) {
			// One is a file, the other is a directory: not the same
			return false;
		}

		if (fileExpected.isDirectory()) {
			// Both are directories: they should contain the same filenames,
			// and each pair should have the same contents
			final Set<String> expectedFilenames = listFilesAsSet(fileExpected);
			final Set<String> actualFilenames = listFilesAsSet(fileActual);
			if (!expectedFilenames.equals(actualFilenames)) {
				return false;
			}
			for (String filename : expectedFilenames) {
				final File expectedEntry = new File(fileExpected, filename);
				final File actualEntry = new File(fileActual, filename);
				if (!sameContents(expectedEntry, actualEntry)) {
					return false;
				}
			}
			return true;
		}
		else {
			if (fileExpected.length() != fileActual.length()) {
				// Different length: no need to read the files
				return false;
			}

			final FileInputStream isExpected = new FileInputStream(fileExpected);
			try {
				final FileInputStream isActual = new FileInputStream(fileActual);
				try {
					return sameContents(isExpected, isActual);
				} finally {
					isActual.close();
				}
			} finally {
				isExpected.close();
			}
		}
	}
	
	public static boolean sameContents(InputStream isExpected, InputStream isActual) throws IOException {
		int chExpected, chActual;
	
		do {
			chExpected = isExpected.read();
			chActual = isActual.read();
		}
		while (chExpected == chActual && chExpected > 0 && chActual > 0);
	
		return chExpected == chActual;
	}
	
	public static HashSet<String> listFilesAsSet(File fileExpected) {
		return new HashSet<String>(Arrays.asList(fileExpected.list()));
	}
	
	public File executeTransformation(String mode) throws EolModelLoadingException, Exception, EolRuntimeException {
		
		EglFileGeneratingTemplateFactory templateFactory = new EglFileGeneratingTemplateFactory();
		
		IModel model = EmfModelFactory.getInstance().loadEmfModel("gmfgraph", new File(FileUtil.getFile("../metamodels/gmfgraph_1.24.ecore", Templates.class).toString()), EcorePackage.eINSTANCE);
		EgxModule module = null;
		
		if(mode == "0") {
			module = new EgxModuleInc(templateFactory);
			((EgxModuleInc)module).setLaunchConfigName("foo");
			templateFactory.setOutputRoot(FileUtil.getAbsolutePath("src/outputs", "incr-tal"));
		}
		else {
			module = new EgxModule(templateFactory);
			templateFactory.setOutputRoot(FileUtil.getAbsolutePath("src/outputs", "non-incr"));
		}
		
		module.getContext().getModelRepository().addModel(model);
		
		module.parse(FileUtil.getFile("pongo.egx", Templates.class));
		
		module.execute();
		
		if(mode == "0")
			return new File(FileUtil.getAbsolutePath("src/outputs", "incr-tal"));
		else
			return new File(FileUtil.getAbsolutePath("src/outputs", "non-incr"));
		
		}
	
	public void deleteDir(String dir) {
		File path = new File(dir);
	    if( path.exists() ) {
	    	if(path.isDirectory()) {
	    		File[] files = path.listFiles();
	    		for(int i=0; i<files.length; i++) {
	    			if(files[i].isDirectory()) {
	    				deleteDir(files[i].toString());
	    			}
	    			else {
	    				files[i].delete();
	    			}
	    		}
	    	}
	    	else if(path.isFile()) {
	    		path.delete();
	    	}
	    }    
	}
	
}