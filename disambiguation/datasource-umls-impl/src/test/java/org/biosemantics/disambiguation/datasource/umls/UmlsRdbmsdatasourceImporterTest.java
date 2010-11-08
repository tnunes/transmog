package org.biosemantics.disambiguation.datasource.umls;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from files in the root of the classpath
@ContextConfiguration({ "/datasource-umls-impl-test-context.xml" })
public class UmlsRdbmsdatasourceImporterTest {

	@Autowired
	private UmlsRdbmsDatasourceImporter umlsRdbmsDatasourceImporter;
	
	@Test
	public void test(){
		
	}
}
