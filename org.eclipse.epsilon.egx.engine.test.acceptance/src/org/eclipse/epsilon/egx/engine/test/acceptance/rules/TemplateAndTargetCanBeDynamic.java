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

import static org.junit.Assert.assertEquals;

import org.eclipse.epsilon.egx.engine.test.acceptance.util.EgxAcceptanceTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class TemplateAndTargetCanBeDynamic extends EgxAcceptanceTest {

	private static final String egx = "rule Person2Greeting "           +
	                                  "  transform p : Person {"        +
	                                  "    template: p.name + \".egl\"" +
	                                  "    target: p.name + \".txt\" "  + 
	                                  "}";
	
	private static final String model = "Families { "        +
	                                    "  Person { "        +
	                                    "    name: \"John\"" +
	                                    "  }"                +
	                                    "  Person { "        +
	                                    "    name: \"Jane\"" +
	                                    "  }"                + 
	                                    "}";
	
	@BeforeClass
	public static void setup() throws Exception {
		runEgx(egx, model,
			template("John.egl", "Hello Mr. [%=p.name%]"),
			template("Jane.egl", "Hello Ms. [%=p.name%]")
		);
	}
	
	@Test
	public void eachTargetContainsCorrectText() {
		assertEquals("Hello Mr. John", factory.getContentFor("John.txt"));
		assertEquals("Hello Ms. Jane", factory.getContentFor("Jane.txt"));
	}

}
