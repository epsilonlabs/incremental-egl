package org.eclipse.epsilon.egx.signature.managers;

import org.eclipse.epsilon.egx.signatures.Signature;


public interface ISignatureManager {	
	
	public Signature getSignature(String identifier, String rule) throws Exception;
	
	public void addSignature(Signature s, String rule);
	
	public void setConfiguration(String configurationName);
	
	public void dispose();
	
}
