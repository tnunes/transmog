package org.biosemantics.disambiguation.service.impl;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.service.LabelStorageService;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LabelStorageServiceImplTest extends AbstractTransactionalDataSource {

	private static final String PREFERRED_TXT = "PREFERRED";
	private static final String PREFERRED_TXT_SPACES = "PREFERRED MORE";
	private static final String TEXT_WITH_SPACES = "This is a looong text    with some spaces";
	@Autowired
	LabelStorageService labelStorageService;

	@Test
	public void testCreateLabel() {
		LabelImpl labelImpl = new LabelImpl(PREFERRED_TXT, LanguageImpl.EN);
		labelStorageService.createLabel(labelImpl);
	}

	@Test
	public void testGetLabelByText() {
		LabelImpl labelImpl = new LabelImpl(PREFERRED_TXT, LanguageImpl.EN);
		LabelImpl otherLabel = new LabelImpl(PREFERRED_TXT_SPACES, LanguageImpl.EN);
		labelStorageService.createLabel(labelImpl);
		labelStorageService.createLabel(otherLabel);
		Collection<Label> labels = labelStorageService.getLabelByText(PREFERRED_TXT);
		assertNotNull(labels);
		assertTrue(labels.size() == 1);
		for (Label label : labels) {
			assertTrue(label.getLanguage() == LanguageImpl.EN);
			assertTrue(label.getText().equals(PREFERRED_TXT));
		}
	}

	@Test
	public void testGetLabelByTextEmpty() {
		LabelImpl labelImpl = new LabelImpl(PREFERRED_TXT, LanguageImpl.EN);
		labelStorageService.createLabel(labelImpl);
		Collection<Label> labels = labelStorageService.getLabelByText("DUMMY");
		assertNotNull(labels);
		assertTrue(labels.isEmpty());
	}

	@Test
	public void testGetLabelByTextWithSpaces() {
		LabelImpl labelImpl = new LabelImpl(TEXT_WITH_SPACES, LanguageImpl.EN);
		labelStorageService.createLabel(labelImpl);
		Collection<Label> labels = labelStorageService.getLabelByText(TEXT_WITH_SPACES);
		assertNotNull(labels);
		assertTrue(labels.size() == 1);
		for (Label label : labels) {
			assertTrue(label.getLanguage() == LanguageImpl.EN);
			assertTrue(label.getText().equals(TEXT_WITH_SPACES));
		}
	}

	@Test
	public void testGetLabelByTextEquals() {
		LabelImpl labelImpl = new LabelImpl(TEXT_WITH_SPACES, LanguageImpl.EN);
		Label createdOnce = labelStorageService.createLabel(labelImpl);
		Label createdTwice = labelStorageService.createLabel(labelImpl);
		assertTrue(createdOnce.equals(createdTwice));
	}

}
