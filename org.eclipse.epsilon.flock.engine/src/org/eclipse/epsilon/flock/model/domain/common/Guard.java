/*******************************************************************************
 * Copyright (c) 2009 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************
 *
 * $Id$
 */
package org.eclipse.epsilon.flock.model.domain.common;

import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.flock.execution.EolExecutor;
import org.eclipse.epsilon.flock.execution.exceptions.FlockRuntimeException;

public class Guard {
	
	private final AST blockOrExpession;
	
	public Guard(AST blockOrExpession) {
		this.blockOrExpession = blockOrExpession;
	}

	public boolean isSatisifedBy(EolExecutor executor, Variable variable) throws FlockRuntimeException {
		if (blockOrExpession == null)
			return true;
		
		return executor.executeGuard(blockOrExpession, variable);
	}
	
	@Override
	public String toString() {
		return "Guard: " + blockOrExpession;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Guard))
			return false;
		
		final Guard other = (Guard)object;
		
		return blockOrExpession == null ?
		       other.blockOrExpession == null : 
		       blockOrExpession.equals(other.blockOrExpession);
	}
	
	@Override
	public int hashCode() {
		return blockOrExpession == null ? 0 : blockOrExpession.hashCode();
	}
}
