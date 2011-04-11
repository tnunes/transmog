package org.biosemantics.disambiguation.service.impl;

import java.util.Collection;

import junit.framework.Assert;

import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.biosemantics.disambiguation.service.local.LabelStorageServiceLocal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LabelStorageServiceTest extends AbstractTransactionalDataSource {

	@Autowired
	private LabelStorageServiceLocal labelStorageServiceLocal;

	@Test
	public void createLabel() {
		LabelImpl labelImpl = new LabelImpl(LanguageImpl.CS, "neo");
		String uuid = labelStorageServiceLocal.createLabel(labelImpl);
		Assert.assertNotNull(uuid);
	}

	@Test(expected = NullPointerException.class)
	public void createNullLabel() {
		labelStorageServiceLocal.createLabel(null);
	}

	@Test(expected = NullPointerException.class)
	public void createLabelWithNullLanguage() {
		labelStorageServiceLocal.createLabel(new LabelImpl(null, "neo"));

	}

	@Test(expected = NullPointerException.class)
	public void createLabelWithNullText() {
		labelStorageServiceLocal.createLabel(new LabelImpl(LanguageImpl.EN, null));

	}

	@Test(expected = IllegalArgumentException.class)
	public void createLabelWithBlankText() {
		labelStorageServiceLocal.createLabel(new LabelImpl(LanguageImpl.EN, "   "));

	}

	@Test
	public void retrieveLabel() {
		LabelImpl created = new LabelImpl(LanguageImpl.EN, "morpheus");
		String uuid = labelStorageServiceLocal.createLabel(created);
		Assert.assertNotNull(uuid);
		Label retrieved = labelStorageServiceLocal.getLabel(uuid);
		Assert.assertNotNull(retrieved);
		Assert.assertEquals(retrieved.getText(), created.getText());
		Assert.assertEquals(retrieved.getLanguage().getLabel(), created.getLanguage().getLabel());
	}

	@Test(expected = NullPointerException.class)
	public void retrieveLabelWithNullUuid() {
		labelStorageServiceLocal.getLabel(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void retrieveLabelWithBlankUuid() {
		labelStorageServiceLocal.getLabel("     ");
	}

	@Test
	public void createMultipleLabelsWithSameContents() {
		LabelImpl created = new LabelImpl(LanguageImpl.EN, "morpheus");
		String uuid1 = labelStorageServiceLocal.createLabel(created);
		String uuid2 = labelStorageServiceLocal.createLabel(created);
		String uuid3 = labelStorageServiceLocal.createLabel(created);
		Assert.assertNotSame(uuid1, uuid2);
		Assert.assertNotSame(uuid2, uuid3);
		Assert.assertNotSame(uuid1, uuid3);

	}

	@Test
	public void retrieveMultipleLabels() {
		LabelImpl trinity = new LabelImpl(LanguageImpl.EN, "trinity");
		LabelImpl neo = new LabelImpl(LanguageImpl.EN, "neo");
		labelStorageServiceLocal.createLabel(trinity);
		labelStorageServiceLocal.createLabel(trinity);
		labelStorageServiceLocal.createLabel(trinity);
		labelStorageServiceLocal.createLabel(neo);
		labelStorageServiceLocal.createLabel(neo);
		Collection<Label> trinities = labelStorageServiceLocal.getLabelsByText("trinity");
		Assert.assertEquals(3, trinities.size());
		Collection<Label> neos = labelStorageServiceLocal.getLabelsByText("neo");
		Assert.assertEquals(2, neos.size());
	}

	@Test
	public void retrieveLabelsWithSpaces() {
		LabelImpl trinity = new LabelImpl(LanguageImpl.EN, "trinity is bold");
		LabelImpl neo = new LabelImpl(LanguageImpl.EN, "neo is the one");
		labelStorageServiceLocal.createLabel(trinity);
		labelStorageServiceLocal.createLabel(trinity);
		labelStorageServiceLocal.createLabel(trinity);
		labelStorageServiceLocal.createLabel(neo);
		labelStorageServiceLocal.createLabel(neo);
		Collection<Label> trinities = labelStorageServiceLocal.getLabelsByText("trinity is bold");
		Assert.assertEquals(3, trinities.size());
		Collection<Label> neos = labelStorageServiceLocal.getLabelsByText("neo is the		one");
		Assert.assertTrue(neos.isEmpty());
	}
}
