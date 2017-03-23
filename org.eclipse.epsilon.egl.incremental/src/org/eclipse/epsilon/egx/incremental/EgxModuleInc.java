package org.eclipse.epsilon.egx.incremental;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.egl.GenerationRule;
import org.eclipse.epsilon.egx.live.GenerationRuleInc;
import org.eclipse.epsilon.egx.live.RuleApplication;
import org.eclipse.epsilon.egx.live.ChangeAgent;
import org.eclipse.epsilon.egx.signature.managers.AbstractSignatureManager;
import org.eclipse.epsilon.egx.signature.managers.EgxH2SignatureManager;
import org.eclipse.epsilon.egx.signature.managers.EgxPreBlockSignatureManager;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.SingleFrame;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.erl.rules.INamedRule;


public class EgxModuleInc extends EgxModule {

	private AbstractSignatureManager signMgr = new EgxH2SignatureManager();
	private AbstractSignatureManager varMgr = new EgxPreBlockSignatureManager();
	private HashMap<String, HashMap<String, Variable>> preVariables = new HashMap<String, HashMap<String, Variable>>();
	private ChangeAgent changeAgent;
	public HashMap<String, String> ruleMap = new HashMap<String, String>();
	private static int count = 0;
	
	public EgxModuleInc() {
		super();
	}
	
	public EgxModuleInc(EglTemplateFactory templateFactory) {
		super(templateFactory);
	}
	
	@Override
	protected GenerationRule createGenerationRule(AST generationRuleAst) {
		return new GenerationRuleInc(generationRuleAst, signMgr, varMgr);
	}
	
	
	@Override
	public Object execute() throws EolRuntimeException {
		
		long startTime = System.nanoTime();
		
		varMgr.connect();
		signMgr.connect();
		getInvokedTemplates().clear();
		
		context.setModule(this);
		context.copyInto(templateFactory.getContext(), true);
		
		execute(getPre(), context);
		processPreBlockVariables();
		
		try {
			varMgr.constructSignatures(0);
			if(this.isOfflineMode()) {
				signMgr.constructSignatures(0);
			}
			else {
				signMgr.constructSignatures(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(!isOfflineMode()) {
			if(count >= 1) {
				
				ArrayList<String> rules = new ArrayList<String>();
				for(INamedRule rule : getGenerationRules()) {
					rules.add(rule.getName());
				}
				Set<RuleApplication> ra = ((EgxH2SignatureManager) signMgr).getRuleApplications(getChangeAgent());

				HashMap <INamedRule, ArrayList> appl = new HashMap<INamedRule, ArrayList>();
				for(RuleApplication ruleApp : ra) {
					IModel owner = context.getModelRepository().getModelByName("ECore");
					EObject o = (EObject) owner.getElementById(ruleApp.getObjectId());
					INamedRule aRule = null;
					String obj_id = null;
					if(ruleApp.getRuleName() != null && o != null) {
						if(this.declaredGenerationRules.getRule(ruleApp.getRuleName()) != null) {
							aRule = this.declaredGenerationRules.getRule(ruleApp.getRuleName());
							obj_id = ruleApp.getObjectId();
						}
					}
					else {
						if(o != null) {		//added object not found in trace
							for(Object rule: ruleMap.keySet()) {
								if(ruleMap.get(rule.toString()).equals(o.eClass().getName()) || ruleMap.get(rule.toString()).equals(owner.getName() + "!" + o.eClass().getName())) {
									rule = this.declaredGenerationRules.getRule(rule.toString());
									obj_id = ruleApp.getObjectId();
									if(appl.get(rule) == null) {
										appl.put((INamedRule) rule, new ArrayList<String>());
										appl.get(rule).add(obj_id);
									}
									else {
										if(!appl.get(rule).contains(obj_id)) {
											appl.get(rule).add(obj_id);
										}
									}
								}
							}
						}
						else {		//probably deleted object
							if(ruleApp.getRuleName() != null)
								((GenerationRuleInc) this.declaredGenerationRules.getRule(ruleApp.getRuleName())).deleteObsoleteFile(ruleApp.getGeneratedFile());
						}
					}
					
					if(aRule != null) {
						if(appl.get(aRule) == null) {
							appl.put(aRule, new ArrayList<String>());
							appl.get(aRule).add(obj_id);
						}
						else {
							if(!appl.get(aRule).contains(obj_id)) {
								appl.get(aRule).add(obj_id);
							}
						}
					}
				}
				
				for(INamedRule rule : appl.keySet()) {
					((GenerationRuleInc) rule).generateAll(context, templateFactory, this, appl.get(rule));
				}

			} else {
				for (INamedRule rule : getGenerationRules()) {
					((GenerationRuleInc) rule).generateAll(context, templateFactory, this, null);
				}
				count = 1;
			}
		} else {
			for (INamedRule rule : getGenerationRules()) {
				((GenerationRuleInc) rule).generateAll(context, templateFactory, this, null);
			}
		}
		
		execute(getPost(), context);
		
		System.out.format("Execution time: %.4f \n", (System.nanoTime() - startTime)/1000000000.0);
		System.out.println("Invocations: " + this.getInvokedTemplates().size());
		
		try {
			varMgr.dispose();
			signMgr.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private void processPreBlockVariables() {
		SingleFrame frame = context.getFrameStack().getFrames().get(0);
		SingleFrame myFrame = frame.clone();
		myFrame.remove("TemplateFactory");
		myFrame.remove("openTag");
		myFrame.remove("openOutputTag");
		myFrame.remove("closeTag");
		myFrame.remove("UserInput");

		//System.out.println(frame.getAll().toString());
		preVariables.put("preblock", new HashMap<String, Variable>());
		for(String key : myFrame.getAll().keySet()) {
			if(myFrame.get(key) instanceof Collection) {
				String value = EgxModuleInc.getList((Collection<?>) myFrame.get(key));
				Variable var = (Variable)myFrame.get(key);
				var.setValueBruteForce(value);
				preVariables.get("preblock").put(key, var);
			}
			else{
				preVariables.get("preblock").put(key, (Variable)myFrame.get(key));
			}
		}
	}
	
	private static String getList(Collection<?> list) {
		String ret = "";
		if(list.isEmpty())
			return "";
		else {
			for(Object o : list) {
				if(o instanceof Collection<?>) {
					return ret + getList((Collection<?>) o);
				}
				else {
					ret += o.toString();
				}
			}
		}
		return ret;
	}

	public void setLaunchConfigName(String configurationName) {
		signMgr.setConfiguration(configurationName);
		varMgr.setConfiguration(configurationName);
	}
	
	public HashMap<String, HashMap<String, Variable>> getPreVariables () {
		return preVariables;
	}
	
	public void setChangeAgent(ChangeAgent ca) {
		changeAgent = ca;
	}
	
	private ChangeAgent getChangeAgent() {
		return changeAgent;
	}

	public boolean isOfflineMode() {
		return true;
	}
	
}
