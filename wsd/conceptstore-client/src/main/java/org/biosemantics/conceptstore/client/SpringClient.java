package org.biosemantics.conceptstore.client;

import org.biosemantics.conceptstore.repository.*;
import org.springframework.context.support.*;
import org.springframework.data.neo4j.support.*;

/**
 * Client class to access the knowledge base as a java library. It is an enum to
 * enforce the singleton pattern (see: effective java). Creating multiple
 * instances of this class is not needed. Call the initGraph() method before
 * making any other calls. initGraph() loads the spring context and initializes
 * the valiables with the requisite repositories
 * 
 * @author bhsingh
 * 
 */
public enum SpringClient {

	INSTANCE;

	private static final String[] CONTEXT_FILES = new String[] { "conceptstore-client-context.xml" };
	private ClassPathXmlApplicationContext applicationContext;
	private ConceptRepository conceptRepository;
	private LabelRepository labelRepository;
	private NotationRepository notationRepository;
	private Neo4jTemplate neo4jTemplate;

	/**
	 * Loads the graph database from the spring context file
	 */
	public void initGraph() {
		/*
		 * load the spring configuration from the xml file
		 * "conceptstore-context.xml" located in "src/main/resources"
		 */
		applicationContext = new ClassPathXmlApplicationContext(CONTEXT_FILES);
		/*
		 * registers a shutdown hook so that the application context is stopped
		 * with the java process. Avoids curropting the graph database
		 */
		applicationContext.registerShutdownHook();
		/*
		 * respositories retrieved from spring
		 */
		conceptRepository = applicationContext.getBean(ConceptRepository.class);
		labelRepository = applicationContext.getBean(LabelRepository.class);
		notationRepository = applicationContext.getBean(NotationRepository.class);
		neo4jTemplate = applicationContext.getBean(Neo4jTemplate.class);
	}

	public ClassPathXmlApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public ConceptRepository getConceptRepository() {
		return conceptRepository;
	}

	public LabelRepository getLabelRepository() {
		return labelRepository;
	}

	public NotationRepository getNotationRepository() {
		return notationRepository;
	}

	public Neo4jTemplate getNeo4jTemplate() {
		return neo4jTemplate;
	}

	/**
	 * Example code to talk to concept store 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringClient.INSTANCE.initGraph();
		ConceptRepository conceptRepository = SpringClient.INSTANCE.getConceptRepository();

		/*
		 * do something here
		 */

		// when complete call (not required, it will be automatically called
		// when the java process shuts down)
		SpringClient.INSTANCE.getApplicationContext().close();
	}
}
