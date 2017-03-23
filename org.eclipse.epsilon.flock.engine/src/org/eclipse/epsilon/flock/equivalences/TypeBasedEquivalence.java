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
package org.eclipse.epsilon.flock.equivalences;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.flock.FlockExecution;
import org.eclipse.epsilon.flock.context.ConservativeCopyContext;
import org.eclipse.epsilon.flock.emc.wrappers.ModelElement;
import org.eclipse.epsilon.flock.emc.wrappers.ModelValue;
import org.eclipse.epsilon.flock.execution.EolExecutor;
import org.eclipse.epsilon.flock.execution.exceptions.ConservativeCopyException;
import org.eclipse.epsilon.flock.execution.exceptions.FlockRuntimeException;
import org.eclipse.epsilon.flock.model.domain.rules.IgnoredProperties;

public class TypeBasedEquivalence extends Equivalence {
	
	protected final ModelElement original;
	protected final ModelElement equivalent;
	
	public TypeBasedEquivalence(EolExecutor executor, FlockExecution execution, ModelElement original, ModelElement equivalent) {
		super(executor, execution);
		
		this.original   = original;
		this.equivalent = equivalent;
	}
	
	public ModelElement getOriginal() {
		return original;
	}

	public ModelElement getEquivalent() {
		return equivalent;
	}
	
	@Override
	public void ruleApplied(FlockExecution execution) {
		// Do nothing
	}
	
	public void automaticallyPopulateEquivalent(ConservativeCopyContext context, IgnoredProperties ignoredProperties) throws FlockRuntimeException {
		equivalent.copyIdentityFrom(original);
		conservativeCopy(context, ignoredProperties);
	}

	private void conservativeCopy(ConservativeCopyContext context, IgnoredProperties ignoredProperties) throws ConservativeCopyException {
		new ConservativeCopy(context, ignoredProperties).copyProperties(original, equivalent);
	}
	
	public static class ConservativeCopy {
	
		private final ConservativeCopyContext context;
		private final IgnoredProperties ignoredProperties;
		
		public ConservativeCopy(ConservativeCopyContext context) {
			this(context, new IgnoredProperties());
		}
		
		public ConservativeCopy(ConservativeCopyContext context, IgnoredProperties ignoredProperties) {
			this.context           = context;
			this.ignoredProperties = ignoredProperties;
		}
		
		public void copyProperties(ModelElement original, ModelElement equivalent) throws ConservativeCopyException {
			try {			
				for (String propertyName : original.getPropertiesSharedWith(equivalent)) {
					if (ignoredProperties.isNotIgnored(propertyName)) {
						copyProperty(propertyName, original, equivalent);
					}
				}
				
			} catch (EolModelElementTypeNotFoundException e) {
				throw new ConservativeCopyException("Exception encountered while determining properties shared between " + original + " and " + this, e);
			}
		}
	
		void copyProperty(String propertyName, ModelElement original, ModelElement equivalent) throws ConservativeCopyException {
			try {
				final ModelValue<?> equivalentValue = context.getEquivalentValue(original.getValueOfProperty(propertyName));
				
				equivalent.conservativelySetValueForProperty(equivalentValue, propertyName, context);
				
			} catch (EolRuntimeException e) {
				throw new ConservativeCopyException("Exception encountered while copying '" + propertyName + "' from " + original + " to " + this, e);
			}
		}
	}
	
	@Override
	public String toString() {
		return original + " <-> " + equivalent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TypeBasedEquivalence))
			return false;
		
		final TypeBasedEquivalence other = (TypeBasedEquivalence)obj;
		
		return original.equals(other.original) &&
		       equivalent.equals(other.equivalent);
	}
	
	@Override
	public int hashCode() {
		return original.hashCode() + equivalent.hashCode();
	}
}
