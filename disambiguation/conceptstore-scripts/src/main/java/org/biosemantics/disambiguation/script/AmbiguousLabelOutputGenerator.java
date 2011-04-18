package org.biosemantics.disambiguation.script;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AmbiguousLabelOutputGenerator {

	private static final String[] CONTEXTS = new String[] { "conceptstore-scripts-test-context.xml" };

	public static void main(String[] args) {
		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(CONTEXTS);
		AmbiguousLabelScript ambiguousLabelScript = classPathXmlApplicationContext.getBean(AmbiguousLabelScript.class);
		ambiguousLabelScript.writeAmbiguousLabels();
		classPathXmlApplicationContext.close();
	}

}
