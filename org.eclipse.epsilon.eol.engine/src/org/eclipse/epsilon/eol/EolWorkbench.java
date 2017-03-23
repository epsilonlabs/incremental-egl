/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.eol;

import java.util.Arrays;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.IPropertyGetter;
import org.eclipse.epsilon.eol.execute.introspection.recording.IPropertyAccess;
import org.eclipse.epsilon.eol.execute.introspection.recording.PropertyAccessExecutionListener;
import org.eclipse.epsilon.eol.execute.introspection.recording.PropertyAccessRecorder;
import org.eclipse.epsilon.eol.models.java.JavaModel;

 
public class EolWorkbench {
	
	public static void main(String[] args) throws Exception {
		// Create a simple EOL module with a property access recorder attached
		EolEvaluator module = new EolEvaluator();
		PropertyAccessRecorder recorder = new PropertyAccessRecorder();
		module.getContext().getExecutorFactory().addExecutionListener(new PropertyAccessExecutionListener(recorder));
		
		// Add a model to the context, backed by a Java object
		// The model contains a single Person with name=Bob and age=23
		module.getContext().getModelRepository().addModel(new JavaModel("People", Arrays.asList(new Person("Bob", 23)), Arrays.asList(Person.class)));
	
		// Obtain a reference to the first person in EOL
		module.execute("var p = Person.all.first;");
	
		// Access the properties of EOL whilst recording
		recorder.startRecording();
		module.execute("p.name.println();");
		module.execute("p.age.println();");
		recorder.stopRecording();
		
		// Iterate over all property accesses and obtain their values
		for (IPropertyAccess access : recorder.getPropertyAccesses().all()) {
			System.out.println("Object: " + access.getModelElement());
			System.out.println("Property: " + access.getPropertyName());
			System.out.println("Value: " + getValueFromPropertyAccess(access, module.getContext()));
		}
	}
	
	public static Object getValueFromPropertyAccess(IPropertyAccess propertyAccess, IEolContext context) throws EolRuntimeException {
		IPropertyGetter getter = context.getIntrospectionManager().getPropertyGetterFor(propertyAccess.getModelElement(), propertyAccess.getPropertyName(), context);
		return getter.invoke(propertyAccess.getModelElement(), propertyAccess.getPropertyName());
	}
	
	public static class Person {
		private String name;
		private int age;
		
		public Person(String name, int age) {
			this.name = name;
			this.age = age;
		}
 
		public int getAge() {
			return age;
		}
 
		public void setAge(int age) {
			this.age = age;
		}
 
		public String getName() {
			return name;
		}
 
		public void setName(String name) {
			this.name = name;
		}
	}
}

/*
import org.eclipse.epsilon.common.parse.Antlr3TreeViewer;
import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.eol.parse.EolParser;
import java.io.File;

public class EolWorkbench {
	
	
	public static void main(String[] args) throws Exception {
		
		EolEvaluator evaluator = new EolEvaluator();
		evaluator.execute("var a : Foo;");
		
		if (true) return;
		
		EolModule module = new EolModule();
		
		String path = "E:\\Projects\\Eclipse\\3.4\\workspace3\\org.eclipse.epsilon.eol.engine\\src\\org\\eclipse\\epsilon\\eol\\test.eol";		
		try {
			//module.parse(new File(path));
			module.parse("var i : Integer; \r\n -- A comment \r\n -- Another comment \r\n //var b : String;");
		}
		catch (Exception ex) {
			int index = module.parser.getTokenStream().index();
			System.err.println(index);
		}
		//System.err.println(module.parser.);
		//System.err.println(module.parser.getNumberOfSyntaxErrors());
		
		for (ParseProblem problem : module.getParseProblems()) {
			System.err.println(problem);
		}
	}
	
}
*/
