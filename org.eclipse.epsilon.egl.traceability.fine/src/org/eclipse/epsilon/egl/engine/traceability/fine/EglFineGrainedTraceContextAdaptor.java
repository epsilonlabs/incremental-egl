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
package org.eclipse.epsilon.egl.engine.traceability.fine;

import org.eclipse.epsilon.egl.engine.traceability.fine.internal.EglOutputBufferPrintExecutionListener;
import org.eclipse.epsilon.egl.engine.traceability.fine.internal.TracedPropertyAccessLedger;
import org.eclipse.epsilon.egl.engine.traceability.fine.internal.TraceLinkCreatingTemplateExecutionListener;
import org.eclipse.epsilon.egl.engine.traceability.fine.trace.Trace;
import org.eclipse.epsilon.egl.execute.context.IEglContext;
import org.eclipse.epsilon.eol.execute.introspection.recording.IPropertyAccessRecorder;
import org.eclipse.epsilon.eol.execute.introspection.recording.PropertyAccessExecutionListener;
import org.eclipse.epsilon.eol.execute.introspection.recording.PropertyAccessRecorder;

/**
 * The entry point to the EGL fine-grained traceability functionality. This adaptor can
 * be used to non-destructively enhance an EglModule with execution listeners that build
 * a trace model when the module is executed.
 * 
 * @see Trace
 */
public class EglFineGrainedTraceContextAdaptor {

	/**
	 * Prepares to build a trace model for the specified context, returning an empty
	 * trace. Subsequent execution of the module attached to this context will cause
	 * the trace to be populated. 
	 */
	public Trace adapt(IEglContext context) {
		final Trace trace = new Trace();
		final TracedPropertyAccessLedger ledger = new TracedPropertyAccessLedger();
		
		selectivelyRecordPropertyAccesses(context, ledger);
		listenForTemplateExecution(context, trace, ledger);
		
		return trace;
	}

	protected void listenForTemplateExecution(IEglContext context, Trace trace, TracedPropertyAccessLedger ledger) {
		context.getTemplateFactory().getTemplateExecutionListeners().add(new TraceLinkCreatingTemplateExecutionListener(trace, ledger));
	}

	protected void selectivelyRecordPropertyAccesses(IEglContext context, TracedPropertyAccessLedger ledger) {
		final IPropertyAccessRecorder recorder = new PropertyAccessRecorder();
		context.getExecutorFactory().addExecutionListener(new PropertyAccessExecutionListener(recorder));
		context.getExecutorFactory().addExecutionListener(new EglOutputBufferPrintExecutionListener(recorder, ledger));
	}
}
