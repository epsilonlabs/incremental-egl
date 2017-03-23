/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.egl.config;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.execute.context.EglContext;
import org.eclipse.epsilon.egl.merge.partition.CommentBlockPartitioner;
import org.eclipse.epsilon.egl.merge.partition.CompositePartitioner;
import org.eclipse.epsilon.common.util.FileUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestXMLContentTypeRepository {

	private final ContentTypeRepository repository = new XMLContentTypeRepository(new EglContext(new EglTemplateFactory()));
	
	private static String invalidPath;
	private static String nonExistentPath;
	private static String valid;
	
	
	@BeforeClass
	public static void setUpOnce() {
		invalidPath     = FileUtil.getPath("In*alid.xml", TestXMLContentTypeRepository.class);
		nonExistentPath = FileUtil.getPath("Absent.xml", TestXMLContentTypeRepository.class);
		valid           = FileUtil.getPath("Valid.xml", TestXMLContentTypeRepository.class);
	}
	
	
	private void load(String path) throws FileNotFoundException, PersistenceException {
		repository.load(new FileInputStream(new File(path)));
	}
	
	@Test (expected=FileNotFoundException.class)
	public void readInvalid() throws PersistenceException, FileNotFoundException {
		load(invalidPath);
	}
	
	@Test (expected=FileNotFoundException.class)
	public void readNonExistent() throws PersistenceException, FileNotFoundException {
		load(nonExistentPath);
	}
	
	@Test
	public void readValid() throws PersistenceException, FileNotFoundException {
		load(valid);		
		
		assertEquals(new CompositePartitioner(new CommentBlockPartitioner("//", null),
		                                      new CommentBlockPartitioner("/*", "*/")),
		             repository.partitionerFor("Java"));

		
		assertEquals(new CompositePartitioner(new CommentBlockPartitioner("<!--", "-->")),
		             repository.partitionerFor("HTML"));
		
		assertEquals(new CompositePartitioner(new CommentBlockPartitioner("--", null)),
				     repository.partitionerFor("EGL"));
		
		assertEquals(new CompositePartitioner(new CommentBlockPartitioner("'", null)),
		             repository.partitionerFor("VisualBasic"));
		
	}
	
	@Test
	public void testDefaultConfig() throws PersistenceException, FileNotFoundException {
		final ContentTypeRepository repository = new XMLContentTypeRepository(new EglContext(new EglTemplateFactory()));		
		
		assertEquals(new CompositePartitioner(new CommentBlockPartitioner("//", null),
		                                      new CommentBlockPartitioner("/*", "*/")),
		             repository.partitionerFor("Java"));

		
		assertEquals(new CompositePartitioner(new CommentBlockPartitioner("<!--", "-->")),
		             repository.partitionerFor("HTML"));
		
		assertEquals(new CompositePartitioner(new CommentBlockPartitioner("--", null)),
				     repository.partitionerFor("EGL"));
		
		assertEquals(new CompositePartitioner(new CommentBlockPartitioner("'", null)),
		             repository.partitionerFor("VisualBasic"));
		
		assertEquals(new CompositePartitioner(new CommentBlockPartitioner("#", null)),
		             repository.partitionerFor("Perl"));
		
	}
	
	@Test
	public void readUndefinedContentType() throws PersistenceException, FileNotFoundException {
		load(valid);		
		
		assertNull(repository.partitionerFor("UndefinedContentType"));	
	}
	
	@Test
	public void readNullContentType() throws PersistenceException, FileNotFoundException {
		load(valid);		
		
		assertNull(repository.partitionerFor(null));	
	}
}
