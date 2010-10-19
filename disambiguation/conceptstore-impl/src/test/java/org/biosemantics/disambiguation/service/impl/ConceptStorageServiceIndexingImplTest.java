package org.biosemantics.disambiguation.service.impl;

import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.common.service.LabelStorageService;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConceptStorageServiceIndexingImplTest extends AbstractTransactionalDataSource {

	private static final String PREFERRED_TXT = "PREFERRED";
	@Autowired
	ConceptStorageService conceptStorageService;
	@Autowired
	LabelStorageService labelStorageService;

	@Test(expected = NullPointerException.class)
	public void testCreateConceptNull() {
		conceptStorageService.createConcept(null);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateConceptNullLabels() {
		ConceptImpl conceptImpl = new ConceptImpl.Builder("", null).build();
		conceptStorageService.createConcept(conceptImpl);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateConceptEmptyLabels() {
		ConceptImpl conceptImpl = new ConceptImpl.Builder("", new Label[0]).build();
		conceptStorageService.createConcept(conceptImpl);
	}

	@Test
	public void testCreateConceptWithLabels() {
		LabelImpl labelImpl =  new LabelImpl(LabelType.PREFERRED, PREFERRED_TXT, Language.EN);
		Label label = labelStorageService.createLabel(labelImpl);
		ConceptImpl conceptImpl = new ConceptImpl.Builder("", label).build();
		conceptStorageService.createConcept(conceptImpl);
		conceptStorageService.createDomain(conceptImpl);
		conceptStorageService.createPredicate(conceptImpl);
	}
}
