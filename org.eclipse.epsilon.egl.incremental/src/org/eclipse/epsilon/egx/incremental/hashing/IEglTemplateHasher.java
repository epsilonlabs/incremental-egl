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
package org.eclipse.epsilon.egx.incremental.hashing;

import java.net.URI;

import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;

public interface IEglTemplateHasher {

	public EglTemplateHash hash(URI templateUri) throws EglRuntimeException;

}
