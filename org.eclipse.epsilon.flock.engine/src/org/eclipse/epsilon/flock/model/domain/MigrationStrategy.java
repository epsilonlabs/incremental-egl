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
package org.eclipse.epsilon.flock.model.domain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.epsilon.common.module.ModuleElement;
import org.eclipse.epsilon.flock.context.MigrationStrategyCheckingContext;
import org.eclipse.epsilon.flock.equivalences.Equivalence;
import org.eclipse.epsilon.flock.execution.MigrateRuleContext;
import org.eclipse.epsilon.flock.execution.TypeMappingContext;
import org.eclipse.epsilon.flock.execution.exceptions.FlockRuntimeException;
import org.eclipse.epsilon.flock.model.domain.common.ClassifierTypedConstruct;
import org.eclipse.epsilon.flock.model.domain.rules.IgnoredProperties;
import org.eclipse.epsilon.flock.model.domain.rules.MigrateRule;
import org.eclipse.epsilon.flock.model.domain.rules.MigrateRules;
import org.eclipse.epsilon.flock.model.domain.typemappings.TypeMappingConstruct;
import org.eclipse.epsilon.flock.model.domain.typemappings.TypeMappingConstructs;

public class MigrationStrategy {
	
	private final List<ModuleElement> children = new LinkedList<ModuleElement>();
	private final TypeMappingConstructs typeMappingConstructs = new TypeMappingConstructs();
	private final MigrateRules          migrateRules          = new MigrateRules();
	
	
	public MigrationStrategy(ClassifierTypedConstruct... constructs) {
		for (ClassifierTypedConstruct construct : constructs) {
			if (construct instanceof TypeMappingConstruct) {
				addTypeMappingConstruct((TypeMappingConstruct)construct);
				
			} else if (construct instanceof MigrateRule) {
				addRule((MigrateRule)construct);
			
			} else {
				throw new IllegalArgumentException("constructs contains an unrecognsied construct: " + construct);
			}
		}
	}
	
	public Collection<ModuleElement> getTypeMappingsAndRules() {
		return children;
	}
	
	public void addTypeMappingConstruct(TypeMappingConstruct typeMappingConstruct) {
		typeMappingConstructs.add(typeMappingConstruct);
		children.add(typeMappingConstruct);
	}
	
	public Equivalence createEquivalence(TypeMappingContext context) throws FlockRuntimeException {
		return typeMappingConstructs.createEquivalence(context);
	}
	
	
	public void addRule(MigrateRule rule) {
		migrateRules.add(rule);
		children.add(rule);
	}
	
	public void checkTypeMappingsAndRules(MigrationStrategyCheckingContext context) {
		typeMappingConstructs.check(context);
		migrateRules.check(context);
	}
	
	public IgnoredProperties ignoredPropertiesFor(MigrateRuleContext context) throws FlockRuntimeException {
		return migrateRules.ignoredPropertiesFor(context);
	}
	
	public void applyRulesTo(MigrateRuleContext context) throws FlockRuntimeException {
		migrateRules.applyTo(context);
	}
}
