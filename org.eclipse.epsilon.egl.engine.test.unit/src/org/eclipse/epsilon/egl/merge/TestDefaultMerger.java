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
package org.eclipse.epsilon.egl.merge;

import static org.eclipse.epsilon.egl.util.FileUtil.NEWLINE;
import static org.junit.Assert.*;

import org.eclipse.epsilon.egl.merge.partition.CommentBlockPartitioner;
import org.eclipse.epsilon.egl.merge.partition.Partitioner;
import org.junit.Test;

public class TestDefaultMerger {
	
	private final Partitioner partitioner = new CommentBlockPartitioner("<!--", "-->");
	
	private String generated = "<!-- protected region anId on begin -->" + NEWLINE +
	                           "This text is generated." + NEWLINE +
	                           "<!-- protected region anId end -->";
	
	private String existing = "<!-- protected region anId on begin -->" + NEWLINE +
                              "This text is preserved." + NEWLINE +
                              "<!-- protected region anId end -->";
	
	private String turnOff(String pr) {
		return pr.replace(" on ", " off ");
	}

	@Test
	public void testOnAndOn() {
		final Merger merger = new DefaultMerger(partitioner, generated, existing);
		
		assertEquals(existing, merger.merge());
		assertEquals(0, merger.getMergeWarnings().size());
	}
	
	@Test
	public void testOnAndOff() {
		final Merger merger = new DefaultMerger(partitioner, generated, turnOff(existing));
		
		assertEquals(generated, merger.merge());
		assertEquals(0, merger.getMergeWarnings().size());
	}
	
	@Test
	public void testOnAndAbsent() {
		final Merger merger = new DefaultMerger(partitioner, generated, "");
		
		assertEquals(generated, merger.merge());
		assertEquals(0, merger.getMergeWarnings().size());
	}
	

	@Test
	public void testOffAndOn() {
		final Merger merger = new DefaultMerger(partitioner, turnOff(generated), existing);
		
		assertEquals(existing, merger.merge());
		assertEquals(0, merger.getMergeWarnings().size());
	}
	
	@Test
	public void testOffAndOff() {
		final Merger merger = new DefaultMerger(partitioner, turnOff(generated), turnOff(existing));
		
		assertEquals(turnOff(generated), merger.merge());
		assertEquals(0, merger.getMergeWarnings().size());
	}
	
	@Test
	public void testOffAndAbsent() {
		final Merger merger = new DefaultMerger(partitioner, turnOff(generated), "");
		
		assertEquals(turnOff(generated), merger.merge());
		assertEquals(0, merger.getMergeWarnings().size());
	}
	
	
	@Test
	public void testAbsentAndOn() {
		final Merger merger = new DefaultMerger(partitioner, "", existing);
		
		assertEquals("", merger.merge());
		assertEquals(1, merger.getMergeWarnings().size());
	}
	
	@Test
	public void testAbsentAndOff() {
		final Merger merger = new DefaultMerger(partitioner, "", turnOff(existing));
		
		assertEquals("", merger.merge());
		assertEquals(1, merger.getMergeWarnings().size());
	}
	
	@Test
	public void testAbsentAndAbsent() {
		final Merger merger = new DefaultMerger(partitioner, "", "");
		
		assertEquals("", merger.merge());
		assertEquals(0, merger.getMergeWarnings().size());
	}
}
