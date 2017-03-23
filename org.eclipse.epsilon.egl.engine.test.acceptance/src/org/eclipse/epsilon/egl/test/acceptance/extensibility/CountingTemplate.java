/*******************************************************************************
 * Copyright (c) 2009 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************
 *
 * $Id$
 */
package org.eclipse.epsilon.egl.test.acceptance.extensibility;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.epsilon.egl.EglPersistentTemplate;
import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;
import org.eclipse.epsilon.egl.execute.context.IEglContext;
import org.eclipse.epsilon.egl.spec.EglTemplateSpecification;

public class CountingTemplate extends EglPersistentTemplate {

	private static final Map<File, Integer> COUNTERS = new HashMap<File, Integer>();
	
	public CountingTemplate(EglTemplateSpecification spec, IEglContext context, URI outputRoot, String outputRootPath) throws Exception {
		super(spec, context, outputRoot, outputRootPath);
	}

	@Override
	protected void doGenerate(File file, String targetName, boolean overwrite, boolean protectRegions) throws EglRuntimeException {
		ensureCounterFor(file);
		incrementCounterFor(file);
	}

	private static void ensureCounterFor(File file) {
		if (!COUNTERS.containsKey(file)) {
			COUNTERS.put(file, 0);
		}
	}

	private static void incrementCounterFor(File file) {
		COUNTERS.put(file, COUNTERS.get(file) + 1);
	}
	
	public static int countFor(File file) {
		return COUNTERS.containsKey(file) ? COUNTERS.get(file) : 0;
	}
	
	@Override
	public void reset() {
		super.reset();
		COUNTERS.clear();
	}
}
