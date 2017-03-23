package org.eclipse.epsilon.egx.notifiers;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;

public class ModelNotifier {
	
	private Resource resource;
	private EmfModel sourceModel;
	private EmfModel ecoreModel;

	public static void main(String[] args) {
		
		ModelNotifier mob = new ModelNotifier();
		try {
			mob.loadEcoreModel();
			mob.loadModel();
		} catch (EolModelLoadingException e) {
			e.printStackTrace();
		}
		
		mob.createResource();
		mob.modifyModel();
		//mob.modifyResource();

	}

	private static String modificationScript = "src/scripts/v23-24.eol";
	
	public Resource createResource() {
		resource = sourceModel.getResource();
		resource.setTrackingModification(true);
		EContentAdapter adapter = new EContentAdapter() {

			public Notifier getTarget() {
				return null;
			}

			public boolean isAdapterForType(Object type) {
				return false;
			}

			public void notifyChanged(Notification notification) {
				System.out.println("something chnaged --- " + notification);
			}

			public void setTarget(Notifier newTarget) {
				
			}
			
		};
		
		resource.eAdapters().add(adapter);
		for(EObject obj : resource.getContents()) {
			obj.eAdapters().add(adapter);
		}
		return resource;
	}
	
	private void loadModel() throws EolModelLoadingException {
		sourceModel = new EmfModel();
		sourceModel.setMetamodelUri("http://www.eclipse.org/emf/2002/Ecore");
		sourceModel.setModelFile("src/models/gmfgraph_1.23.ecore");
		sourceModel.setName("gmfgraph");
		sourceModel.setStoredOnDisposal(true);

		sourceModel.load();
	}
	
	private void loadEcoreModel() throws EolModelLoadingException {
		ecoreModel = new EmfModel();
		ecoreModel.setMetamodelUri("http://www.eclipse.org/emf/2002/Ecore");
		ecoreModel.setModelFile("src/metamodel/Ecore.ecore");
		ecoreModel.setName("ecore");
		ecoreModel.load();
	}
	
	private void modifyModel() {
		EolModule eolModule = new EolModule();
		eolModule.getContext().getModelRepository().addModel(sourceModel);
		
		try {
			eolModule.parse(new File(modificationScript));
			eolModule.execute();
			//sourceModel.store();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void modifyResource() {
		EObject obj = EcoreFactory.eINSTANCE.createEClass();
		obj.eClass().setName("ScalablePolygon");
		resource.getContents().add(obj);
		try {
			resource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

