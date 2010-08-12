package org.biosemantics.disambiguation.knowledgebase.service.impl;

import junit.framework.Assert;

import org.biosemantics.disambiguation.knowledgebase.AbstractTransactionalDataSource;
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
@ContextConfiguration({ "/knowledgebase-test-context.xml" })
public class LabelServiceTest extends AbstractTransactionalDataSource {
	
	@Autowired
	private LabelService labelService;
	private static final String PREF_LBL = "PREF_LBL";
	private static final String ALT_LBL = "ALT_LBL";

	@Test(expected=IllegalArgumentException.class)
	public void testNullLanguage(){
		labelService.createPreferredLabel(PREF_LBL, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullText(){
		labelService.createPreferredLabel(null, Language.EN);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAllNullInput(){
		labelService.createPreferredLabel(null, null);
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullLanguageAlt(){
		labelService.createAlternateLabel(ALT_LBL, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullTextAlt(){
		labelService.createAlternateLabel(null, Language.EN);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAllNullInputAlt(){
		labelService.createAlternateLabel(null, null);
	}
	
	@Test
	public void testCreateLabel(){
		Label Label =  labelService.createPreferredLabel(PREF_LBL, Language.EN);
		Assert.assertNotNull(Label);
	}
	
	@Test
	public void testCreateLabelEquals(){
		Label labelFirst = labelService.createPreferredLabel(PREF_LBL, Language.EN);
		Label labelSecond = labelService.createPreferredLabel(PREF_LBL, Language.EN);
		Label labelThird = labelService.createPreferredLabel(ALT_LBL, Language.EN);
		Assert.assertEquals(labelFirst, labelSecond);
		Assert.assertNotSame(labelFirst, labelThird);
	}
	

}
