/*******************************************************************************
 * Copyright (c) 2014 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.egx.engine.test.acceptance.util;

import java.io.File;

import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.common.util.FileUtil;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.emc.emf.AbstractEmfModel;
import org.eclipse.epsilon.hutn.test.model.HutnTestWithFamiliesMetaModel;

public class EgxAcceptanceTest extends HutnTestWithFamiliesMetaModel {

	protected static final VirtualTemplateFactory factory = new VirtualTemplateFactory();
	
	public static void runEgx(String source, String hutnModel, Template... templates) throws Exception {
		File egxFile = setupFactory(source, templates);
		
		EgxModule module = new EgxModule(factory);
		module.getContext().getModelRepository().addModel(hutnToFamilyModel(hutnModel));
		module.parse(source, egxFile);
		
		if (!module.getParseProblems().isEmpty()) {
			for (ParseProblem p : module.getParseProblems()) {
				System.err.println(p);
			}
			throw new IllegalArgumentException("Error encountered whilst parsing EGX");
		}
		
		module.execute();
	}

	private static File setupFactory(String source, Template... templates) {
		factory.addVirtualTemplate("base.egx", source);
		for (Template template : templates) {
			factory.addVirtualTemplate(template.name, template.content);
		}
		
		File egxFile = FileUtil.getFile("base.egx", EgxAcceptanceTest.class);
		factory.setRoot(egxFile.getParentFile().toURI());
		return egxFile;
	}

	private static AbstractEmfModel hutnToFamilyModel(String hutnModel) {
		return new FamiliesModelConstructor().constructModel("MyFamily", hutnModel);
	}

	protected static Template template(String name, String content) {
		return new Template(name, content);
	}
	
	protected static class Template {
		public final String name, content;
		
		public Template(String name, String content) {
			this.name = name;
			this.content = content;
		}
	}
}
