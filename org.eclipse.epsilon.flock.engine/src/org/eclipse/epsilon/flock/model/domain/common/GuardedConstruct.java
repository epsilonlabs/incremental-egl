/*******************************************************************************
 * Copyright (c) 2013 The University of York.
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

import java.util.Collection;

import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.flock.execution.GuardedConstructContext;
import org.eclipse.epsilon.flock.execution.exceptions.FlockRuntimeException;

public abstract class GuardedConstruct extends FlockConstruct {

	private final Guard guard;
	
	public GuardedConstruct(AST ast, Collection<String> annotations, AST guard) {
		super(ast, annotations);
		this.guard = new Guard(guard);
	}
	
	protected Guard getGuard() {
		return guard;
	}
	
	public boolean appliesIn(GuardedConstructContext context) throws FlockRuntimeException {
		return context.satisfies(guard);
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof GuardedConstruct))
			return false;
		
		final GuardedConstruct other = (GuardedConstruct)object;
		
		return super.equals(other) &&
		       guard.equals(other.guard);
	}
	
	@Override
	public int hashCode() {
		return guard.hashCode();
	}
}
