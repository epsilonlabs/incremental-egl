package org.eclipse.epsilon.egx.signature.managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.epsilon.egx.signatures.Signature;
import org.w3c.dom.Document;

public class EglXmlSignatureManager implements ISignatureManager {
	
	private Document doc = null;
	private String configurationName = "";
	private Map<String, HashMap<String,Signature>> allSignatures = new HashMap<String, HashMap<String,Signature>>();



	private void writeSignatures(String rule)
			throws Exception {
		
		String fileName = rule + configurationName;
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer;
	        
		try {
			transformer = transformerFactory.newTransformer();

	        DOMSource source = new DOMSource(doc);
	        StreamResult result = new StreamResult(new File("/Users/jimmysyl/Desktop/kepler/eclipse/workspace/" + fileName + ".xml"));

	        transformer.transform(source, result);
	        
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}	
	}

	private void configureSignatureStore(String rule)
			throws Exception {
		
		//String fileName = rule + configurationName;
		/*
		Map<String,Signature> sigMap = new HashMap<String,Signature>(); 
		sigMap.putAll(allSignatures.get(rule));
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	    
	    doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("EglObjects");
        doc.appendChild(rootElement);      
	    
        for (String key : sigMap.keySet()){
        	 // objects
	        Element obj = doc.createElement("object");
	        rootElement.appendChild(obj);
			
			// object id
			Attr attr = doc.createAttribute("id");
			attr.setValue(sigMap.get(key).getObjId());
			obj.setAttributeNode(attr);
			
			// signature
			Element signatureNode = doc.createElement("signature");
			signatureNode.appendChild(doc.createTextNode(sigMap.get(key).getObjSignatureValues()));
			obj.appendChild(signatureNode);
        }
        
        /*
		for(int i = 0; i < list.size(); i++){
			 // objects
	        Element obj = doc.createElement("object");
	        rootElement.appendChild(obj);
			
			// object id
			Attr attr = doc.createAttribute("id");
			attr.setValue(list.get(i).getObjId());
			obj.setAttributeNode(attr);
			
			// signature
			Element signatureNode = doc.createElement("signature");
			signatureNode.appendChild(doc.createTextNode(list.get(i).getObjSignatureValue()));
			obj.appendChild(signatureNode);
		}
		*/
		
		writeSignatures(rule);
		
	}

	@Override
	public Signature getSignature(String objectId, String rule) throws Exception {
		/*
		String fileName = rule + configurationName;
		File file = new File("/Users/jimmysyl/Desktop/kepler/eclipse/workspace/" + fileName + ".xml");
		
		if(allSignatures.get(rule) == null){
			allSignatures.put(rule, new HashMap<String, Signature>());
			if(file.exists())
			{
				try {
		        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		        	DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		        	Document doc = docBuilder.parse(file);
		        	doc.getDocumentElement().normalize();
		        	
		        	NodeList nodeList = doc.getElementsByTagName("object");
		            
		        	for (int temp = 0; temp < nodeList.getLength(); temp++) {
		        		 Node node = nodeList.item(temp);
		        		 
		        		 if (node.getNodeType() == Node.ELEMENT_NODE) {
		        			 Element eElement = (Element)node;
		        			 String obj_id = eElement.getAttribute("id");
		        			 Signature s = new Signature(obj_id, rule, eElement.getElementsByTagName("signature").item(0).getTextContent());
		        			 allSignatures.get(rule).put(obj_id, s);
		        			 //allSignatureList.add(s);
		        			 }
		        		 }
		        	} catch (Exception e) {
		        	e.printStackTrace();
		            }
			}
		}
		*/
		return (Signature)allSignatures.get(rule).get(objectId);
		
	}

	@Override
	public void addSignature(Signature s, String rule) {
		if(allSignatures.get(rule) == null){
			allSignatures.put(rule, new HashMap<String, Signature>());		
		}
		allSignatures.get(rule).put(((Signature) s).getObjId(), s);
	}


	@Override
	public void setConfiguration(String configurationName) {
		this.configurationName = configurationName;
		
	}


	@Override
	public void dispose() {
		for(String rule : allSignatures.keySet()){
			try {
				configureSignatureStore(rule);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public HashMap<String, Signature> getAllSignaturesForRule(String rule) {
		
		return null;
	}

}
