package org.eclipse.epsilon.egx.live;

import org.eclipse.epsilon.egx.signaturepropertyaccess.SignaturePropertyAccess;


public class ChangeData extends SignaturePropertyAccess {
	
	private String modificationType;
	private String elementId;

	public ChangeData(String objId, String propName, String type) {
		super(type, propName);
		elementId = objId;
	}
	
	public void setModificationType(String mType) {
		modificationType = mType;
	}
	
	public void setElementType(Object type) {
		modelElement = type;
	}
	
	public Object getElementType() {
		return modelElement;
	}

	public String getObjectId() {
		return elementId;
	}

	public String getPropertyName() {
		return propertyName;
	}
	
	public String getModificationType() {
		return modificationType;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

	    result.append(this.getClass().getName());
	    result.append(" {");
	    result.append("object id: ");
	    result.append(elementId);
	    result.append(" property: ");
	    result.append(propertyName);
	    result.append(" modification type: ");
	    result.append(modificationType);
	    result.append(" }");
	    
	    return result.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof ChangeData))
			return false;
		
		return (((ChangeData)other).elementId.equals(this.elementId) && ((ChangeData)other).propertyName == propertyName && ((ChangeData)other).modificationType == modificationType);
	}
	
	@Override
	public int hashCode() {
	    int hashCode = 1;

	    hashCode = hashCode * 37 + elementId.hashCode();
	    hashCode = hashCode * 37 + (null == propertyName ? 0 : propertyName.hashCode());
	    hashCode = hashCode * 37 + (null == modificationType ? 0 : modificationType.hashCode());

	    return hashCode;
	}

}