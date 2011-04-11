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
		String uuid = sourceStorageServiceLocal.createSource(sourceImpl);
		Assert.assertNotNull(uuid);
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
		String uuid = sourceStorageServiceLocal.createSource(sourceImpl);
		Assert.assertNotNull(uuid);
		Source source = sourceStorageServiceLocal.getSource(uuid);
		Assert.assertNotNull(source);
		Assert.assertEquals(source.getValue(), sourceImpl.getValue());
		Assert.assertEquals(source.getSourceType(), sourceImpl.getSourceType());
	}

	@Test(expected = NullPointerException.class)
	public void retrieveSourceWithNullUuid() {
		sourceStorageServiceLocal.getSource(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void retrieveSourceWithBlankUuid() {
		sourceStorageServiceLocal.getSource("     ");
	}

	@Test
	public void createMultipleSourcesWithSameContents() {
		SourceImpl sourceImpl = new SourceImpl("http://dsfhsdfhkds.com", SourceType.RESOURCE);
		String uuid1 = sourceStorageServiceLocal.createSource(sourceImpl);
		String uuid2 = sourceStorageServiceLocal.createSource(sourceImpl);
		String uuid3 = sourceStorageServiceLocal.createSource(sourceImpl);
		Assert.assertNotSame(uuid1, uuid2);
		Assert.assertNotSame(uuid2, uuid3);
		Assert.assertNotSame(uuid1, uuid3);

	}

}
