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
package org.eclipse.epsilon.egl.test.acceptance.engine.reset;

import static org.junit.Assert.*;

import org.eclipse.epsilon.egl.EglTemplate;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.junit.Test;

public class ResettingTemplates {

	@Test
	public void clearsBuffer() throws Exception {
		EglTemplate template = prepareTemplate("Hello world!");
		template.process();
		template.reset();
		
		assertEquals("Hello world!", template.process());
	}
	
	@Test
	public void clearsVariableValues() throws Exception {
		String code = "[% var x; if (x.isUndefined()) { x = 'new'; } else { x = 'old'; } %][%=x%]";
		
		EglTemplate template = prepareTemplate(code);
		template.process();
		template.reset();
		
		assertEquals("new", template.process());
	}
	

	private EglTemplate prepareTemplate(String code) throws Exception {
		EglTemplateFactory factory = new EglTemplateFactory();
		EglTemplate template = factory.prepare(code);
		return template;
	}
}
