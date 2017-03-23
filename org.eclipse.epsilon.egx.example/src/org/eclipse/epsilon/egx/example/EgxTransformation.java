package org.eclipse.epsilon.egx.example;

import java.io.File;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egx.incremental.EgxModuleInc;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;


public class EgxTransformation {
	
	protected EgxModuleInc egxModule;
	private Resource resource;
	protected EmfModel sourceModel;
	protected EmfModel ecoreModel;
	
	
	protected File egxFile = new File("src/templates/library/example.egx");
	protected String metamodelFile = "src/metamodel/library.ecore";
	protected String modelFile = "src/model/library.model";
	protected String modelName = "library";
	protected String modificationScript = "src/scripts/modify.eol";
	
	
	public static void main(String[] args) throws Exception {
		
		EgxTransformation transformation = new EgxTransformation();
		
		transformation.loadEcoreModel();
		transformation.loadModel();

		transformation.setupTransformation();
		transformation.executeTransformation();
		
		transformation.createResource();
		transformation.modifyModel();
		
	}


	public Resource createResource() {
		resource = sourceModel.getResource();
		
		return resource;
	}

	public void setupTransformation() throws Exception, EolModelLoadingException {
		egxModule = new EgxModuleInc(new EglFileGeneratingTemplateFactory());
		egxModule.parse(egxFile);

		egxModule.getContext().getModelRepository().addModel(sourceModel);
		egxModule.setLaunchConfigName("library-offline-example");
		
		if(!egxModule.getParseProblems().isEmpty())
			System.err.println(egxModule.getParseProblems());
	}
	
	public void executeTransformation() throws EolModelLoadingException, Exception {
		if(egxModule.isOfflineMode())
			setupTransformation();
		
		try {
			egxModule.execute();
		} catch (EolRuntimeException e) {
			e.printStackTrace();
		}
	}
	

	protected void loadModel() throws EolModelLoadingException {
		sourceModel = null;
		sourceModel = new EmfModel();
		sourceModel.setMetamodelFile(metamodelFile);
		sourceModel.setModelFile(modelFile);
		sourceModel.setName(modelName);
		sourceModel.load();
	}
	
	protected void loadEcoreModel() throws EolModelLoadingException {
		ecoreModel = new EmfModel();
		//ecoreModel.setMetamodelUri("http://www.eclipse.org/emf/2002/Ecore");
		ecoreModel.setModelFile("src/metamodel/Ecore.ecore");
		ecoreModel.setName("ecore");
		ecoreModel.load();
	}
	
	public void modifyModel() throws EolModelLoadingException {
		EolModule eolModule = new EolModule();
		eolModule.getContext().getModelRepository().addModel(sourceModel);
		eolModule.getContext().getModelRepository().addModel(ecoreModel);
		
		try {
			eolModule.parse(new File(modificationScript));
			eolModule.execute();
			//resource.save(null);
			sourceModel.store();
			executeTransformation();
		} catch (Exception e) {
			System.err.println(eolModule.getParseProblems());
			e.printStackTrace();
		}
	}
}
