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
package org.eclipse.epsilon.workflow.tasks.xml;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.eclipse.epsilon.emc.plainxml.PlainXmlModel;
import org.eclipse.epsilon.workflow.tasks.EpsilonTask;
import org.eclipse.epsilon.workflow.tasks.ShutdownProjectRepositoryListener;

public class LoadXmlModel extends EpsilonTask {
	
	protected String name;
	protected String alias;
	protected File file;
	protected String uri;
	protected boolean read = true;
	protected boolean store = false;
	
	@Override
	public void executeImpl() throws BuildException {
		ShutdownProjectRepositoryListener.activate(getProject(), getProjectRepository());
		
		PlainXmlModel model = new PlainXmlModel();
		
		model.setName(name);
		model.getAliases().add(alias);
		model.setReadOnLoad(read);
		model.setStoredOnDisposal(store);
		if (file != null) model.setFile(file);
		if (uri != null) model.setUri(uri);
		
		try {
			model.load();
		}
		catch (Exception ex) {
			throw new BuildException();
		}
		
		getProjectRepository().addModel(model);
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isStore() {
		return store;
	}

	public void setStore(boolean store) {
		this.store = store;
	}
	
}
