package org.biosemantics.wsd.script;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ScriptApp {
	
	private static final String[] CONTEXT = new String[]{"script-run-context.xml"};
	
	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXT);
		applicationContext.registerShutdownHook();
//		AmbiguousTermFrequencyGenerator ambiguousTermFrequencyGenerator = applicationContext.getBean(AmbiguousTermFrequencyGenerator.class);
//		ambiguousTermFrequencyGenerator.writeAll();
//		MaxRelatedConceptGenerator maxRelatedConceptGenerator = applicationContext.getBean(MaxRelatedConceptGenerator.class);
//		maxRelatedConceptGenerator.writeAll();
		RelatedConceptWriter relatedConceptWriter = applicationContext.getBean(RelatedConceptWriter.class);
		relatedConceptWriter.writeAll();
	}

}
