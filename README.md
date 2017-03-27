# Incremental EGL
This is a prototype implementation of Property access traces used in Epsilon Generation Language to facilitate incremental model-to-text transformation. Property access traces use runtime analysis to capture information about the way in which a model-to-text transformation accesses its source models and use this data to determine which parts of the trnasformation require re-execution after the source model is modified

# Installation

* Install version 1.1 of Epsilon
  * https://www.eclipse.org/epsilon/download/?version=1.1
* Clone the repository
* Import all projects into your Eclipse workspace

# Quick start
A minimal example of incremental transformation in the offline mode is provided in the **org.eclipse.epsilon.egl.incremental.example** project in the repository. The main method of *IncrementalEgxExample.java* (illustrated below) creates a 1-book [library model](help.eclipse.org/juno/topic/org.eclipse.emf.doc/tutorials/clibmod/clibmod.html) and runs a transformation (*templates/example.egx*) that generates one text file per book on it. It then adds one more book to the model and runs the transformation again. In its second (incremental) execution, the transformation only generates a file for the newly-created book.

```java
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
```

The templates that comprise the transformation are listed below.

### example.egx
```javascript
rule Book2Text 
	transform b : Book{
	
	template : "book.egl";
	target : "./output/" + b.title + ".txt"
}
```

### book.egl
```javascript
Book title: [%= b.title %]
Pages : [%= b.pages%]

Author(s):
____________________________

[% for(w in b.author) { %]
[%= w.name + " " + b.title %]
[% } %]
```
	
  
