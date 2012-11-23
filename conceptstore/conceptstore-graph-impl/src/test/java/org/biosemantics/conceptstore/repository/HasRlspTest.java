package org.biosemantics.conceptstore.repository;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.HasLabel;
import org.biosemantics.conceptstore.domain.HasRlsp;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.impl.ConceptType;
import org.biosemantics.conceptstore.domain.impl.LabelType;
import org.biosemantics.conceptstore.repository.impl.ConceptRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.LabelRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

public class HasRlspTest {
	protected GraphDatabaseService graphDb;
	protected LabelRepository labelRepository;
	protected ConceptRepository conceptRepository;

	/**
	 * Create temporary database for each unit test.
	 */
	@Before
	public void prepareTestDatabase() {
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
		labelRepository = new LabelRepositoryImpl(graphDb);
		conceptRepository = new ConceptRepositoryImpl(graphDb);
	}

	/**
	 * Shutdown the database.
	 */
	@After
	public void destroyTestDatabase() {
		graphDb.shutdown();
	}

	@Test
	public void hasRlspShouldBeRetrieved() {
		Concept concept1 = conceptRepository.create(ConceptType.CONCEPT);
		Concept concept2 = conceptRepository.create(ConceptType.CONCEPT);
		HasRlsp hasRlsp = conceptRepository.hasRlspIfNoneExists(concept1.getId(), concept2.getId(), "xxx", "src1");
		Collection<HasRlsp> foundRlsps = conceptRepository.getAllHasRlspsForConcept(concept1.getId());
		assertNotNull(foundRlsps);
		assertEquals(1, foundRlsps.size());
		for (HasRlsp found : foundRlsps) {
			assertEquals(hasRlsp, found);
		}
	}

	@Test
	public void sourcesShouldBeRetrieved() {
		Concept concept1 = conceptRepository.create(ConceptType.CONCEPT);
		Concept concept2 = conceptRepository.create(ConceptType.CONCEPT);
		String[] sources = new String[] { "src2", "some" };
		HasRlsp hasRlsp = conceptRepository.hasRlspIfNoneExists(concept1.getId(), concept2.getId(), "xxx", sources);
		Collection<HasRlsp> foundRlsps = conceptRepository.getAllHasRlspsForConcept(concept1.getId());
		for (HasRlsp found : foundRlsps) {
			for (String src : found.getSources()) {
				assertTrue(Arrays.asList(sources).contains(src));
			}

		}
	}

	@Test
	public void startAndEndConceptShouldBeRetrieved() {
		Concept concept1 = conceptRepository.create(ConceptType.CONCEPT);
		Concept concept2 = conceptRepository.create(ConceptType.CONCEPT);
		Concept concept3 = conceptRepository.create(ConceptType.CONCEPT);
		String[] sources = new String[] { "src2", "some" };
		HasRlsp hasRlsp1 = conceptRepository.hasRlspIfNoneExists(concept1.getId(), concept2.getId(), "xxx", sources);
		HasRlsp hasRlsp2 = conceptRepository.hasRlspIfNoneExists(concept2.getId(), concept3.getId(), "xxx", sources);
		assertEquals(concept1, hasRlsp1.getStartConcept());
		assertEquals(concept2, hasRlsp1.getEndConcept());
		assertEquals(concept2, hasRlsp2.getStartConcept());
		assertEquals(concept3, hasRlsp2.getEndConcept());
	}

	@Test
	public void otherConceptShouldBeRetrieved() {
		Concept concept1 = conceptRepository.create(ConceptType.CONCEPT);
		Concept concept2 = conceptRepository.create(ConceptType.CONCEPT);
		Concept concept3 = conceptRepository.create(ConceptType.CONCEPT);
		String[] sources = new String[] { "src2", "some" };
		HasRlsp hasRlsp1 = conceptRepository.hasRlspIfNoneExists(concept1.getId(), concept2.getId(), "xxx", sources);
		HasRlsp hasRlsp2 = conceptRepository.hasRlspIfNoneExists(concept2.getId(), concept3.getId(), "xxx", sources);
		assertEquals(concept1, hasRlsp1.getOtherConcept(concept2.getId()));
		assertEquals(concept2, hasRlsp1.getOtherConcept(concept1.getId()));
		assertEquals(concept2, hasRlsp2.getOtherConcept(concept3.getId()));
		assertEquals(concept3, hasRlsp2.getOtherConcept(concept2.getId()));
	}

	@Test
	public void predicateLabelsShouldBeRetrieved() {
		Concept concept1 = conceptRepository.create(ConceptType.CONCEPT);
		Concept concept2 = conceptRepository.create(ConceptType.CONCEPT);
		Concept concept3 = conceptRepository.create(ConceptType.PREDICATE);
		Label label3 = labelRepository.create("pred1", "lang1");
		String[] sources = new String[] { "src2", "some" };
		conceptRepository.hasLabel(concept3.getId(), label3.getId(), LabelType.ALTERNATE, sources);

		HasRlsp hasRlsp1 = conceptRepository.hasRlspIfNoneExists(concept1.getId(), concept2.getId(),
				"" + concept3.getId(), sources);
		Collection<Label> labels = hasRlsp1.getLabels();
		assertNotNull(labels);
		assertEquals(1, labels.size());
		for (Label found : labels) {
			assertEquals(label3, found);
		}

	}
}
