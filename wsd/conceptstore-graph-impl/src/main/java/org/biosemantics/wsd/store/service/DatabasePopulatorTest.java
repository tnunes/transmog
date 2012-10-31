package org.biosemantics.wsd.store.service;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.ConceptType;
import org.biosemantics.wsd.repository.ConceptRepository;
import org.neo4j.graphdb.Transaction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.Transactional;

public class DatabasePopulatorTest {

	private static final int POP_COUNT_EVEN = 10;

	private Neo4jTemplate template;
	private ConceptRepository conceptRepository;

	public DatabasePopulatorTest(ApplicationContext applicationContext) {
		template = applicationContext.getBean(Neo4jTemplate.class);
		conceptRepository = applicationContext.getBean(ConceptRepository.class);
	}

	@Transactional
	public void populateConcepts() {
		Transaction tx = template.getGraphDatabaseService().beginTx();
		Concept concept1 = new Concept("" + 0, ConceptType.CONCEPT);
		Concept saved1 = template.save(concept1);
		for (int i = 1; i < POP_COUNT_EVEN; i++) {
			Concept concept2 = new Concept("" + i, ConceptType.CONCEPT);
			Concept saved2 = template.save(concept2);
			saved1.relatedTo(template, saved2, 100, "ASSOCIATED WITH", "UMLS 2011 AA");

		}
		tx.success();
		tx.finish();
		String id = "" + 0;
		Concept retrievedConcept = conceptRepository.findByPropertyValue("id", id);
		Iterable<Concept> concepts = conceptRepository.getRelatedConcepts(retrievedConcept);
		for (Concept concept : concepts) {
			System.err.println(concept);
		}
	}

	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"/org/biosemantics/wsd/conceptstore-context.xml");
		DatabasePopulatorTest databasePopulatorTest = new DatabasePopulatorTest(applicationContext);
		databasePopulatorTest.populateConcepts();
	}

}
