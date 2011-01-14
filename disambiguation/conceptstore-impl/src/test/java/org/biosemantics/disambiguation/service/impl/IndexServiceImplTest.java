package org.biosemantics.disambiguation.service.impl;

import java.util.Collection;

import junit.framework.Assert;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.biosemantics.disambiguation.service.IndexService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

public class IndexServiceImplTest extends AbstractTransactionalDataSource {

	private static final String PREFERRED_TXT = "PREFERRED";
	private static final String TEXT_WITH_WHITESPACE = "dementia This is\ta    text\nwith  a  lot of white spaces and PREFERRED";
	private static final String TEXT_WITH_SPACE = "dementia This is a   text with    spaces only  ";
	private static final String NOTATION_CODE_SPL_CAHRS = "dshgj12#$%^*&(Å§Ä¬¿Æ«·¬¿öÂûÉŒ ";
	@Autowired
	private IndexService indexService;
	@Autowired
	private ConceptStorageService conceptStorageService;
	private Concept concept1;
	private Concept concept2;
	private Concept concept3;

	@Before
	public void init() {
		ConceptImpl conceptImpl = new ConceptImpl();
		LabelImpl labelImpl = new LabelImpl(PREFERRED_TXT, LanguageImpl.EN);
		conceptImpl.addLabelByType(LabelType.PREFERRED, labelImpl);
		concept1 = conceptStorageService.createConcept(ConceptType.CONCEPT, conceptImpl);
		conceptImpl = new ConceptImpl();
		labelImpl = new LabelImpl(TEXT_WITH_WHITESPACE, LanguageImpl.EN);
		conceptImpl.addLabelByType(LabelType.ALTERNATE, labelImpl);
		concept2 = conceptStorageService.createConcept(ConceptType.CONCEPT, conceptImpl);
		conceptImpl = new ConceptImpl();
		labelImpl = new LabelImpl(TEXT_WITH_SPACE, LanguageImpl.EN);
		conceptImpl.addLabelByType(LabelType.ALTERNATE, labelImpl);
		org.biosemantics.conceptstore.utils.domain.impl.NotationImpl notationImpl = new org.biosemantics.conceptstore.utils.domain.impl.NotationImpl(
				concept2, NOTATION_CODE_SPL_CAHRS);
		conceptImpl.addNotations(notationImpl);
		concept3 = conceptStorageService.createConcept(ConceptType.CONCEPT, conceptImpl);
	}

	@Test
	public void testGetConceptByUuid() {
		Concept concept = indexService.getConceptByUuid(concept1.getUuid());
		Assert.assertNotNull(concept);
		Assert.assertEquals(concept, concept1);
		Assert.assertNotSame(concept, concept2);
		Concept anotherConcept = indexService.getConceptByUuid(concept3.getUuid());
		Assert.assertNotNull(anotherConcept);
		Assert.assertEquals(anotherConcept, concept3);
		Assert.assertNotSame(anotherConcept, concept2);
	}

	@Test
	public void testFullTextSearch() {
		Collection<Concept> concepts = indexService.fullTextSearch("dementia", 100);
		Assert.assertFalse(CollectionUtils.isEmpty(concepts));
		Assert.assertEquals(concepts.size(), 1);
	}

	@Test
	public void testFullTextSearchWithMaxResults() {
		Collection<Concept> concepts = indexService.fullTextSearch("dementia", 1);
		Assert.assertFalse(CollectionUtils.isEmpty(concepts));
		Assert.assertEquals(concepts.size(), 1);
	}

	@Test
	public void testGetLabelByLabelText() {
		Collection<Label> labels = indexService.getLabelsByText(TEXT_WITH_WHITESPACE);
		Assert.assertFalse(CollectionUtils.isEmpty(labels));
		Assert.assertEquals(labels.size(), 1);
		for (Label label : labels) {
			Assert.assertEquals(label.getLanguage(), LanguageImpl.EN);
			Assert.assertEquals(label.getText(), TEXT_WITH_WHITESPACE);
		}
	}

	@Test
	public void testGetNotationByCode() {
		Collection<Notation> notations = indexService.getNotationByCode(NOTATION_CODE_SPL_CAHRS);
		Assert.assertFalse(CollectionUtils.isEmpty(notations));
		Assert.assertEquals(notations.size(), 1);
		for (Notation notation : notations) {
			Assert.assertEquals(notation.getCode(), NOTATION_CODE_SPL_CAHRS);
			Assert.assertEquals(notation.getDomain(), concept2);
		}
	}

}
