package org.eclipse.epsilon.egx.signature.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.epsilon.egx.live.ChangeAgent;
import org.eclipse.epsilon.egx.live.ChangeData;
import org.eclipse.epsilon.egx.live.RuleApplication;
import org.eclipse.epsilon.egx.signaturepropertyaccess.SignaturePropertyAccess;
import org.eclipse.epsilon.egx.signaturepropertyaccess.SignatureSpa;
import org.eclipse.epsilon.egx.signatures.ModelElementSignature;
import org.eclipse.epsilon.egx.signatures.Signature;

public class EgxH2SignatureManager extends AbstractSignatureManager {
	
	private Map<String, Map<String, ModelElementSignature>> tmp = new HashMap<String, Map<String,ModelElementSignature>>();
	private ArrayList<SignaturePropertyAccess> sigPropAccesses = new ArrayList<SignaturePropertyAccess>();
	private ArrayList<SignatureSpa> spas = new ArrayList<SignatureSpa>();
	private Map<SignaturePropertyAccess, List<RuleApplication>> ruleApps = new HashMap<SignaturePropertyAccess, List<RuleApplication>>();
	private ArrayList<RuleApplication> rules = new ArrayList<RuleApplication>();
	private Map<String, List<SignatureSpa>> sigSpas = new HashMap<String, List<SignatureSpa>>();

	
	@Override
	protected String getTableSuffix() {
		return getTableName("signature_property_access");
	}

	private String getTableName(String table) {
		if(table.equals("spa"))
			return "signature_property_access";
		else if(table.equals("rule_invocations"))
			return "rule_invocations";
		else
			return "signature_spa";
	}
	
	protected String createTableCommand(String table) {
		if(table.equals("signature_property_access"))
			return "create table %s (spa_id int auto_increment, object_id varchar, property_name varchar, property_value varchar, CONSTRAINT access UNIQUE(object_id, property_name))";
		else if(table.equals("rule_invocations"))
			return "create table %s (rule_id int auto_increment, object_id varchar, generatedFile varchar, rule varchar, template varchar, CONSTRAINT rule_invocations UNIQUE(object_id, rule, template))";
		else
			return "create table %s (spa_id int, rule_id varchar, CONSTRAINT spa_rule UNIQUE(spa_id, rule_id))";
	}
	
	@Override
	protected void writeSignatures(String rule) throws Exception {
		Map<String,Signature> sigMap = new HashMap<String,Signature>(); 
		sigMap.putAll(allSignatures.get(rule));
		
		Map<String, Integer> ruleIDs = new HashMap<String, Integer>();
		ResultSet rs = null;
		
		for (String key : sigMap.keySet()) { //key : objectId
			if(!((ModelElementSignature) sigMap.get(key)).getSignatureValueAsPropertyAccesses().isEmpty()) {
				int ruleId = -1;
				statement.execute(String.format("merge into rule_invocations (object_id, generatedFile, rule, template) key(object_id, rule, template) values('%s', '%s', '%s', '%s')", key, sigMap.get(key).getGeneratedFile(), sigMap.get(key).getObjRule(), sigMap.get(key).getTemplateFile()));
				rs = statement.getGeneratedKeys();
				if(rs.next()) {
					ruleId = rs.getInt(1);
					//System.out.println(key+ " ** " + sigMap.get(key).getObjRule() +  " ** " + sigMap.get(key).getTemplateFile());
					ruleIDs.put(key+sigMap.get(key).getObjRule()+sigMap.get(key).getTemplateFile(), ruleId);
				}
				for (SignaturePropertyAccess propAccess: ((ModelElementSignature) sigMap.get(key)).getSignatureValueAsPropertyAccesses()) {
					int generatedId = -1;
					statement.execute(String.format("merge into %s (object_id, property_name, property_value) key(object_id, property_name) values ('%s', '%s', '%s')", "signature_property_access", propAccess.getElementId(), propAccess.getPropertyName(), propAccess.getPropertyValue()));
					//statement.execute(String.format("merge into %s (spa_id, object_id, property_name) key(object_id, property_name) values ((select spa_id from signature_property_access s where s.object_id='%s' and s.property_name='%s' ),'%s', '%s')", "signature_property_access", propAccess.getElementId(), propAccess.getPropertyName(),propAccess.getElementId(), propAccess.getPropertyName()));
					rs = statement.getGeneratedKeys();

					
					int aRuleId = ruleIDs.get(key+sigMap.get(key).getObjRule()+sigMap.get(key).getTemplateFile());
					if(rs.next()) {
						generatedId = rs.getInt(1);
						statement.execute(String.format("merge into %s (spa_id, rule_id) key(spa_id, rule_id) values('%s', '%s')", "signature_spa", generatedId, aRuleId));
					}
					else {
						ResultSet rSet = null;
						rSet = statement.executeQuery(String.format("select spa_id from signature_property_access where object_id='%s' and property_name='%s'",propAccess.getElementId(), propAccess.getPropertyName()));
						if(rSet.next()) {
							String spaId = rSet.getString("spa_id");
							statement.execute(String.format("merge into %s (spa_id, rule_id) key(spa_id, rule_id) values('%s', '%s')", "signature_spa", spaId, aRuleId));
						}
					}
				}
			}
		}
		
	}
	
	protected ArrayList<SignaturePropertyAccess> getSignaturePropAccesses() throws Exception {
		sigPropAccesses.clear();
		assert(connection != null);
		resultSet = null;
		
		try {
			resultSet = statement.executeQuery(String.format("select * from %s", "signature_property_access"));
		} catch (Exception e) {
			System.err.println("sql: table signature_property_access does not exist");
		}
		
		if(resultSet != null) {
			while(resultSet.next()) {
				SignaturePropertyAccess spa = new SignaturePropertyAccess(resultSet.getObject("spa_id").toString(), resultSet.getString("object_id"), resultSet.getString("property_name"));
				spa.setPropertyValue(resultSet.getString("property_value"));
				sigPropAccesses.add(spa);
			}
		}
		
		return sigPropAccesses;
	}
	
	private void getSignatureSpa() throws SQLException {
		assert(connection != null);
		
		resultSet = null;
		
		try {
			resultSet = statement.executeQuery(String.format("select * from signature_spa"));
		} catch (Exception e) {
			System.err.println("sql: table signature_spa does not exist");
		}
		
		if(resultSet != null) {
			while(resultSet.next()) {
				SignatureSpa spa = new SignatureSpa(resultSet.getObject("spa_id").toString(), resultSet.getString("rule_id"), null);
				if(!sigSpas.containsKey(resultSet.getString("rule_id"))) {
					sigSpas.put(resultSet.getString("rule_id"), new ArrayList<SignatureSpa>());
				}
				sigSpas.get(resultSet.getString("rule_id")).add(spa);
					
			}
		}
	}
	
	private void getRuleInvocations() throws SQLException {
		assert(connection != null);

		resultSet = null;
		
		try {
			resultSet = statement.executeQuery(String.format("select * from rule_invocations"));
		} catch (Exception e) {
			System.err.println("sql: table signature_spa does not exist");
		}
		
		if(resultSet != null) {
			while(resultSet.next()) {
				RuleApplication rule = new RuleApplication(resultSet.getObject("object_id").toString(), resultSet.getString("rule"));
				rule.setGeneratedFile(resultSet.getString("generatedFile"));
				rule.setRuleId(resultSet.getString("rule_id"));
				rule.setTemplateFile(resultSet.getString("template"));
				rules.add(rule);
			}
		}
	}
	
	
	protected void getAllSignature() throws SQLException {
		getSignatureSpa();
		getRuleInvocations();
		String rule = null;
		String obj_id = null;
		
		tmp.clear();
		spas.clear();
		
		for(RuleApplication r : rules) {
			
			if(sigSpas.containsKey(r.getRuleId())) {
				
				for(SignatureSpa s : sigSpas.get(r.getRuleId())) {
					rule = r.getRuleName();
					obj_id = r.getObjectId();
					if(tmp.containsKey(rule)) {
						if(tmp.get(rule).containsKey(obj_id)) {
							tmp.get(rule).get(obj_id).getObjSignatureValues().add(s.getSpaId());
						}
						else {
							tmp.get(rule).put(obj_id, new ModelElementSignature(obj_id, rule));
							tmp.get(rule).get(obj_id).getObjSignatureValues().add(s.getSpaId());
						}
					}
					else {
						tmp.put(rule, new HashMap<String, ModelElementSignature>());
						if(!tmp.get(rule).containsKey(obj_id)) {
							tmp.get(rule).put(obj_id, new ModelElementSignature(obj_id, rule));
							tmp.get(rule).get(obj_id).getObjSignatureValues().add(s.getSpaId());
						}
					}
					tmp.get(rule).get(obj_id).setGeneratedFile(r.getGeneratedFile());
					tmp.get(rule).get(obj_id).setTemplateFile(r.getTemplateFile());
					spas.add(new SignatureSpa(s.getSpaId(), obj_id, rule));
				}
			}
		}
	}
	
	
	@Override
	public void constructSignatures(int mode) throws Exception {
		
		if(allSignatures.isEmpty()) {
			getAllSignature();
			getSignaturePropAccesses();
			
			if(mode == 1) {
				constructRuleApp();
			}
			
			allSignatures.clear();
			
			for (String rule : tmp.keySet()) {
				if(allSignatures.get(rule) == null)
				{
					allSignatures.put(rule, new HashMap<String, Signature>());
					for(String obj_id : tmp.get(rule).keySet()) {
						ModelElementSignature s = tmp.get(rule).get(obj_id);
						for(SignaturePropertyAccess spa : sigPropAccesses) {
							if(tmp.get(rule).get(obj_id).getObjSignatureValues().contains(spa.getSpaId())) {
								s.setGeneratedFile(tmp.get(rule).get(obj_id).getGeneratedFile());
								s.setTemplateFile(tmp.get(rule).get(obj_id).getTemplateFile());
								tmp.get(rule).get(obj_id).getSignatureValueAsPropertyAccesses().add(spa);
							}
						}
						if(!allSignatures.get(rule).containsKey(obj_id)) {
							allSignatures.get(rule).put(obj_id, s);						
						}
					}		
				}
			}
		}
	
	}

	@Override
	public void addSignature(Signature s, String rule) {
		if(allSignatures.get(rule) == null){
			allSignatures.put(rule, new HashMap<String, Signature>());		
		}
		allSignatures.get(rule).put(((ModelElementSignature) s).getObjId(), s);	
		
	}
	
	
	public Set<RuleApplication> getRuleApplications(ChangeAgent change) {
		Set<RuleApplication> applications = new HashSet<RuleApplication>();
		if(change != null && !change.getChanges().isEmpty()) {
			for(ChangeData data : change.getChanges()) {
				if(data.getPropertyName() != null) {
					SignaturePropertyAccess prop = new SignaturePropertyAccess(data.getObjectId(), data.getPropertyName());
					if(ruleApps.containsKey(prop)) {
						applications.addAll(ruleApps.get(prop));
						}
					}
				else if(data.getPropertyName() == null) {
					applications.add(new RuleApplication(data.getObjectId(), null));
					
					for(SignaturePropertyAccess spa : ruleApps.keySet()) {
					
						if(data.getModificationType().equals("add")) {
							if(spa.getPropertyName().equals("all") || spa.getPropertyName().equals("allInstances")) {
								if(spa.getModelElement().equals(data.getElementType())) {
									applications.addAll(ruleApps.get(spa));
									}
								}
							}
						else if(data.getModificationType() == "remove") {
							if(spa.getPropertyName().equals("all") || spa.getPropertyName().equals("allInstances")) {
								if(spa.getModelElement().equals(data.getElementType())) {
									applications.addAll(ruleApps.get(spa));
									}
								}
							}
					}
				
					if(data.getModificationType().equals("remove")) {
						for(String rule : tmp.keySet()) {
							if(tmp.get(rule).containsKey(data.getObjectId())) {
								RuleApplication rApp = new RuleApplication(data.getObjectId(), rule);
								ModelElementSignature s = tmp.get(rule).get(data.getObjectId());
								System.err.println(s.getGeneratedFile());
								rApp.setGeneratedFile(tmp.get(rule).get(data.getObjectId()).getGeneratedFile());
								if(applications.contains(rApp)) {
									applications.remove(rApp);
								}
								if(rApp.getGeneratedFile() != null)
									applications.add(rApp);
							}
						}
					}
				}
			}
		}
		
		return applications;
	}
	
	public Set<RuleApplication> getRuleApplications___(ChangeAgent change) {
		
		Set<RuleApplication> applications = new HashSet<RuleApplication>();
		
		if(change != null && !change.getChanges().isEmpty()) {
			for(ChangeData data : change.getChanges()) {
				for(SignaturePropertyAccess spa : ruleApps.keySet()) {
					if(data.getModificationType().equals("update")) {	
						if(spa.getElementId().equals(data.getElementId()) && spa.getPropertyName().equals(data.getPropertyName())) {
							applications.addAll(ruleApps.get(spa));
						}
					}
					else if(data.getModificationType().equals("add")) {
						if(data.getPropertyName() == null) {		//resource add
							applications.add(new RuleApplication(data.getObjectId(), null));
						}
						else {		//me add
							if(data.getElementId().equals(spa.getElementId()) && data.getPropertyName().equals(spa.getPropertyName())) {
								applications.addAll(ruleApps.get(spa));
							}
						}
						if(spa.getPropertyName().equals("all") || spa.getPropertyName().equals("allInstances")) {
							if(spa.getModelElement().equals(data.getElementType())) {
								applications.addAll(ruleApps.get(spa));
							}
						}
						
					}
					else if(data.getModificationType() == "remove") {
						if(spa.getPropertyName().equals("all") || spa.getPropertyName().equals("allInstances")) {
							if(spa.getModelElement().equals(data.getElementType())) {
								applications.addAll(ruleApps.get(spa));
							}
						}
					}
				}
				
				if(data.getModificationType().equals("remove")) {
					for(String rule : tmp.keySet()) {
						if(tmp.get(rule).containsKey(data.getObjectId())) {
							RuleApplication rApp = new RuleApplication(data.getObjectId(), rule);
							ModelElementSignature s = tmp.get(rule).get(data.getObjectId());
							System.err.println(s.getGeneratedFile());
							rApp.setGeneratedFile(tmp.get(rule).get(data.getObjectId()).getGeneratedFile());
							if(applications.contains(rApp)) {
								applications.remove(rApp);
							}
							applications.add(rApp);
						}
					}
					
				}
			}
		}
		return applications;
	}
	
	public void constructRuleApp() {
		ruleApps = new HashMap<SignaturePropertyAccess, List<RuleApplication>>();
		
		for(SignaturePropertyAccess propAccess : sigPropAccesses) {
			for(SignatureSpa spa : spas) {
				if(spa.getSpaId().equals(propAccess.getSpaId())) {
					RuleApplication ruleApplication = new RuleApplication(spa.getObjectId(), spa.getRule());
					if(ruleApps.get(propAccess) == null) {
						ruleApps.put(propAccess, new ArrayList<RuleApplication>());
					}
					if(!ruleApps.get(propAccess).contains(ruleApplication)) {
						ruleApps.get(propAccess).add(ruleApplication);
					}
					
				}
			}	
		}
		
	}
	

	@Override
	protected ArrayList<String> getTables() {
		ArrayList<String>tableList = new ArrayList<String>();
		tableList.add("signature_spa");
		tableList.add("rule_invocations");
		tableList.add("signature_property_access");
		
		return tableList;
	}

	@Override
	public Signature getSignature(String identifier, String rule) throws Exception {
		
		if(allSignatures.get(rule) == null) {
			//constructSignatures();
			return null;
		}
		
		return allSignatures.get(rule).get(identifier);
	}
	
	@Override
	public void finishWritingSignatures() throws Exception {
		getAllSignature();
		getSignaturePropAccesses();
		constructRuleApp();
	}
	
}


