/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.eol.execute;

import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;


public class RealExecutor extends AbstractExecutor{

	@Override
	public Object execute(AST ast, IEolContext context) throws EolRuntimeException{
		String text = "";
		boolean doublePrecision = false;
		if (ast.getText().endsWith("f")) {
			text = ast.getText().substring(0, ast.getText().length() - 1);
			doublePrecision = false;
		}
		else if (ast.getText().endsWith("d")) {
			text = ast.getText().substring(0, ast.getText().length() - 1);
			doublePrecision = true;		
		}
		else {
			text = ast.getText();
			doublePrecision = false;			
		}
		
		if (doublePrecision) {
			return new Double(text);
		}
		else {
			return new Float(text);
		}
		
	}

}
