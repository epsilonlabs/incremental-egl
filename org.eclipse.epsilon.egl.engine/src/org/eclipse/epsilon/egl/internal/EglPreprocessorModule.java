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
package org.eclipse.epsilon.egl.internal;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.antlr.runtime.Token;
import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.common.parse.Region;
import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;
import org.eclipse.epsilon.egl.exceptions.EglStoppedException;
import org.eclipse.epsilon.egl.parse.problem.EglParseProblem;
import org.eclipse.epsilon.egl.preprocessor.Preprocessor;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.EolInternalException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.parse.EolParser;

public class EglPreprocessorModule extends EolModule {

	private final Preprocessor preprocessor = new Preprocessor();
	private EglPreprocessorContext context = new EglPreprocessorContext(super.context);
	
	@Override
	public EglPreprocessorContext getContext() {
		return context;
	}
	
	@Override
	public void reset() {
		super.reset();
		this.context = new EglPreprocessorContext(super.context);
	}
	
	public boolean preprocess(AST ast, File sourceFile, URI sourceUri) {
		this.sourceFile = sourceFile;
		this.sourceUri  = sourceUri;
		
		final String eol = preprocessor.convertToEol(ast);

		try {
			setOperationFactory(new EglOperationFactory());
			if (parse(eol, sourceFile)) {
				updateASTLocations(this.ast);
				return true;
			}

		} catch (Exception e) {
			// Ignore - clients are expected to call
			// getParseProblems in order to check for problems
		}

		return false;
	}
	
	protected void updateASTLocations(AST ast) {
		ast.setColumn(preprocessor.getTrace().getEglColumnNumberFor(ast.getLine(), ast.getColumn()));
		ast.setLine(preprocessor.getTrace().getEglLineNumberFor(ast.getLine()));
		
		for (Token token : ast.getExtraTokens()) {
			if (token == null) continue;
			token.setCharPositionInLine(preprocessor.getTrace().getEglColumnNumberFor(token.getLine(), token.getCharPositionInLine()));
			token.setLine(preprocessor.getTrace().getEglLineNumberFor(token.getLine()));
		}
		
		boolean done = updateRegionsOfStaticTextASTs(ast);
			
		if (!done) {
			for (AST child : ast.getChildren()) {
				updateASTLocations(child);
			}
		}
	}

	/**
	 * Updates the EOL ASTs produced by the preprocessor from EGL static sections.
	 * 
	 * In the generated EOL, static sections appear as statements of the form:
	 * out.prinx("the static text")
	 *           12345678901234567
	 *           0        1
	 * 
	 * In the EGL AST outline view, we display (a filtered version of) the preprocessed
	 * EOL's AST, which contains a STRING node. For the above statement, this would be:
	 * 
	 * STRING, 1:1 to 1:17
	 * 
	 * Note that the start (end) column includes the opening (closing) double quote. This 
	 * method corrects this issue by finding all AST nodes that correspond to text
	 * generated for static sections and adjusting the regions of the nested STRING nodes.
	 * 
	 * This method also "hides" (makes imaginary) any AST nodes that need not be displayed
	 * in the AST outline view.
	 * 
	 * @return true if and only iff this method has processed all nested AST nodes and
	 *         this part of the AST should not be processed any further.
	 */
	private boolean updateRegionsOfStaticTextASTs(AST ast) {
		// Turn out.prinx("something") / out.printdyn(x) to imaginary and fix the region of the parameter
		if (ast.getType() == EolParser.POINT && ast.getNumberOfChildren() == 2) {
			AST outAst = ast.getFirstChild();
			AST printAst = ast.getChild(1);
			
			if ("out".equals(outAst.getText()) && ("prinx".equals(printAst.getText()) || "printdyn".equals(printAst.getText()))) {
				AST parametersAst = printAst.getFirstChild();
				if (parametersAst != null) {
					AST firstParameterAst = parametersAst.getFirstChild();
					if (firstParameterAst != null) {
						ast.setImaginary(true);
						outAst.setImaginary(true);
						printAst.setImaginary(true);
						parametersAst.setImaginary(true);
						
						updateASTLocations(firstParameterAst);
						Region region = firstParameterAst.getRegion();
						
						Region adjustedRegion = null;
						
						// For out.prinx("something") we need to lose the double quotes
						if ("prinx".equals(printAst.getText()) && firstParameterAst.getType() == EolParser.STRING) {
							adjustedRegion = new Region(region.getStart().getLine(), region.getStart().getColumn()+1,
								region.getEnd().getLine(), region.getEnd().getColumn() - 1);
							firstParameterAst.setRegion(adjustedRegion);
						}
						else {
							adjustedRegion = region;
						}
						
						// Turn out.print("\n") and out.print("\r\n") to imaginary
						if ("\\n".equals(firstParameterAst.getText()) || "\\r\\n".equals(firstParameterAst.getText())) firstParameterAst.setImaginary(true);
						
						// Make all involved ASTs imaginary and assign them the region of the first parameter
						for (AST imaginary : Arrays.asList(ast, outAst, printAst, parametersAst)) {
							imaginary.setColumn(preprocessor.getTrace().getEglColumnNumberFor(imaginary.getLine(), imaginary.getColumn()));
							imaginary.setLine(preprocessor.getTrace().getEglLineNumberFor(imaginary.getLine()));
							imaginary.setImaginary(true);
							imaginary.setRegion(adjustedRegion);
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public Object execute() throws EglRuntimeException {
		try {
			return super.execute();

		} catch (EolInternalException ex) {
			if (ex.getInternal() instanceof EglStoppedException) {
				// Ignore exception caused by a call to out.stop()
				return null;
				
			} else if (ex.getInternal() instanceof EglRuntimeException) {
				throw new EglRuntimeException(ex);
			} else {
				throw new EglRuntimeException("Error encountered whilst processing template.", ex);
			}
		} catch (EolRuntimeException ex) {
			if (ex instanceof EglRuntimeException) {
				throw (EglRuntimeException)ex;
			} else {
				throw new EglRuntimeException(ex);
			}
		}
	}
	
	@Override
	public List<ParseProblem> getParseProblems() {
		final List<ParseProblem> parseProblems = super.getParseProblems();
		
		for (int index = 0; index < parseProblems.size(); index++) {
			final ParseProblem problem = parseProblems.get(index);
			
			if (!(problem instanceof EglParseProblem)) {
				parseProblems.remove(index);
				parseProblems.add(index, new EglParseProblem(problem, preprocessor.getTrace()));
			}
		}
		
		return parseProblems;
	}
	
	private static final HashMap<String, Class<?>> importConfiguration = new HashMap<String, Class<?>>();
	static {
		importConfiguration.put("eol", EolModule.class);
		importConfiguration.put("egl", EglModule.class);
	}
	
	/**
	 * Get the import configurations for this translation. Notice that when overriding this method, 
	 * it is illegal to call super and change the map received. To avoid this kind of problem, the
	 * method is declared final.
	 */
	@Override
	public final HashMap<String, Class<?>> getImportConfiguration() {
		return importConfiguration;
	}
}
