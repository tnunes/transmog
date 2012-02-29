package org.biosemantics.wsd;

import static org.junit.Assert.assertEquals;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.ConceptType;
import org.biosemantics.wsd.repository.ConceptRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"conceptstore-context.xml"})
public class ConceptStoreTests {

	@Autowired
	 private Neo4jTemplate template;
	
	@Autowired
	private ConceptRepository conceptRepository;

	@Test
	@Transactional
	public void persistedMovieShouldBeRetrievableFromGraphDb() {
		Concept concept = template.save(new Concept("dummyId", ConceptType.CONCEPT));
		Concept retrievedConcept = template.findOne(concept.getNodeId(), Concept.class);
		assertEquals("retrieved movie matches persisted one", concept, retrievedConcept);
		assertEquals("retrieved movie title matches", "dummyId", retrievedConcept.getId());
	}

	@Test
	@Transactional
	public void persistedMovieShouldBeRetrievableFromGraphDbByIndex() {
		String id = "someId";
		Concept forrestGump = template.save(new Concept(id, ConceptType.CONCEPT));
		GraphRepository<Concept> conceptRepository = template.repositoryFor(Concept.class);
		Concept retrievedConcept = conceptRepository.findByPropertyValue("id", id);
		assertEquals("retrieved concept matches persisted one", forrestGump, retrievedConcept);
		assertEquals("retrieved concept uuid matches", id, retrievedConcept.getId());
	}
	
	@Test
	@Transactional
	public void persistedMovieShouldBeRetrievableFromGraphDbUsingRepo() {
		String id = "someId";
		Concept forrestGump = conceptRepository.save(new Concept(id, ConceptType.CONCEPT));
		Concept retrievedConcept = conceptRepository.getConceptById(id);
		assertEquals("retrieved concept matches persisted one", forrestGump, retrievedConcept);
		assertEquals("retrieved concept uuid matches", id, retrievedConcept.getId());
	}
}
