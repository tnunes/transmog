package org.biosemantics.disambiguation.service.impl;

import java.util.Collection;

import junit.framework.Assert;

import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.disambiguation.service.local.NotationStorageServiceLocal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NotationStorageServiceTest extends AbstractTransactionalDataSource {

	@Autowired
	private NotationStorageServiceLocal notationStorageServiceLocal;

	@Test
	public void createNotation() {
		NotationImpl notationImpl = new NotationImpl("c12345", "c12345");
		String uuid = notationStorageServiceLocal.createNotation(notationImpl);
		Assert.assertNotNull(uuid);
	}

	@Test(expected = NullPointerException.class)
	public void createNullNotation() {
		notationStorageServiceLocal.createNotation(null);
	}

	@Test(expected = NullPointerException.class)
	public void createNotationWithNullDomainUuid() {
		notationStorageServiceLocal.createNotation(new NotationImpl(null, "c12345"));

	}

	@Test(expected = NullPointerException.class)
	public void createNotationWithNullCode() {
		notationStorageServiceLocal.createNotation(new NotationImpl("c12345", null));

	}

	@Test(expected = IllegalArgumentException.class)
	public void createNotationWithBlankCode() {
		notationStorageServiceLocal.createNotation(new NotationImpl("c12345", "   "));

	}

	@Test
	public void retrieveNotation() {
		NotationImpl notationImpl = new NotationImpl("C12345", "C12345");
		String uuid = notationStorageServiceLocal.createNotation(notationImpl);
		Notation retrieved = notationStorageServiceLocal.getNotation(uuid);
		Assert.assertEquals(notationImpl.getCode(), retrieved.getCode());
		Assert.assertEquals(notationImpl.getDomainUuid(), retrieved.getDomainUuid());
	}

	@Test(expected = NullPointerException.class)
	public void retrieveNotationWithNullUuid() {
		notationStorageServiceLocal.getNotation(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void retrieveNotationWithBlankUuid() {
		notationStorageServiceLocal.getNotation(" ");
	}

	@Test
	public void createMultipleNotationsWithSameContents() {
		NotationImpl notationImpl = new NotationImpl("C12345", "C 12345");
		String uuid1 = notationStorageServiceLocal.createNotation(notationImpl);
		String uuid2 = notationStorageServiceLocal.createNotation(notationImpl);
		String uuid3 = notationStorageServiceLocal.createNotation(notationImpl);
		Assert.assertNotSame(uuid1, uuid2);
		Assert.assertNotSame(uuid2, uuid3);
		Assert.assertNotSame(uuid1, uuid3);

	}

	@Test
	public void retrieveMultipleNotations() {
		NotationImpl nlm = new NotationImpl("C12345", "C 12345");
		NotationImpl drugbank = new NotationImpl("DB12345", "DB 12345");
		notationStorageServiceLocal.createNotation(nlm);
		notationStorageServiceLocal.createNotation(nlm);
		notationStorageServiceLocal.createNotation(nlm);
		notationStorageServiceLocal.createNotation(drugbank);
		notationStorageServiceLocal.createNotation(drugbank);

		Collection<Notation> nlms = notationStorageServiceLocal.getNotationsByCode("C 12345");
		Collection<Notation> drugbanks = notationStorageServiceLocal.getNotationsByCode("DB 12345");
		Assert.assertEquals(3, nlms.size());
		Assert.assertEquals(2, drugbanks.size());
	}

	@Test
	public void retrieveNotationsWithSpaces() {
		NotationImpl nlm = new NotationImpl("C12345", "C	12 12345");
		NotationImpl drugbank = new NotationImpl("C12345", "DB	12	12345");
		notationStorageServiceLocal.createNotation(nlm);
		notationStorageServiceLocal.createNotation(nlm);
		notationStorageServiceLocal.createNotation(nlm);
		notationStorageServiceLocal.createNotation(drugbank);
		notationStorageServiceLocal.createNotation(drugbank);
		Collection<Notation> nlms = notationStorageServiceLocal.getNotationsByCode("C	12 12345");
		Assert.assertEquals(3, nlms.size());
		Collection<Notation> drugbanks = notationStorageServiceLocal.getNotationsByCode("DB 12 12345");
		Assert.assertTrue(drugbanks.isEmpty());
		
		
	}
}
