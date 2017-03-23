
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
package org.eclipse.epsilon.workflow.tasks;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.egl.engine.traceability.fine.EglFineGrainedTraceContextAdaptor;
//import org.eclipse.epsilon.egl.engine.traceability.fine.EglFineGrainedTraceContextAdaptor;
import org.eclipse.epsilon.egl.engine.traceability.fine.trace.ModelLocation;
import org.eclipse.epsilon.egl.engine.traceability.fine.trace.Region;
import org.eclipse.epsilon.egl.engine.traceability.fine.trace.TextLocation;
import org.eclipse.epsilon.egl.engine.traceability.fine.trace.Trace;
import org.eclipse.epsilon.egl.engine.traceability.fine.trace.TraceLink;
import org.eclipse.epsilon.egl.execute.context.IEglContext;
//import org.eclipse.epsilon.egl.execute.context.IEglContext;
import org.eclipse.epsilon.egl.formatter.Formatter;
import org.eclipse.epsilon.egx.incremental.EgxModuleInc;
import org.eclipse.epsilon.eol.IEolExecutableModule;
import org.eclipse.epsilon.workflow.tasks.nestedelements.EglDefaultFormatterNestedElement;

public class EglTask extends ExportableModuleTask {
	
	protected File target;
	protected Class<? extends EglTemplateFactory> templateFactoryType = EglFileGeneratingTemplateFactory.class;
	protected List<EglDefaultFormatterNestedElement> defaultFormatterNestedElements = new LinkedList<EglDefaultFormatterNestedElement>();

	protected boolean incremental;
	protected String incrementalId;
	
	protected String file;

	protected Trace trace;

	
	@Override
	protected IEolExecutableModule createModule() throws InstantiationException, IllegalAccessException {
		final IEolExecutableModule module; 
		final EglTemplateFactory templateFactory = templateFactoryType.newInstance();
		
		templateFactory.setDefaultFormatters(instantiateDefaultFormatters());
		
		if (src.getName().endsWith("egx")) {
			return createEgxModule(templateFactory);

			//module = new EgxModule(templateFactory);
		}
		else {		
			module = new EglTemplateFactoryModuleAdapter(templateFactory);
		}
		
		// Turn on fine-grained traceability, and
		// obtain a reference to the trace so that we can export it later
		
		if (shouldExportAsModel()) {
			trace = new EglFineGrainedTraceContextAdaptor().adapt((IEglContext) module.getContext());
		}
		
		return module;
	}

	private EgxModule createEgxModule(final EglTemplateFactory templateFactory) {
		if (isIncremental()) {
			EgxModuleInc egxModule = new EgxModuleInc(templateFactory);
			egxModule.setLaunchConfigName(getIncrementalId());
			//templateFactory.getDefaultIncrementalitySettings().setOverwriteUnchangedFiles(true);
			return egxModule;
		} else {
			return new EgxModule(templateFactory);			
		}
	}

	private List<Formatter> instantiateDefaultFormatters() throws InstantiationException, IllegalAccessException {
		final List<Formatter> defaultFormatters = new LinkedList<Formatter>();
		
		for (EglDefaultFormatterNestedElement defaultFormatterNestedElement : defaultFormatterNestedElements) {
			defaultFormatters.add(defaultFormatterNestedElement.getImplementation().newInstance());
		}
		
		return defaultFormatters;
	}

	@Override
	protected void examine() throws Exception {
		super.examine();
		
		if (target!=null) {
			FileWriter fw = new FileWriter(target);
			fw.write(String.valueOf(result));
			fw.flush();
			fw.close();
		}
	}

	@Override
	protected void initialize() throws Exception {}

	public File getTarget() {
		return target;
	}

	public void setTarget(File output) {
		this.target = output;
	}
	
	public boolean isIncremental() {
		return incremental;
	}

	public void setIncremental(boolean incremental) {
		this.incremental = incremental;
	}

	public String getIncrementalId() {
		return incrementalId;
	}

	public void setIncrementalId(String incrementalId) {
		this.incrementalId = incrementalId;
	}
	
	public void setFile(String inputFile) {
		file = inputFile;
	}
	
	public String getFile() {
		return file;
	}
	
	public Class<? extends EglTemplateFactory> getTemplateFactoryType() {
		return templateFactoryType;
	}
	
	public void setTemplateFactoryType(Class<? extends EglTemplateFactory> templateFactoryType) {
		if (EglTemplateFactory.class.isAssignableFrom(templateFactoryType)) {
			this.templateFactoryType = templateFactoryType;
		
		} else {
			throw new BuildException("The templateFactoryType parameter must be class that subtypes org.eclipse.epsilon.egl.EglTemplateFactory.");
		}
	}
	
	public EglDefaultFormatterNestedElement createDefaultFormatter() {
		final EglDefaultFormatterNestedElement nestedElement = new EglDefaultFormatterNestedElement();
		defaultFormatterNestedElements.add(nestedElement);
		return nestedElement;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Collection<? extends Class<?>> getClassesForExportedModel() {
		return Arrays.asList(Trace.class, TraceLink.class, TextLocation.class, ModelLocation.class, Region.class);
	}
	
	@Override
	protected Collection<? extends Object> getObjectsForExportedModel() {
		return trace.getAllContents();
	}
}