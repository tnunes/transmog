package org.biosemantics.disambiguation.service.impl;

import junit.framework.Assert;

import org.biosemantics.conceptstore.common.domain.Source;
import org.biosemantics.conceptstore.common.domain.Source.SourceType;
import org.biosemantics.conceptstore.utils.domain.impl.SourceImpl;
import org.biosemantics.disambiguation.service.local.SourceStorageServiceLocal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SourceStorageServiceTest extends AbstractTransactionalDataSource {

	@Autowired
	private SourceStorageServiceLocal sourceStorageServiceLocal;

	@Test
	public void createSource() {
		SourceImpl sourceImpl = new SourceImpl("http://dsfhsdfhkds.com", SourceType.RESOURCE);
		long id = sourceStorageServiceLocal.createSource(sourceImpl);
		Assert.assertTrue(id > 0);
	}

	@Test(expected = NullPointerException.class)
	public void createNullSource() {
		sourceStorageServiceLocal.createSource(null);
	}

	@Test(expected = NullPointerException.class)
	public void createSourceWithNullType() {
		sourceStorageServiceLocal.createSource(new SourceImpl("value", null));

	}

	@Test(expected = NullPointerException.class)
	public void createSourceWithNullValue() {
		sourceStorageServiceLocal.createSource(new SourceImpl(null, SourceType.RESOURCE));

	}

	@Test(expected = IllegalArgumentException.class)
	public void createSourceWithBlankValue() {
		sourceStorageServiceLocal.createSource(new SourceImpl("	", SourceType.RESOURCE));
	}

	@Test
	public void retrieveSource() {
		SourceImpl sourceImpl = new SourceImpl("http://dsfhsdfhkds.com", SourceType.RESOURCE);
		long id = sourceStorageServiceLocal.createSource(sourceImpl);
		Assert.assertTrue(id > 0);
		Source source = sourceStorageServiceLocal.getSource(id);
		Assert.assertNotNull(source);
		Assert.assertEquals(source.getValue(), sourceImpl.getValue());
		Assert.assertEquals(source.getSourceType(), sourceImpl.getSourceType());
	}

	@Test(expected = IllegalArgumentException.class)
	public void retrieveSourceWithZeroId() {
		sourceStorageServiceLocal.getSource(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void retrieveSourceWithNegativeId() {
		sourceStorageServiceLocal.getSource(-2198098);
	}

	@Test
	public void createMultipleSourcesWithSameContents() {
		SourceImpl sourceImpl = new SourceImpl("http://dsfhsdfhkds.com", SourceType.RESOURCE);
		long id1 = sourceStorageServiceLocal.createSource(sourceImpl);
		long id2 = sourceStorageServiceLocal.createSource(sourceImpl);
		long id3 = sourceStorageServiceLocal.createSource(sourceImpl);
		Assert.assertNotSame(id1, id2);
		Assert.assertNotSame(id2, id3);
		Assert.assertNotSame(id1, id3);

	}

}
