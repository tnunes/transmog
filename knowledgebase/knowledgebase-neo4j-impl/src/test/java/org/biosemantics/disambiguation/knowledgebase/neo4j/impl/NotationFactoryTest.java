package org.biosemantics.disambiguation.knowledgebase.neo4j.impl;

import junit.framework.Assert;

import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.service.NotationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from files in the root of the classpath
@ContextConfiguration({ "/knowledgebase-neo4j-impl-context.xml" })
public class NotationFactoryTest {
	private static final String C54321 = "C54321";
	private static final String C0012345 = "C0012345";

	@Autowired
	private NotationService notationFactory;

	
	@Test(expected=IllegalArgumentException.class)
	public void testNullDomain(){
		notationFactory.createNotation(null, C0012345);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullCode(){
		notationFactory.createNotation(Domain.MTH, null);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAllNullInput(){
		notationFactory.createNotation(null, null);
	}
	
	@Test
	public void testCreateNotation(){
		Notation notation =  notationFactory.createNotation(Domain.MTH, C0012345);
		Assert.assertNotNull(notation);
	}
	
	@Test
	public void testCreateNotationEquals(){
		Notation notationFirst = notationFactory.createNotation(Domain.MTH, C54321);
		Notation notationSecond = notationFactory.createNotation(Domain.MTH, C54321);
		Notation notationThird = notationFactory.createNotation(Domain.MTH, C0012345);
		Assert.assertEquals(notationFirst, notationSecond);
		Assert.assertNotSame(notationFirst, notationThird);
	}
	
	

}
