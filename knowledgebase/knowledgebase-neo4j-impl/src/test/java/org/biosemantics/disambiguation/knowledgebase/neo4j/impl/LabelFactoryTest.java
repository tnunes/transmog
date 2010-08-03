package org.biosemantics.disambiguation.knowledgebase.neo4j.impl;

import junit.framework.Assert;

import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.LabelService;
import org.biosemantics.disambiguation.knowledgebase.service.Language;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//ApplicationContext will be loaded from files in the root of the classpath
@ContextConfiguration({ "/knowledgebase-neo4j-impl-context.xml" })
public class LabelFactoryTest {
	
	@Autowired
	private LabelService labelFactory;
	private static final String PREF_LBL = "PREF_LBL";
	private static final String ALT_LBL = "ALT_LBL";

	@Test(expected=IllegalArgumentException.class)
	public void testNullLanguage(){
		labelFactory.createPreferredLabel(PREF_LBL, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullText(){
		labelFactory.createPreferredLabel(null, Language.EN);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAllNullInput(){
		labelFactory.createPreferredLabel(null, null);
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullLanguageAlt(){
		labelFactory.createAlternateLabel(ALT_LBL, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullTextAlt(){
		labelFactory.createAlternateLabel(null, Language.EN);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAllNullInputAlt(){
		labelFactory.createAlternateLabel(null, null);
	}
	
	@Test
	public void testCreateLabel(){
		Label Label =  labelFactory.createPreferredLabel(PREF_LBL, Language.EN);
		Assert.assertNotNull(Label);
	}
	
	@Test
	public void testCreateLabelEquals(){
		Label labelFirst = labelFactory.createPreferredLabel(PREF_LBL, Language.EN);
		Label labelSecond = labelFactory.createPreferredLabel(PREF_LBL, Language.EN);
		Label labelThird = labelFactory.createPreferredLabel(ALT_LBL, Language.EN);
		Assert.assertEquals(labelFirst, labelSecond);
		Assert.assertNotSame(labelFirst, labelThird);
	}
	

}
