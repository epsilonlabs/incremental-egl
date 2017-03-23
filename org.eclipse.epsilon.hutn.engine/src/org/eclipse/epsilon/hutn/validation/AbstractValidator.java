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
package org.eclipse.epsilon.hutn.validation;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.evl.EvlUnsatisfiedConstraint;
import org.eclipse.epsilon.evl.IEvlModule;
import org.eclipse.epsilon.hutn.exceptions.HutnValidationException;
import org.eclipse.epsilon.hutn.util.EpsilonUtil;

public abstract class AbstractValidator {

	private final AbstractFixer fixer;
	private final URL evlSource;
	
	protected AbstractValidator(AbstractFixer fixer, URL url) {
		this.fixer = fixer;
		this.evlSource = url;
	}
	
	protected List<ParseProblem> validate(IModel model, List<IModel> extraModels) throws HutnValidationException {
		return validate(model, extraModels.toArray(new IModel[]{}));
	}
	
	protected List<ParseProblem> validate(IModel model, IModel... extraModels) throws HutnValidationException {
		List<ParseProblem> problems;
		
		do {
			fixer.reset();
			problems = doValidate(model, extraModels);
		} while (fixer.hasChangedModel());
		
		return problems;
	}
	
	private List<ParseProblem> doValidate(IModel model, IModel... extraModels) throws HutnValidationException {
		try {		
			final IEvlModule validator = EpsilonUtil.initialseEvlModule(fixer, model, extraModels);
		
			validator.parse(evlSource.toURI());
			validator.execute();
			
			return collectParseProblems(validator);
			
		} catch (Exception e) {
			throw new HutnValidationException(e);
		}
	}

	private List<ParseProblem> collectParseProblems(final IEvlModule validator) {
		final List<ParseProblem> problems = new LinkedList<ParseProblem>();
		
		if (validator != null) {
			for (EvlUnsatisfiedConstraint constraint : validator.getContext().getUnsatisfiedConstraints()) {
				problems.add(fixer.interpretUnsatisfiedConstraint(constraint));
			}
		}
		
		return problems;
	}

}
