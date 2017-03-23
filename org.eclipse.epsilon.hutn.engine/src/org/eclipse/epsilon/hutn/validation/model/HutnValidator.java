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
package org.eclipse.epsilon.hutn.validation.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.emc.emf.EmfMetaModel;
import org.eclipse.epsilon.emc.emf.EmfUtil;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.hutn.exceptions.HutnUnrecognisedNsUriException;
import org.eclipse.epsilon.hutn.exceptions.HutnValidationException;
import org.eclipse.epsilon.hutn.model.hutn.HutnPackage;
import org.eclipse.epsilon.hutn.model.hutn.NsUri;
import org.eclipse.epsilon.hutn.model.hutn.Spec;
import org.eclipse.epsilon.hutn.validation.AbstractValidator;

public class HutnValidator extends AbstractValidator {

	public HutnValidator() {
		super(new HutnFixer(), HutnValidator.class.getResource("ValidateIntermediateModel.evl"));
	}
	
	private static Resource resourceFor(Spec hutn) {
		return hutn.eResource() == null ? EmfUtil.createResource(hutn) : hutn.eResource();
	}
	
	public List<ParseProblem> getProblemsForIntermediateModel(Spec hutn) throws HutnValidationException {
		final IModel model = new InMemoryEmfModel("Intermediate", resourceFor(hutn), HutnPackage.eINSTANCE);
		final List<IModel> metamodels = new ArrayList<IModel>();
		
		for (NsUri uri : hutn.getNsUris()) {
			try {
				final IModel metamodel = new EmfMetaModel("Metamodel", uri.getValue());
				metamodel.load();
				
				metamodels.add(metamodel);
			} catch (EolModelLoadingException e) {
				throw new HutnUnrecognisedNsUriException(uri.getValue(), uri.getLine(), uri.getCol());
			}
		}
		
		return validate(model, metamodels);
	}
}
