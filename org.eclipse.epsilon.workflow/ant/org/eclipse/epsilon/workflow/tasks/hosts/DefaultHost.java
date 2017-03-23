/*******************************************************************************
 * Copyright (c) 2012 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.workflow.tasks.hosts;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.eclipse.epsilon.eol.IEolExecutableModule;
import org.eclipse.epsilon.eol.models.IModel;

public class DefaultHost implements Host{

	@Override
	public boolean isRunning() {
		return true;
	}

	@Override
	public void addNativeTypeDelegates(IEolExecutableModule module) {
		
	}

	@Override
	public void addStopCapabilities(Project project, IEolExecutableModule module) {
		
	}

	@Override
	public void initialise() {
		
	}

	@Override
	public Object debug(IEolExecutableModule module, File file)
			throws Exception {
		return null;
	}

	@Override
	public boolean supportsDebugging() {
		return false;
	}

	@Override
	public void configureUserInput(IEolExecutableModule module, boolean isGui) {
		
	}

	@Override
	public IModel createModel(String type) throws BuildException {
		return null;
	}

}
