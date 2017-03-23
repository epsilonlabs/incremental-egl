package org.eclipse.epsilon.egx.signaturepropertyaccess;

public class SignatureSpa {

	private String spa_id;
	private String object_id;
	private String rule;
	
	public SignatureSpa(String spaId, String obj_id, String r) {
		spa_id = spaId;
		object_id = obj_id;
		rule = r;
	}

	public String getSpaId() {
		return spa_id;
	}
	
	public String getObjectId() {
		return object_id;
	}
	
	public String getRule() {
		return rule;
	}
}
