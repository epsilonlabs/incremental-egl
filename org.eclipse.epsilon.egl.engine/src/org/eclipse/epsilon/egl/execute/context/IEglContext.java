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
package org.eclipse.epsilon.egl.execute.context;

import java.util.List;

import org.eclipse.epsilon.egl.EglTemplate;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.config.ContentTypeRepository;
import org.eclipse.epsilon.egl.formatter.Formatter;
import org.eclipse.epsilon.egl.merge.partition.CompositePartitioner;
import org.eclipse.epsilon.egl.output.IOutputBuffer;
import org.eclipse.epsilon.egl.output.IOutputBufferFactory;
import org.eclipse.epsilon.egl.status.StatusMessage;
import org.eclipse.epsilon.egl.traceability.Template;
import org.eclipse.epsilon.eol.execute.context.IEolContext;

public interface IEglContext extends IEolContext {
		
	public List<String> getPartitioningProblems();	
	
	public EglTemplateFactory getTemplateFactory();
	
	public void copyInto(IEolContext context);

	public void copyInto(IEolContext context, boolean preserveFrameStack);
	
	public CompositePartitioner getPartitioner();
	
	public void setPartitioner(CompositePartitioner partitioner);
	
	public boolean usePartitionerFor(String contentType);
	
	public ContentTypeRepository getContentTypeRepository();
	
	public void setContentTypeRepository(ContentTypeRepository repository);
	
	public void addStatusMessage(StatusMessage message);
	
	public List<StatusMessage> getStatusMessages();

	public void enter(EglTemplate template);

	public void exit();

	public IOutputBuffer getOutputBuffer();
	
	public Template getTrace();
	
	public EglTemplate getCurrentTemplate();

	public void formatWith(Formatter formatter);

	public IOutputBufferFactory getOutputBufferFactory();
	
	public void setOutputBufferFactory(IOutputBufferFactory outputBufferFactory);
}
