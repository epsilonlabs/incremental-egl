/*******************************************************************************
 * Copyright (c) 2011 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.egl.engine.traceability.fine.test.acceptance;

import static org.eclipse.epsilon.egl.util.FileUtil.FILE_SEP;

import java.io.File;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.epsilon.common.util.FileUtil;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.engine.traceability.fine.EglFineGrainedTraceContextAdaptor;
import org.eclipse.epsilon.egl.engine.traceability.fine.trace.Trace;
import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;
import org.eclipse.epsilon.egl.test.acceptance.AcceptanceTestUtil;
import org.eclipse.epsilon.emc.emf.EmfUtil;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.java.JavaModel;
import org.eclipse.epsilon.test.util.ModelWithEolAssertions;

public class EglFineGrainedTraceabilityAcceptanceTest {

	protected static ModelWithEolAssertions trace;
	
	protected static void generateTrace(String egl, EObject root) throws Exception {
		generateTrace(egl, root, "Out.txt");
	}
	
	protected static void generateTrace(String egl, EObject root, String destination) throws Exception {
		runEgl(egl, root, destination);
	}
	
	protected static String getOutputPath() {
		return FileUtil.getDirectoryOf(EglFineGrainedTraceabilityAcceptanceTest.class).getAbsolutePath();
	}
	
	protected static boolean deleteOutputFile(String relativePath) {
		return new File(getOutputPath() + FILE_SEP + relativePath).delete();
	}

	private static void runEgl(String egl, EObject root, String destination) throws Exception {
		deleteOutputFile(destination);
		AcceptanceTestUtil.generate(createTemplateFactory(), egl, destination, new InMemoryEmfModel("Ecore", EmfUtil.createResource(root), EcorePackage.eINSTANCE));
	}

	private static EglFileGeneratingTemplateFactory createTemplateFactory() throws EglRuntimeException {
		final EglFileGeneratingTemplateFactory factory = new EglFileGeneratingTemplateFactory();
		factory.setOutputRoot(getOutputPath());
		createTraceWithAssertions(new EglFineGrainedTraceContextAdaptor().adapt(factory.getContext()));
		return factory;
	}
	
	private static void createTraceWithAssertions(Trace root) {
		IModel traceModel = new JavaModel("TraceModel", Collections.singleton(root), Collections.singleton(Trace.class));
		trace = new ModelWithEolAssertions(traceModel);
		trace.setVariable("trace", "Trace.all.first");
	}
}
