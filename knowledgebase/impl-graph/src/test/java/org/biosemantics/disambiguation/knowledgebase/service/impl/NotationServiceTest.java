package org.biosemantics.disambiguation.knowledgebase.service.impl;

import junit.framework.Assert;

import org.biosemantics.disambiguation.knowledgebase.AbstractTransactionalDataSource;
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
@ContextConfiguration({ "/knowledgebase-test-context.xml" })
public class NotationServiceTest extends AbstractTransactionalDataSource{
	private static final String C54321 = "C54321";
	private static final String C0012345 = "C0012345";

	@Autowired
	private NotationService notationService;

	
	@Test(expected=IllegalArgumentException.class)
	public void testNullDomain(){
		notationService.createNotation(null, C0012345);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullCode(){
		notationService.createNotation(Domain.MTH, null);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAllNullInput(){
		notationService.createNotation(null, null);
	}
	
	@Test
	public void testCreateNotation(){
		Notation notation =  notationService.createNotation(Domain.MTH, C0012345);
		Assert.assertNotNull(notation);
	}
	
	@Test
	public void testCreateNotationEquals(){
		Notation notationFirst = notationService.createNotation(Domain.MTH, C54321);
		Notation notationSecond = notationService.createNotation(Domain.MTH, C54321);
		Notation notationThird = notationService.createNotation(Domain.MTH, C0012345);
		Assert.assertEquals(notationFirst, notationSecond);
		Assert.assertNotSame(notationFirst, notationThird);
	}
	
	

}
