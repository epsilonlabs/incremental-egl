/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.egl.test.acceptance.output.newlines;

import static org.junit.Assert.assertEquals;

import org.eclipse.epsilon.egl.test.acceptance.AcceptanceTestUtil;
import org.eclipse.epsilon.egl.util.FileUtil;
import org.junit.Test;

public class OutputNewlines {
	
	// When processed, a template should produce newlines that are compatible with the current operating system
	
	private String normalisedOutput = "Foo" + FileUtil.NEWLINE + "Bar";
	
	@Test
	public void windowsNewlinesAreNormalised() throws Exception {
		final String program = "Foo\r\n" +
		                       "Bar";
		
		assertEquals(normalisedOutput, AcceptanceTestUtil.run(program));
	}
	
	@Test
	public void unixNewlinesAreNormalised() throws Exception {
		final String program = "Foo\n" +
		                       "Bar";
		
		assertEquals(normalisedOutput, AcceptanceTestUtil.run(program));
	}
	
	@Test
	public void oldMacNewlinesAreNormalised() throws Exception {
		final String program = "Foo\r" +
		                       "Bar";
		
		assertEquals(normalisedOutput, AcceptanceTestUtil.run(program));
	}
}
