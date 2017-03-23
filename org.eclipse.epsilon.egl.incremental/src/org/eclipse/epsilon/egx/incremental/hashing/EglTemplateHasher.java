/*******************************************************************************
 * Copyright (c) 2014 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.egx.incremental.hashing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Formatter;

import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;

public class EglTemplateHasher implements IEglTemplateHasher {

	private final String algorithmName;
	
	public EglTemplateHasher() {
		this("SHA1");
	}
	
	public EglTemplateHasher(String algorithm) {
		this.algorithmName = algorithm;
	}
	
	@Override
	public EglTemplateHash hash(URI templateUri) throws EglRuntimeException {
		BufferedReader br = null;
		try {
			MessageDigest algorithm = MessageDigest.getInstance(algorithmName);
			
			// Prepare to read the template
			br = createReader(templateUri, algorithm);
			
			// Read template and construct result object
			String text = readContents(br);
			String hash = byteArray2Hex(algorithm.digest());
			EglTemplateHash result = new EglTemplateHash(hash, text);
			
			// Clean up
			br.close();
			
			return result;
			
		} catch (Exception e) {
			throw new EglRuntimeException("Error encountered whilst computing hash for template at: " + templateUri, e);
		}
	}


	private BufferedReader createReader(URI templateUri, MessageDigest algorithm) throws IOException {
		final InputStream stream = templateUri.toURL().openStream();
		final DigestInputStream hashComputingStream = new DigestInputStream(stream, algorithm);
		return new BufferedReader(new InputStreamReader(hashComputingStream));
	}
	
	private String readContents(BufferedReader reader) throws IOException {
		final StringBuilder contents = new StringBuilder();
		
		String line;
		while ((line = reader.readLine()) != null) {
			contents.append(line);
		}
		
		return contents.toString();
	}
	
	private String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        
        String results = formatter.toString();
        formatter.close();
		return results;
    }
	
	public static void main(String[] args) throws Exception {
		EglTemplateHash result = new EglTemplateHasher().hash(EglTemplateHasher.class.getResource("test.egl").toURI());
		System.out.println(result.getText());
		System.out.println(result.getValue());
	}
}
