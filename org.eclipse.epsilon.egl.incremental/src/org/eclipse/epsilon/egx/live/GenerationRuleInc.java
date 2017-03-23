package org.eclipse.epsilon.egx.live;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.common.util.AstUtil;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplate;
import org.eclipse.epsilon.egl.EglTemplate;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.egl.GenerationRule;
import org.eclipse.epsilon.egl.execute.context.IEglContext;
import org.eclipse.epsilon.egl.parse.EgxParser;
import org.eclipse.epsilon.egx.incremental.EgxModuleInc;
import org.eclipse.epsilon.egx.signature.managers.AbstractSignatureManager;
import org.eclipse.epsilon.egx.signaturepropertyaccess.SignaturePropertyAccess;
import org.eclipse.epsilon.egx.signatures.ModelElementSignature;
import org.eclipse.epsilon.egx.signatures.PreBlockSignature;
import org.eclipse.epsilon.egx.signatures.Signature;
import org.eclipse.epsilon.eol.exceptions.EolIllegalReturnException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.Return;
import org.eclipse.epsilon.eol.execute.context.FrameType;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.execute.control.IExecutionListener;
import org.eclipse.epsilon.eol.execute.introspection.IPropertyGetter;
import org.eclipse.epsilon.eol.execute.introspection.recording.IPropertyAccess;
import org.eclipse.epsilon.eol.execute.introspection.recording.IPropertyAccessRecorder;
import org.eclipse.epsilon.eol.execute.introspection.recording.PropertyAccess;
import org.eclipse.epsilon.eol.execute.introspection.recording.PropertyAccessExecutionListener;
import org.eclipse.epsilon.eol.execute.introspection.recording.PropertyAccessRecorder;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.parse.EolParser;
import org.eclipse.epsilon.eol.types.EolMap;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.eclipse.epsilon.eol.types.EolType;

public class GenerationRuleInc extends GenerationRule {
	
	private AbstractSignatureManager signMgr;
	private AbstractSignatureManager varMgr;
	private String template = "";
	private PreBlockSignature rulePreBlockSignature = new PreBlockSignature(name);
	private HashMap<String, Variable> preVars;
	private ArrayList<SignaturePropertyAccess> featureCallStore = new ArrayList<SignaturePropertyAccess>();
	private IModel sourceModel = null;
	private IPropertyAccessRecorder templateRecorder = new PropertyAccessRecorder();
	private boolean templateExecuting = false;
	private EolMap parameters = new EolMap();

	public GenerationRuleInc(AST ast, AbstractSignatureManager signMgr, AbstractSignatureManager variableMgr) {
		super(ast);
		this.signMgr = signMgr;
		this.varMgr = variableMgr;
	}
	
	@Override
	protected void parse(AST ast) {
		super.parse(ast);
		signatureAst = AstUtil.getChild(ast, EgxParser.SIGNATURE);
	}
	
	public Object getValueFromPropertyAccess(IPropertyAccess propertyAccess, IEolContext context) throws EolRuntimeException {
		IPropertyGetter getter = context.getIntrospectionManager().getPropertyGetterFor(propertyAccess.getModelElement(), propertyAccess.getPropertyName(), context);
		return getter.invoke(propertyAccess.getModelElement(), propertyAccess.getPropertyName());
	}
	
	public String calculatePartialSignature(String modelElementId, String propertyName, IModel model, IEglContext context) throws EolRuntimeException {
		
		Object modelElement = model.getElementById(modelElementId);
		if (modelElement == null || modelElementId == null)
			return "";
		
		if(propertyName.equals("null"))
			return "null";
		
		PropertyAccess fake = new PropertyAccess(modelElement, propertyName);
		Object value = getValueFromPropertyAccess(fake, context);	//if primitive
		
		if (value instanceof EList && ((EList<?>)value).size() > 0) {
			String val = "";
			for (int i = 0; i < ((EList<?>)value).size(); i++) {				
				val += model.getElementId(((EList<?>) value).get(i));	//list of ids
			}
			value = val;
		}
		else if (value instanceof EObject) {	//if single ref
			value = "" + model.getElementId(value);
		}
		
		return "" + value;
		
	}
	
	public boolean paramsBlockHasChanged(HashMap<String, Variable> preVars, IModel sourceModel, Object o, IEglContext context) throws EolRuntimeException {
		PreBlockSignature storedRuleSignature = null;
		try {
			storedRuleSignature = (PreBlockSignature) varMgr.getSignature(template, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(storedRuleSignature != null && storedRuleSignature.getTemplateVariables(template) != null) {
			for(Variable var : storedRuleSignature.getTemplateVariables(template)) {
				if(preVars.containsKey(var.getName())) {
					if(!var.getValue().toString().equals(preVars.get(var.getName()).getValue().toString())) {
						return false;
					}
					
				}
			}
		}
		
		return true;
			
	}
	
	public boolean needsToExecute(IModel sourceModel, Object o, IEglContext context) throws EolRuntimeException {
		
		String object_id = sourceModel.getElementId(o);

		ModelElementSignature storedSignatureObj = null;
		
		// retrieve stored signature
		try {
			storedSignatureObj = (ModelElementSignature) signMgr.getSignature(object_id, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean preBlockChanged =  paramsBlockHasChanged(preVars, sourceModel, o, context);
		if(!preBlockChanged)
			return preBlockChanged;

		
		boolean signed = false;
		List<SignaturePropertyAccess> storedSignatureValue = new ArrayList<SignaturePropertyAccess>();		
		
		if(storedSignatureObj != null) {
			storedSignatureValue  = storedSignatureObj.getSignatureValueAsPropertyAccesses();
		
				if(storedSignatureValue.isEmpty() || ((SignaturePropertyAccess)storedSignatureValue.get(0)).getPropertyName() == null) {
					return true;
				
			}
			
			for (SignaturePropertyAccess propAccess : storedSignatureValue) {
				if(propAccess.getPropertyName().equals("all") || propAccess.getPropertyName().equals("allInstances")) {
					String collectionValue = Integer.toString(((Collection<?>)sourceModel.getAllOfKind(propAccess.getModelElement().toString())).size());
					signed = propAccess.getPropertyValue().equals(collectionValue);
					if(!signed) {
						return signed;
					}
				} else {
					signed = calculatePartialSignature(propAccess.getElementId(), propAccess.getPropertyName(), sourceModel, context).equals(propAccess.getPropertyValue());
					if (!signed) {
						return signed;
					}
				}
			}
		}
		
		return signed;
		
	}
	
	public void generateAll(IEglContext context, EglTemplateFactory templateFactory, EgxModule module, ArrayList<?> obj) throws EolRuntimeException {
		
		module.getContext().getExecutorFactory().addExecutionListener(new IExecutionListener() {
			
			Object type = null;
					
			@Override
			public void finishedExecuting(AST ast, Object result, IEolContext context) {
				if (ast.getType() == EolParser.POINT) {
					if (isAllInstances(ast) && type instanceof EolModelElementType){
						SignaturePropertyAccess featureCallSpa = new SignaturePropertyAccess(((EolModelElementType) type).getName(), ast.getSecondChild().getText());
						featureCallSpa.setPropertyValue(Integer.toString(((Collection<?>)result).size()));
						featureCallSpa.setElementId(((EolModelElementType) type).getName());
						featureCallStore.add(featureCallSpa);
					}
				}
				
				type = result;
				
			}
		
			private boolean isAllInstances(AST ast) {
				return (ast.getSecondChild().getText().equals("all")  || ast.getSecondChild().getText().equals("allInstances")) && ast.getSecondChild().getChildren().isEmpty();
			}
			
			@Override
			public void aboutToExecute(AST ast, IEolContext context) {
				if(parameters != null && templateExecuting) {
					if (ast.getType() == EolParser.FEATURECALL) {
						if(preVars.get(ast.toString()) != null) {
							if(rulePreBlockSignature.getVariables(name).get(template) == null)
								rulePreBlockSignature.getVariables(name).put(template, new ArrayList<Variable>());
								
							if(!rulePreBlockSignature.getVariables(name).get(template).contains(preVars.get(ast.toString()))){
								rulePreBlockSignature.getVariables(name).get(template).add(preVars.get(ast.toString()));
								}
							}
						}
					}
				
				}
			});
		
		preVars = ((EgxModuleInc) module).getPreVariables().get("preblock");
		templateFactory.getContext().getExecutorFactory().addExecutionListener(new PropertyAccessExecutionListener(templateRecorder));	
		
		Map<URI, EglTemplate> templateCache = new HashMap<URI, EglTemplate>();
		
		Collection<?> all = new ArrayList<Object>();
		
		if(((EgxModuleInc)module).isOfflineMode() || obj == null) { //or isSubsequent
			
			if (sourceParameter != null) {
				
				EolType sourceParameterType = sourceParameter.getType(context);
				sourceModel = ((EolModelElementType) sourceParameterType).getModel();

				if (sourceParameterType instanceof EolModelElementType) {
					if (isGreedy()) {
						all = ((EolModelElementType) sourceParameterType).getAllOfKind();
					} else {
						all = ((EolModelElementType) sourceParameterType).getAllOfType();
					}
					((EgxModuleInc)module).ruleMap.put(this.getName(), ((EolModelElementType)sourceParameterType).getName());
				}
			} else {
				all.add(null);
			}
		}
		else {
			all = obj;
		}
			
			
		for (Object o : all) {
			
			featureCallStore.clear();
			
			if(o instanceof String) {
				o = sourceModel.getElementById(o.toString());
			}
			
			if (sourceParameter != null) {
				context.getFrameStack().enterLocal(FrameType.PROTECTED, getAst(), Variable.createReadOnlyVariable(sourceParameter.getName(), o));
			}
			else {
				context.getFrameStack().enterLocal(FrameType.PROTECTED, getAst());
			}
			
			
			if (preAst != null) context.getExecutorFactory().executeAST(preAst.getFirstChild(), context);
			
			boolean guard = true;
			if (guardAst != null) {
				Return r = (Return) context.getExecutorFactory().executeBlockOrExpressionAst(guardAst.getFirstChild(), context);
				Object value = r.getValue();
				if (!(value instanceof Boolean)) {
					throw new EolIllegalReturnException("Boolean", value, guardAst, context);
				}
				guard = (Boolean) value;
			}
			
			if(!guard) continue;
			
			if(((EgxModuleInc) module).isOfflineMode()) {
				if(needsToExecute(sourceModel, o, context)) continue;
			}
		
			boolean overwrite = true;
			if (overwriteAst != null) {
				Return r = (Return) context.getExecutorFactory().executeBlockOrExpressionAst(overwriteAst.getFirstChild(), context);
				Object value = r.getValue();
				if (!(value instanceof Boolean)) { 
					throw new EolIllegalReturnException("Boolean", value, overwriteAst, context);
				}
				overwrite = (Boolean) value;
			}

			boolean protectRegions = true;
			if (protectRegionsAst != null) {
				Return r = (Return) context.getExecutorFactory().executeBlockOrExpressionAst(protectRegionsAst.getFirstChild(), context);
				Object value = r.getValue();
				if (!(value instanceof Boolean)) {
					throw new EolIllegalReturnException("Boolean", value, protectRegionsAst, context);
				}
				protectRegions = (Boolean) value;
			}
			
			if (parametersAst != null) {
				Return r = (Return) context.getExecutorFactory().executeBlockOrExpressionAst(parametersAst.getFirstChild(), context);
				Object value = r.getValue();
				if (!(value instanceof EolMap)) {
					throw new EolIllegalReturnException("Map", value, parametersAst, context);
				}
				parameters = (EolMap) value;
			}
		
			if (templateAst != null) {
				Return r = (Return) context.getExecutorFactory().executeBlockOrExpressionAst(templateAst.getFirstChild(), context);
				template = r.getValue() + "";
			}
			
			String target = null;
			if (targetAst != null) {
				Return r = (Return) context.getExecutorFactory().executeBlockOrExpressionAst(targetAst.getFirstChild(), context);
				target = r.getValue() + "";
			}
			
			URI templateUri = templateFactory.resolveTemplate(template);
			
			if(!templateCache.containsKey(templateUri)){
				templateCache.put(templateUri, templateFactory.load(templateUri));
			}

			EglTemplate eglTemplate = templateCache.get(templateUri);
			
			if (sourceParameter != null) {
				eglTemplate.populate(sourceParameter.getName(), o);
			}

			for (Object key : parameters.keySet()) {
				eglTemplate.populate(key + "", parameters.get(key));
			}
			
			List<SignaturePropertyAccess> store = new ArrayList<SignaturePropertyAccess>();
			
			Signature signature = new ModelElementSignature(sourceModel.getElementId(o), store);
			signature.setObjRule(name);
			signature.setGeneratedFile(target);
			signature.setTemplateFile(template);
			
			if (eglTemplate instanceof EglFileGeneratingTemplate) {
				templateRecorder.startRecording();
				templateExecuting = true;
				((EglFileGeneratingTemplate) eglTemplate).generate(target, overwrite, protectRegions);
							
				templateRecorder.stopRecording();
				
				if(templateRecorder.getPropertyAccesses().isEmpty()) {
					SignaturePropertyAccess sigProp = new SignaturePropertyAccess(o, null);
					sigProp.setElementId(sourceModel.getElementId(o));
					sigProp.setPropertyValue(null);
					store.add(sigProp);	
				}
				else {				
					storeSignaturePropertyAccesses(context, sourceModel, templateRecorder, store, module);
				}
				
				templateExecuting = false;
				signMgr.addSignature(signature, name);
			}
			
			module.getInvokedTemplates().add(eglTemplate.getTemplate());

			if (postAst != null) context.getExecutorFactory().executeAST(postAst.getFirstChild(), context);

			context.getFrameStack().leaveLocal(getAst());
			
			eglTemplate.reset();
			
			addFeatureCalls(sourceModel, o, store);

			varMgr.addSignature(rulePreBlockSignature, name);
			
		}
	}

	private void addFeatureCalls(IModel sourceModel, Object o, List<SignaturePropertyAccess> store) {
		store.addAll(featureCallStore);
	}

	public void storeSignaturePropertyAccesses(IEglContext context, IModel sourceModel, IPropertyAccessRecorder templateRecorder, List<SignaturePropertyAccess> store, EgxModule module) throws EolRuntimeException {
		
		for (IPropertyAccess propertyAccess : templateRecorder.getPropertyAccesses().all()) {
			SignaturePropertyAccess sigProp = new SignaturePropertyAccess(propertyAccess.getModelElement(), propertyAccess.getPropertyName());
			sigProp.setElementId(sourceModel.getElementId(propertyAccess.getModelElement()));
			if(((EgxModuleInc)module).isOfflineMode())
				sigProp.setPropertyValue((calculatePartialSignature(sourceModel.getElementId(propertyAccess.getModelElement()), propertyAccess.getPropertyName(), sourceModel, context)));
			store.add(sigProp);			
		}

	}
	
	@Override
	public List<?> getChildren() {
		return Collections.emptyList();
	}
	
	@Override
	public String toString() {
		String label = this.name;
		if (sourceParameter != null) {
			label += " (" + sourceParameter.getTypeName() + ")";
		}
		return label;
	}

	public void deleteObsoleteFile(String generatedFile) {
		System.out.println(generatedFile + " deleted");
		
	}
	
}
