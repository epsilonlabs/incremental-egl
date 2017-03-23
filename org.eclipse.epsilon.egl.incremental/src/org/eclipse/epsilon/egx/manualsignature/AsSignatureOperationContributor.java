package org.eclipse.epsilon.egx.manualsignature;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.eol.execute.operations.contributors.OperationContributor;

public class AsSignatureOperationContributor extends OperationContributor {

	@Override
	public boolean contributesTo(Object target) {
		return target instanceof EObject;
	}

	public ArrayList<String> asSignature() {
		return new ManualSignature().asSignature((EObject)target);
	}
}