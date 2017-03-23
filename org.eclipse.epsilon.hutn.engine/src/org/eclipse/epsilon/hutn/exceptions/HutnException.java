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
package org.eclipse.epsilon.hutn.exceptions;

public abstract class HutnException extends Exception {
	
	// Generated by Eclipse
	private static final long serialVersionUID = 4156083316857541986L;

	public HutnException(String message) {
		super(message);
	}
	
	public HutnException(Throwable cause) {
		super(cause);
	}
	
	public HutnException(String message, Throwable cause) {
		super(message, cause);
	}
}