package org.biosemantics.disambiguation.service.impl;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
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
		Collection<Label> labels = null;
		ConceptImpl conceptImpl = new ConceptImpl();
		conceptImpl.addLabelByType(LabelType.PREFERRED, labels);
		conceptStorageService.createConcept(conceptImpl);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateConceptNullLabel() {
		Label label = null;
		ConceptImpl conceptImpl = new ConceptImpl();
		conceptImpl.addLabelByType(LabelType.PREFERRED, label);
		conceptStorageService.createConcept(conceptImpl);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateConceptEmptyLabels() {
		ConceptImpl conceptImpl = new ConceptImpl();
		conceptImpl.addLabelByType(LabelType.PREFERRED, new Label[0]);
		conceptStorageService.createConcept(conceptImpl);
	}

	@Test
	public void testCreateConceptWithLabels() {
		LabelImpl labelImpl = new LabelImpl(PREFERRED_TXT, Language.EN);
		Label label = labelStorageService.createLabel(labelImpl);
		ConceptImpl conceptImpl = new ConceptImpl();
		conceptImpl.addLabelByType(LabelType.PREFERRED, label);
		conceptStorageService.createConcept(conceptImpl);
		conceptStorageService.createDomain(conceptImpl);
		conceptStorageService.createPredicate(conceptImpl);
	}
}
