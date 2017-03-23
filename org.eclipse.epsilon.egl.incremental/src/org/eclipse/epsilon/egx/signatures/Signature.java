package org.eclipse.epsilon.egx.signatures;


public abstract class Signature {
	
	protected String rule;
	protected String templateFile;
	protected String generatedFile;
	protected String objectId;
	
	public String getObjRule(){
		return rule;
	}
	
	//abstract public List<String> getObjSignatureValues();
	
	//abstract public void setSignatureValue(List<String> s);

	public void setGeneratedFile(String gFile) {
		generatedFile = gFile;
	}
	
	public String getGeneratedFile() {
		return generatedFile;
	}

	public String getObjId() {
		return objectId;
	}

	public void setObjId(String obj_id) {
		objectId = obj_id;	
	}

	public String getTemplateFile() {
		return templateFile;
	}
	
	public void setTemplateFile(String tFile) {
		templateFile = tFile;
	}

	public void setObjRule(String ruleName) {
		rule = ruleName;
	}
}
