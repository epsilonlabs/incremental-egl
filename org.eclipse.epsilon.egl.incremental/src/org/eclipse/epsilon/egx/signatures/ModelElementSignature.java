package org.eclipse.epsilon.egx.signatures;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.egx.signaturepropertyaccess.SignaturePropertyAccess;


public class ModelElementSignature extends Signature {
	
	private List<SignaturePropertyAccess> signatureValue;
	private List<String> signatureValueList = new ArrayList<String>();
	
	public ModelElementSignature(String i, String r){
		super();
		rule = r;
		signatureValueList = new ArrayList<String>();
		signatureValue = new ArrayList<SignaturePropertyAccess>();
		objectId = i;
	}
	

	public ModelElementSignature(String object_id, List<SignaturePropertyAccess> store) {
		super();
		objectId = object_id;
		signatureValue = store;
	}
	
	public List<SignaturePropertyAccess> getSignatureValueAsPropertyAccesses() {
		return signatureValue;
	}
	
	public List<String> getObjSignatureValues(){
		return signatureValueList;
	}
	
	public void setSignatureValue(List<String> s){
		signatureValueList = s;
	}
	
	

}