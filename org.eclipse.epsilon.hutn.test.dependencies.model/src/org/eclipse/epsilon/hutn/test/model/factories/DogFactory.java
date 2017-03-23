/*******************************************************************************
 * Copyright (c) 2008 The University of York.
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
package org.eclipse.epsilon.hutn.test.model.factories;

import org.eclipse.epsilon.hutn.test.model.families.Dog;
import org.eclipse.epsilon.hutn.test.model.families.DogBreed;
import org.eclipse.epsilon.hutn.test.model.families.FamiliesFactory;

public abstract class DogFactory {

	private DogFactory() {}
	
	public static Dog createDog() {
		return FamiliesFactory.eINSTANCE.createDog();
	}
	
	public static Dog createDog(String name) {
		final Dog dog = createDog();
		dog.setName(name);
		return dog;
	}
	
	public static Dog createDog(DogBreed breed) {
		final Dog dog = createDog();
		dog.setBreed(breed);
		return dog;
	}
}
