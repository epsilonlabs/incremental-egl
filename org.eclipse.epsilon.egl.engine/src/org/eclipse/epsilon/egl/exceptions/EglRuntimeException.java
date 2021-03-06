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
package org.eclipse.epsilon.egl.exceptions;

import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.eol.exceptions.EolInternalException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;


public class EglRuntimeException extends EolRuntimeException {
	
	// Generated by Eclipse
	private static final long serialVersionUID = 2370066735399525557L;
	
	private final Throwable cause;
	private final int line;
	private final int column;
	
	
	public EglRuntimeException (EolRuntimeException ex){
		super(ex.getReason(), ex.getAst());
		
		reason = ex.getReason();
		cause  = ex;
		line   = ex.getAst().getLine();
		column = ex.getColumn();
		ast    = ex.getAst();
	}
	
	public EglRuntimeException (EolInternalException ex){
		super(ex.getReason(), ex.getAst());
		
		final EglRuntimeException internal = (EglRuntimeException)ex.getInternal();
		
		reason = internal.getReason();
		cause  = internal.getCause();
		line   = ex.getAst().getLine();
		column = ex.getColumn();
		ast    = ex.getAst();
	}
	
	public EglRuntimeException(String reason, AST ast) {
		this(reason, null, ast);
	}
	
	public EglRuntimeException(String reason, Throwable cause) {
		this(reason, cause, 1, 1, null);
	}
	
	public EglRuntimeException(String reason, Throwable cause, AST ast) {
		this(reason, cause, ast.getLine(), ast.getColumn(), ast);
	}
	
	private EglRuntimeException(String reason, Throwable cause, int line, int column, AST ast) {
		this.reason = reason;		
		this.cause  = cause;
		this.line   = line;
		this.column = column;
		this.ast    = ast;
	}


	@Override
	public Throwable getCause() {
		return cause;
	}
	
	@Override
	public int getLine(){
		return line;
	}
	
	@Override
	public int getColumn(){
		return column;
	}
	
	@Override
	public String getReason() {
		return reason;
	}
	
	@Override
	public String toString() {
		String result = super.toString();
		
		if (cause!=null) {
			result += "\n\tCause: " + cause.toString();
		}
		
		return result;
	}
}
