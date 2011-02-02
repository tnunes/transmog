package org.biosemantics.disambiguation.conceptstore.rmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RmiExporter {

	private static final String[] CONTEXT = new String[] { "conceptstore-rmi-context.xml" };
	private static final Logger logger = LoggerFactory.getLogger(RmiExporter.class);

	public static void main(String[] args) {
		logger.info("loding RMI configuration from {}", CONTEXT);
		new ClassPathXmlApplicationContext(CONTEXT);
		logger.info("RMI service started. See logs for RMI port information.");
	}

}
