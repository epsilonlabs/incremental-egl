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
package org.eclipse.epsilon.egx.engine.test.acceptance.rules;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.eclipse.epsilon.egx.engine.test.acceptance.util.EgxAcceptanceTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class RulesAreTyped extends EgxAcceptanceTest {

	private static final String egx = "rule Person2Greeting "       +
	                                  "  transform p : Person {"    +
	                                  "    template: \"hello.egl\"" +
	                                  "    target: \"out.txt\" "    + 
	                                  "}";

	private static final String model = "Families { "              +
	                                    "  Person { "              +
	                                    "    name: \"John\""       +
	                                    "  }"                      + 
	                                    "  Family { "              +
	                                    "    name: \"The Smiths\"" +
	                                    "  }"                      + 
	                                    "}";

	@BeforeClass
	public static void setup() throws Exception {
		runEgx(egx, model, template("hello.egl", "Hello [%=p.name%]"));
	}
	
	@Test
	public void targetContainsGeneratedText() {
		assertEquals("Hello John", factory.getContentFor("out.txt"));
	}
	
	@Test
	public void noOtherFilesAreGeneratd() {
		assertThat(factory.getOutputFiles(), containsInAnyOrder("out.txt"));
	}
}
