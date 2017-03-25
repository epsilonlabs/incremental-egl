# Incremental EGL
This is a prototype implementation of Property access traces used in Epsilon Generation Language to facilitate incremental model-to-text transformation. Property access traces use runtime analysis to capture information about the way in which a model-to-text transformation accesses its source models and use this data to determine which parts of the trnasformation require re-execution after the source model is modified

# Installation

* Install version 1.1 of Epsilon
  * https://www.eclipse.org/epsilon/download/?version=1.1
* Clone the repository
* Import all projects into your Eclipse workspace

# Quick start
A minimal example of incremental transformation in the offline mode is provided in the repository: **org.eclipse.epsilon.egx.example** which includes a sample **Library** model. A runner method is provided in *EgxTransformation.java* that executes the transformation in the following order:
* Loads the Library model and templates: *example.egx* and *BookReport.egl*
```javascript
//  example.egx
rule Book2Report
  transform b : library!Book {
	template { 
		return "BookReport.egl";
	}
	target : "./output/" + b.title + ".txt"
}

//  BookReport.egl
Number of authors in library: [%= Writer.allInstances.size() %]
Book title: [%= b.title %]
Pages : [%= b.getPages()%]

Author(s):
____________________________

[% for(w in b.author){ %]
[%= w.name + " " + b.title %]
[% } %]

[% @template
operation Book getPages() { %]
	[%= self.pages + 2 %]
[% } %]
```
* Executes the transformation
* Modifies the source model (modify.eol)
  * Adds a new Book object to the model
* Re-executes the transformation in the offline mode



	
  
