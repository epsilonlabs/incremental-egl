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

public class EglTemplateHash {

	private final String value;
	private final String text;
	
	public EglTemplateHash(String value, String text) {
		this.value = value;
		this.text = text;
	}
	
	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EglTemplateHash))
			return false;
		
		final EglTemplateHash other = (EglTemplateHash)object;
		
		return value.equals(other.value) &&
		       text.equals(other.text);
	}
	
	@Override
	public int hashCode() {
		return value.hashCode() + text.hashCode();
	}

}
