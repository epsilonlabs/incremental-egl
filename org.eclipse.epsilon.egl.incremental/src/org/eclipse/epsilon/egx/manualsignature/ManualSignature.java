package org.eclipse.epsilon.egx.manualsignature;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

public class ManualSignature {

	
	public String asSignatureFromPrimitive(EObject obj, EStructuralFeature attr)
	{	
		if(attr != null) {
			if(attr.eClass().getName().equals("EEnumLiteral"))
			{
				return ((EEnumLiteral)attr).getLiteral();
			}
			else
			{
				return obj.eGet(attr) + "";
			}
		}
		
		return "";
		
	}
	
	public ArrayList<String> asSignatureCall(EObject obj, ArrayList<Object> visitedRefs, int depth)
	{
		ArrayList<String> signature = new ArrayList<String>();
		
		if (obj == null || depth > 1) {
			return null;
		}
		
		depth = depth + 1;
		visitedRefs.add(obj);
	
		if (obj.eClass().getName() == "EGenericType")
			return new ArrayList<String>();
		
		for(EAttribute attr : obj.eClass().getEAllAttributes()) {
			if (! attr.isDerived() ) {
				if (attr.isMany()) {
					ArrayList<String> signaturesOfAllValues = new ArrayList<String>();
					for (EObject val : attr.eContents()){
						System.out.println("contents " + attr.eContents());
						signaturesOfAllValues.add(this.asSignatureFromPrimitive(attr,(EAttribute)val));
					}
					signature.addAll(signaturesOfAllValues);
				}
				else {
					signature.add(this.asSignatureFromPrimitive(obj, attr));
				}
			}
		}
		
		for (EReference r : obj.eClass().getEAllReferences()) {
			if (! r.isDerived()) {
				if(r.isMany() ) {
					ArrayList<EObject> refs = new ArrayList<EObject>();
			    	refs.addAll((Collection<? extends EObject>) obj.eGet(r));
			    	for(EObject ref : refs)
			    	{
			    		if (! visitedRefs.contains(ref) && ref != null) {// && !(ref instanceof Collection<?>)) {
			    			ArrayList<String> refSigHold = new ArrayList<String>();
				 			refSigHold = this.asSignatureCall(ref, visitedRefs, depth);
				 			signature.addAll(refSigHold == null ? new ArrayList<String>() : refSigHold);
				 			}
			    	}
		    	}
			 	else {
			 		EObject ref = (EObject) obj.eGet(r);
			 		if (! visitedRefs.contains(ref) && ref != null) {
			 			ArrayList<String> refSigHold = new ArrayList<String>();
			 			refSigHold = this.asSignatureCall(ref, visitedRefs, depth);
			 			signature.addAll(refSigHold == null ? new ArrayList<String>() : refSigHold);
			 			}
			 		}
				}
			}
		/*
		if(obj.eClass().getEAllSuperTypes().size() > 0) {
			ArrayList<String>names = new ArrayList<String>();
			for (EObject o : obj.eClass().getEAllSuperTypes()) {
				names.add(o.eClass().getName());
			}
			signature.addAll(names);
		}
		*/
		return signature;
		
	}
	
	public ArrayList<String> asSignature(EObject o){
		return this.asSignatureCall(o, new ArrayList<Object>(), 0);
	}
	
	public static void main(String[] args) {
		EObject obj = EcoreFactory.eINSTANCE.createEObject();
		EEnum eenum = EcoreFactory.eINSTANCE.createEEnum();
		ManualSignature ms = new ManualSignature();
//		System.out.println(ms.asSignatureFromPrimitive(obj));
//		System.out.println(ms.asSignatureFromPrimitive(eenum));
	}

}
