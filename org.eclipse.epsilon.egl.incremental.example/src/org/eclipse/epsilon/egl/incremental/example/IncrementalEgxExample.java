package org.eclipse.epsilon.egl.incremental.example;

import java.io.File;

import library.Book;
import library.Library;
import library.LibraryFactory;
import library.LibraryPackage;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egx.incremental.EgxModuleInc;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;

public class IncrementalEgxExample {
	
	public static void main(String[] args) throws Exception {
		
		// Create a library model with one book
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", 
				new XMIResourceFactoryImpl());
		Resource resource = resourceSet.createResource(URI.createURI("library.xmi"));
		Library library = LibraryFactory.eINSTANCE.createLibrary();
		Book book = LibraryFactory.eINSTANCE.createBook();
		library.getBooks().add(book);
		book.setTitle("Book1");
		resource.getContents().add(library);
		
		// Run the transformation on the model
		EgxModuleInc egxModule = new EgxModuleInc(new EglFileGeneratingTemplateFactory());
		egxModule.parse(new File("templates/example.egx"));
		InMemoryEmfModel model = new InMemoryEmfModel("M", resource, LibraryPackage.eINSTANCE);
		model.setCachingEnabled(false);
		egxModule.getContext().getModelRepository().addModel(model);
		egxModule.setLaunchConfigName("library-offline-example");
		egxModule.execute();
		
		// Add a second book to the model
		Book book2 = LibraryFactory.eINSTANCE.createBook();
		book2.setTitle("Book2");
		library.getBooks().add(book2);
		
		// Run the transformation again (only a file for the new book will be generated)
		egxModule = new EgxModuleInc(new EglFileGeneratingTemplateFactory());
		egxModule.parse(new File("templates/example.egx"));
		egxModule.getContext().getModelRepository().addModel(model);
		egxModule.setLaunchConfigName("library-offline-example");
		egxModule.execute();
		
	}
	
}
