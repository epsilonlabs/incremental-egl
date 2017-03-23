package org.eclipse.epsilon.egx.signaturepropertyaccess;

import org.eclipse.epsilon.eol.execute.introspection.recording.PropertyAccess;

public class SignaturePropertyAccess extends PropertyAccess {

	protected String propertyValue;
	private String spa_id;
	
	public SignaturePropertyAccess(Object modelElement, String propertyName) {
		super(modelElement, propertyName);
	}	
	
	public SignaturePropertyAccess(String spaId, String elementId, String propertyName) {
		super(elementId, propertyName);
		this.spa_id = spaId;
	}
	
	public String getSpaId() {
		return spa_id;
	}

	public void setPropertyValue(String propVal) {
		propertyValue = propVal;
	}
	
	public void setElementId(String element_id) {
		modelElement = element_id;
	}
	
	public String getPropertyValue() {
		return propertyValue;
	}
	
	public String getElementId() {
		return modelElement.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

	    result.append(this.getClass().getName());
	    result.append(" {");
	    result.append("object id: ");
	    result.append(modelElement);
	    result.append(" property: ");
	    result.append(propertyName);
	    result.append(" }");
	    
	    return result.toString();
	}
	
	/*
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof SignaturePropertyAccess))
			return false;
		
		return (((SignaturePropertyAccess)other).modelElement.equals(this.modelElement) && ((SignaturePropertyAccess)other).propertyName == propertyName);
	}
	
	@Override
	public int hashCode() {
	    int hashCode = 1;

	    hashCode = hashCode * 37 + modelElement.hashCode();
	    hashCode = hashCode * 37 + (null == propertyName ? 0 : propertyName.hashCode());

	    return hashCode;
	}
	*/
	
	/*
	public Object getValueFromPropertyAccess(IPropertyAccess propertyAccess, IEolContext context) throws EolRuntimeException {
		IPropertyGetter getter = context.getIntrospectionManager().getPropertyGetterFor(propertyAccess.getModelElement(), propertyAccess.getPropertyName(), context);
		return getter.invoke(propertyAccess.getModelElement(), propertyAccess.getPropertyName());
	}
	*/
	
}
