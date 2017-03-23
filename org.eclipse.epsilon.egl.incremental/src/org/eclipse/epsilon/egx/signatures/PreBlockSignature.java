package org.eclipse.epsilon.egx.signatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.epsilon.eol.execute.context.Variable;

public class PreBlockSignature extends Signature {
	
	//private ArrayList<Variable> templateVariables;
	private Map<String, HashMap<String,ArrayList<Variable>>> templateVariablesList = new HashMap<String, HashMap<String, ArrayList<Variable>>>();

	public PreBlockSignature(String r, String t) {
		super();
		rule = r;
		templateFile = t;
		templateVariablesList.put(rule, new HashMap<String, ArrayList<Variable>>());
	}
	
	public PreBlockSignature(String r) {
		super();
		rule = r;
		templateVariablesList.put(rule, new HashMap<String, ArrayList<Variable>>());
	}
	
	public HashMap<String, ArrayList<Variable>> getVariables(String rule) {
		return templateVariablesList.get(rule);
	}
	
	public ArrayList<Variable> getTemplateVariables(String template) {
		return getVariables(rule).get(template);
	}
}
