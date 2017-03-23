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
import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.epsilon.common.dt.console.EolRuntimeExceptionHyperlinkListener;
import org.eclipse.epsilon.common.dt.launching.EclipseExecutionController;
import org.eclipse.epsilon.common.dt.launching.extensions.ModelTypeExtension;
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.dt.launching.EclDebugger;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.egl.dt.debug.EgxDebugger;
import org.eclipse.epsilon.eml.EmlModule;
import org.eclipse.epsilon.eml.dt.launching.EmlDebugger;
import org.eclipse.epsilon.eol.IEolExecutableModule;
import org.eclipse.epsilon.eol.dt.ExtensionPointToolNativeTypeDelegate;
import org.eclipse.epsilon.eol.dt.debug.EolDebugTarget;
import org.eclipse.epsilon.eol.dt.debug.EolDebugger;
import org.eclipse.epsilon.eol.dt.userinput.JFaceUserInput;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.epsilon.etl.dt.launching.EtlDebugger;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dt.launching.EvlDebugger;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;

public class EclipseHost implements Host{

	@Override
	public boolean isRunning() {
		try {
			Class.forName("org.eclipse.epsilon.eol.dt.ExtensionPointToolNativeTypeDelegate");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	public void addNativeTypeDelegates(IEolExecutableModule module) {
		module.getContext().getNativeTypeDelegates().add(new ExtensionPointToolNativeTypeDelegate());
	}

	@Override
	public void addStopCapabilities(Project project, IEolExecutableModule module) {
		// Allow the user to stop any E*L task through the stop button in the console
		IProgressMonitor monitor =
			(IProgressMonitor) project.getReferences().get(AntCorePlugin.ECLIPSE_PROGRESS_MONITOR);
		if (monitor != null) {
			module.getContext().getExecutorFactory().setExecutionController(new EclipseExecutionController(monitor));
		}
	}

	@Override
	public void initialise() {
		if (ConsolePlugin.getDefault() != null) {
			for (IConsole c : ConsolePlugin.getDefault().getConsoleManager().getConsoles()) {
				if (c instanceof IOConsole) {
					IOConsole ioConsole = ((org.eclipse.ui.console.IOConsole) c);
					ioConsole.addPatternMatchListener(new EolRuntimeExceptionHyperlinkListener(ioConsole));
				}
			}
		}
	}
	
	@Override
	public Object debug(IEolExecutableModule module, File file) throws Exception {
		final EolDebugger debugger = (EolDebugger) createDebugger(module);

		// HACK: we assume the only running launch is the Ant launch. There's no clear way to
		// tell apart an Ant launch from a regular Run launch, apart from using internal classes
		// in the Eclipse Ant internal API.
		final ILaunch currentLaunch = DebugPlugin.getDefault().getLaunchManager().getLaunches()[0];
		// HACK: we need to remove the Ant source locator so Eclipse can find the source file
		currentLaunch.setSourceLocator(null);

		final EolDebugTarget target = new EolDebugTarget(
			currentLaunch, module, debugger, file.getAbsolutePath());
		debugger.setTarget(target);
		currentLaunch.addDebugTarget(target);
		return target.debug();
	}
	
	private Object createDebugger(IEolExecutableModule module) {
		if (module instanceof EclModule) {
			return new EclDebugger();
		} else if (module instanceof EmlModule) {
			return new EmlDebugger();
		} else if (module instanceof EtlModule) {
			return new EtlDebugger();
		} else if (module instanceof EvlModule) {
			return new EvlDebugger();
		} else if (module instanceof EgxModule) {
			return new EgxDebugger();
		}
		else return new EolDebugger();
	}

	@Override
	public boolean supportsDebugging() {
		return true;
	}

	@Override
	public void configureUserInput(IEolExecutableModule module, boolean isGui) {
		if (isGui) {
			module.getContext().setUserInput(new JFaceUserInput(module.getContext().getPrettyPrinterManager()));
		}
	}
	
	@Override
	public IModel createModel(String type) throws BuildException {
		try {
			IModel model = ModelTypeExtension.forType(type).createModel();
			return model;
		} catch (CoreException e) {
			throw new BuildException(e);
		}
	}
	
}
