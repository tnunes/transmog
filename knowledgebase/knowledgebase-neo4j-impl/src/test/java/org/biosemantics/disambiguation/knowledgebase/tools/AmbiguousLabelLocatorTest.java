package org.biosemantics.disambiguation.knowledgebase.tools;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//ApplicationContext will be loaded from files in the root of the classpath
@ContextConfiguration({ "/knowledgebase-neo4j-impl-context.xml" })
public class AmbiguousLabelLocatorTest {
	
	@Autowired
	private AmbiguousLabelLocator ambiguousLabelLocator;
	
	
	@Test
	public void testLocateAmbiguousLabels(){
		ambiguousLabelLocator.locateAmbiguousLabels();
	}

}
