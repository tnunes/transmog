package org.biosemantics.wsd.script;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ScriptApp {
	
	private static final String[] CONTEXT = new String[]{"script-run-context.xml"};
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXT);
		applicationContext.registerShutdownHook();
//		AmbiguousTermFrequencyGenerator ambiguousTermFrequencyGenerator = applicationContext.getBean(AmbiguousTermFrequencyGenerator.class);
//		ambiguousTermFrequencyGenerator.writeAll();
		ConnectedConceptFrequencyGenerator conceptFrequencyGenerator = applicationContext.getBean(ConnectedConceptFrequencyGenerator.class);
		conceptFrequencyGenerator.writeAll();
	}

}
