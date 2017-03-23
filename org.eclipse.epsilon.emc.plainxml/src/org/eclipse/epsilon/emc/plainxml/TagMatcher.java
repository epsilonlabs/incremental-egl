/*******************************************************************************
 * Copyright (c) 2012 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.emc.plainxml;

import org.w3c.dom.Element;

public class TagMatcher {
	
	public static boolean matches(Element e, String pattern) {
		if (e == null) return false;
		if (pattern.equals("*")) return true;
		return e.getTagName().equals(pattern);
	}
	
}
