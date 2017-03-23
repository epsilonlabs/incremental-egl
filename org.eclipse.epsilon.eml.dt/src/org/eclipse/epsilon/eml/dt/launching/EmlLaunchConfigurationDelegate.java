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
package org.eclipse.epsilon.eml.dt.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.dt.launching.EclDebugger;
import org.eclipse.epsilon.ecl.dt.launching.EclLaunchConfigurationDelegate;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.eml.EmlModule;
import org.eclipse.epsilon.eol.IEolExecutableModule;
import org.eclipse.epsilon.eol.dt.launching.EolLaunchConfigurationAttributes;
import org.eclipse.epsilon.eol.dt.launching.EpsilonLaunchConfigurationDelegate;
import org.eclipse.epsilon.eol.models.ModelRepository;

public class EmlLaunchConfigurationDelegate extends EpsilonLaunchConfigurationDelegate {

	MatchTrace matchTrace = null;
	ModelRepository modelRepository = null;
	
	@Override
	public IEolExecutableModule createModule() {
		return null;
	}
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor progressMonitor)
			throws CoreException {
		
		EclLaunchConfigurationDelegate eclLaunchConfigurationDelegate = new EclLaunchConfigurationDelegate() {
			@Override
			protected void postExecute(IEolExecutableModule module) {
				if (module instanceof EclModule) {
					matchTrace = ((EclModule) module).getContext().getMatchTrace().getReduced();
					modelRepository = ((EclModule) module).getContext().getModelRepository();
				}
			}
		};
		
		if (!eclLaunchConfigurationDelegate.launch(configuration, mode, launch, 
				progressMonitor, new EclModule(), new EclDebugger(),  EmlLaunchConfigurationAttributes.ECL_SOURCE, true, false))
			return;
	
		super.launch(configuration, mode, launch, 
				progressMonitor, new EmlModule(), new EmlDebugger(), EolLaunchConfigurationAttributes.SOURCE, false, true);
		
	}
	
	@Override
	protected void preExecute(IEolExecutableModule module) {
		if (module instanceof EmlModule) {
			((EmlModule) module).getContext().setMatchTrace(matchTrace);
			((EmlModule) module).getContext().setModelRepository(modelRepository);
		}
	}
	
}
