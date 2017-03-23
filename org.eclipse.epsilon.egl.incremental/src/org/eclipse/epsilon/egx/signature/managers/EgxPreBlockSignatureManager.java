package org.eclipse.epsilon.egx.signature.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.epsilon.egx.signatures.PreBlockSignature;
import org.eclipse.epsilon.egx.signatures.Signature;
import org.eclipse.epsilon.eol.execute.context.Variable;

public class EgxPreBlockSignatureManager extends AbstractSignatureManager {
	
	private String table = getTables().get(0);
	
	@Override
	protected String getTableSuffix() {
		return "preblock";
	}
	
	protected String createTableCommand(String table) {
		return "create table %s (templateName varchar, variableName varchar, variableValue varchar, rule varchar)";
	}
	
	@Override
	protected void writeSignatures(String rule) throws Exception {
		
		Map<String,Signature> sigMap = new HashMap<String,Signature>();
		sigMap.putAll (allSignatures.get(rule));
		
		for (String key : sigMap.keySet()) {
			if(!((PreBlockSignature) sigMap.get(rule)).getVariables(rule).isEmpty() && !key.equals("")) {
				PreBlockSignature preb = (PreBlockSignature) sigMap.get(key);
				for(String template : preb.getVariables(key).keySet()) {
					for(Variable var : preb.getVariables(key).get(template)) {
						statement.execute(String.format("insert into %s values('%s', '%s', '%s', '%s')", table, template, ((Variable)var).getName(), ((Variable)var).getValue(), preb.getObjRule()));
					}
				}
			}
		}	
	}
	
	@Override
	public void constructSignatures(int mode) throws Exception {
		
		Map<String, PreBlockSignature> preblocksMap = new HashMap<String,PreBlockSignature>();
		allSignatures.clear();
		
		resultSet = null;
		try {
			resultSet = statement.executeQuery(String.format("select * from %s", "pre_block"));
		} catch(Exception e) {
			System.out.println("Table pre_block does not exist");
		}
		
		if(resultSet != null) {
			while(resultSet.next()) {
				Variable var = new Variable();
				var.setName(resultSet.getString(2));
				var.setValueBruteForce(resultSet.getString(3));
				
				String template = resultSet.getString(1);
				String rule = resultSet.getString("rule");
				
				if(preblocksMap.containsKey(rule)) {
					if(!preblocksMap.get(rule).getVariables(rule).containsKey(template)) {
						preblocksMap.get(rule).getVariables(rule).put(template, new ArrayList<Variable>());				
					}
					preblocksMap.get(rule).getVariables(rule).get(template).add(var);
				}
				else {
					PreBlockSignature preb = new PreBlockSignature(rule);
					preblocksMap.put(rule, preb);
					if(!preb.getVariables(rule).containsKey(template)) {
						preb.getVariables(rule).put(template, new ArrayList<Variable>());
					}
					preb.getVariables(rule).get(template).add(var);	
				}
			}
		}
		
		if(!preblocksMap.isEmpty()) {
			for(String key : preblocksMap.keySet()) {
				if(!allSignatures.containsKey(key)) {
					allSignatures.put(key, new HashMap<String, Signature>());
				}
				
				allSignatures.get(key).put(key, preblocksMap.get(key));
			}
		}
		
	}

	
	@Override
	public void addSignature(Signature s, String rule) {
		
		if(allSignatures.get(rule) == null) {
			allSignatures.put(rule, new HashMap<String, Signature>());		
		}
		
		PreBlockSignature pre = (PreBlockSignature) allSignatures.get(rule).get(rule);
		
		if(pre != null) {
			HashMap<String, ArrayList<Variable>> a = ((PreBlockSignature)s).getVariables(rule);
			if(!pre.getVariables(rule).isEmpty()) {
				HashMap<String, ArrayList<Variable>> b = new HashMap<String, ArrayList<Variable>>();
				for(String template : ((PreBlockSignature)s).getVariables(rule).keySet()) {
					for(String t : pre.getVariables(rule).keySet()) {
						if (!t.equals(template)) {
							b.put(template, a.get(template));
							//((PreBlockSignature)allSignatures.get(rule).get(rule)).getVariables(rule).put(template, a.get(template));
						}
					}
				}
				for(String template : b.keySet()) {
					((PreBlockSignature)allSignatures.get(rule).get(rule)).getVariables(rule).put(template, a.get(template));
				}
			}
		}
		else {
			allSignatures.get(rule).put(rule, s);
		}
		
	}

	
	@Override
	protected ArrayList<String> getTables() {
		ArrayList<String>tableList = new ArrayList<String>();
		tableList.add("pre_block");
		
		return tableList;
	}

	@Override
	public Signature getSignature(String identifier, String rule)
			throws Exception {
		//PreBlockSignature b = (PreBlockSignature) allSignatures.get(rule).get(rule);
		if(allSignatures.get(rule) != null)
			return allSignatures.get(rule).get(rule);
		return null;
	}
	
}
