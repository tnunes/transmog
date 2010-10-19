package org.biosemantics.disambiguation.service.impl;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.common.service.LabelStorageService;
import org.biosemantics.conceptstore.common.service.NotationStorageService;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NotationStorageServiceImplTest extends AbstractTransactionalDataSource {

	private static final String NOTATION_CODE = "C123456";
	private static final String SPECIAL_CHARS_NOTATION_CODE = "@!#$#$%  ^&*() 3ŒÄ©´¬ ÂûÆ¾û¾M  <NKLJS";
	private static final String OTHER_NOTATION_CODE = "C654321";
	@Autowired
	NotationStorageService notationStorageService;
	@Autowired
	ConceptStorageService conceptStorageService;
	@Autowired
	LabelStorageService labelStorageService;

	@Test
	public void testCreateNotation() {
		NotationImpl notationImpl = new NotationImpl(createDomain(), NOTATION_CODE);
		notationStorageService.createNotation(notationImpl);
	}

	private Concept createDomain() {
		Label label = labelStorageService.createLabel(new LabelImpl(LabelType.PREFERRED, "UMLS", Language.EN));
		ConceptImpl conceptImpl = new ConceptImpl.Builder("", label).build();
		return conceptStorageService.createDomain(conceptImpl);

	}

	@Test
	public void testGetNotationByCode() {
		Concept domain = createDomain();
		NotationImpl notationImpl = new NotationImpl(domain, NOTATION_CODE);
		NotationImpl other = new NotationImpl(createDomain(), OTHER_NOTATION_CODE);
		notationStorageService.createNotation(notationImpl);
		notationStorageService.createNotation(other);

		Collection<Notation> notations = notationStorageService.getNotationsByCode(NOTATION_CODE);
		assertNotNull(notations);
		assertTrue(notations.size() == 1);
		for (Notation notation : notations) {
			assertTrue(notation.getCode() == NOTATION_CODE);
			assertTrue(notation.getDomain().equals(domain));
		}
	}

	@Test
	public void testGetNotationByCodeEmpty() {
		NotationImpl other = new NotationImpl(createDomain(), OTHER_NOTATION_CODE);
		notationStorageService.createNotation(other);
		Collection<Notation> notations = notationStorageService.getNotationsByCode(NOTATION_CODE);
		assertNotNull(notations);
		assertTrue(notations.isEmpty());
	}

	@Test
	public void testGetLabelByTextEquals() {
		NotationImpl other = new NotationImpl(createDomain(), OTHER_NOTATION_CODE);
		Notation createdOnce = notationStorageService.createNotation(other);
		Notation createdTwice = notationStorageService.createNotation(other);
		assertTrue(createdOnce.equals(createdTwice));
	}
	
	@Test
	public void testGetNotationByCodeWithSpecialCharacter() {
		Concept domain = createDomain();
		NotationImpl notationImpl =  new NotationImpl(domain, SPECIAL_CHARS_NOTATION_CODE);
		notationStorageService.createNotation(notationImpl);
		Collection<Notation> notations = notationStorageService.getNotationsByCode(SPECIAL_CHARS_NOTATION_CODE);
		assertNotNull(notations);
		assertTrue(notations.size() == 1);
		for (Notation notation : notations) {
			assertTrue(notation.getCode().equals(SPECIAL_CHARS_NOTATION_CODE));
			assertTrue(notation.getDomain().equals(domain));
		}
	}
	

}
