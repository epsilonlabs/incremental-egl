package org.eclipse.epsilon.egx.live;

public class RuleApplication {
	private String ruleName;
	private String objectId;
	private String generatedFile;
	private String ruleId;
	private String template;
	
	public RuleApplication(String objId, String rule) {
		objectId = objId;
		ruleName = rule;
	}
	
	public String getRuleName() {
		return ruleName;
	}
	
	public String getObjectId() {
		return objectId;
	}
	
	public void setGeneratedFile(String genFile) {
		generatedFile = genFile;
	}
	
	public String getGeneratedFile() {
		return generatedFile;
	}
	
	public void setRuleName(String rule) {
		ruleName = rule;
	}
	
	public void setRuleId(String rId) {
		ruleId = rId;
	}
	
	public String getRuleId() {
		return ruleId;
	}
	
	public void setTemplateFile(String tFile) {
		template = tFile;
	}
	
	public String getTemplateFile() {
		return template;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

	    result.append(this.getClass().getName());
	    result.append(" {");
	    result.append(ruleName);
	    result.append(" rule on object with id: ");
	    result.append(objectId);
	    result.append(" }");
	    
	    return result.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof RuleApplication))
			return false;
		return (((RuleApplication)other).objectId.equals(this.objectId) && ((RuleApplication)other).ruleName == ruleName);
			
	}
	
	@Override
	public int hashCode() {
	    int hashCode = 1;
	    
	    hashCode = hashCode * 37 + objectId.hashCode();
	    hashCode = hashCode * 37 + (null == ruleName ? 0 : ruleName.hashCode());

	    return hashCode;
	}
	
	

}
