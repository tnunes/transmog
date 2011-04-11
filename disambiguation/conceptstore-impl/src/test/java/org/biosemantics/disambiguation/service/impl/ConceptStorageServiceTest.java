package org.biosemantics.disambiguation.service.impl;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptLabelImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.biosemantics.disambiguation.service.local.ConceptStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.LabelStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.NotationStorageServiceLocal;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConceptStorageServiceTest extends AbstractTransactionalDataSource {

	@Autowired
	private ConceptStorageServiceLocal conceptStorageServiceLocal;
	@Autowired
	private LabelStorageServiceLocal labelStorageServiceLocal;
	@Autowired
	private NotationStorageServiceLocal notationStorageServiceLocal;

	@Test
	public void createConceptWithLabelsOnly() {
		ConceptImpl conceptImpl = new ConceptImpl();
		ConceptLabelImpl label1 = new ConceptLabelImpl(LanguageImpl.DE, "deutch", LabelType.ALTERNATE);
		ConceptLabelImpl label2 = new ConceptLabelImpl(LanguageImpl.ES, "espanol", LabelType.ALTERNATE);
		conceptImpl.addConceptLabels(label1);
		conceptImpl.addConceptLabels(label2);
		String uuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, conceptImpl);
		Assert.assertNotNull(uuid);
	}

	@Test
	public void createConcept() {
		String uuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		Assert.assertNotNull(uuid);
	}

	@Test
	public void retrieveConcept() {
		String uuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		Assert.assertNotNull(uuid);
		Concept concept = conceptStorageServiceLocal.getConcept(uuid);
		Assert.assertEquals(2, concept.getLabels().size());
		Assert.assertEquals(2, concept.getNotations().size());
		Assert.assertEquals(2, concept.getNotes().size());
	}

	@Test
	public void checkConceptOptimization() {
		String uuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		Assert.assertNotNull(uuid);
		Collection<Notation> notations = notationStorageServiceLocal.getNotationsByCode("someCode");
		// should be 1 despite providing 2 notations with same details
		Assert.assertEquals(1, notations.size());
		Collection<Label> labels = labelStorageServiceLocal.getLabelsByText("espanol");
		// should be 1 despite providing 2 labels with same details
		Assert.assertEquals(1, labels.size());
	}

	@Test
	public void checkMultipleConceptOptimization() {
		ConceptImpl concept1 = TestUtility.createFullConcept();
		String uuid1 = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, concept1);
		Assert.assertNotNull(uuid1);

		String uuid2 = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, concept1);
		Assert.assertNotNull(uuid2);

		Assert.assertNotSame(uuid2, uuid1);

		Collection<Notation> notations = notationStorageServiceLocal.getNotationsByCode("someCode");
		// should be 1 despite providing 4 notations with same details
		Assert.assertEquals(1, notations.size());
		Collection<Label> labels = labelStorageServiceLocal.getLabelsByText("espanol");
		// should be 1 despite providing 4 labels with same details
		Assert.assertEquals(1, labels.size());
	}

	@Test(timeout = 1000)
	// 100 concepts 1 second
	public void createConceptStressTest() {
		for (int i = 0; i < 100; i++) {
			conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		}
	}

}
