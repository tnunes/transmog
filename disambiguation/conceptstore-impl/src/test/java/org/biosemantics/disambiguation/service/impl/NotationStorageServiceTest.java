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
		long id = notationStorageServiceLocal.createNotation(notationImpl);
		Assert.assertTrue(id > 0);
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
		long id = notationStorageServiceLocal.createNotation(notationImpl);
		Notation retrieved = notationStorageServiceLocal.getNotation(id);
		Assert.assertEquals(notationImpl.getCode(), retrieved.getCode());
		Assert.assertEquals(notationImpl.getDomain(), retrieved.getDomain());
	}

	@Test(expected = IllegalArgumentException.class)
	public void retrieveNotationWithZeroId() {
		notationStorageServiceLocal.getNotation(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void retrieveNotationWithNegativeId() {
		notationStorageServiceLocal.getNotation(-100);
	}

	@Test
	public void createMultipleNotationsWithSameContents() {
		NotationImpl notationImpl = new NotationImpl("C12345", "C 12345");
		long id1 = notationStorageServiceLocal.createNotation(notationImpl);
		long id2 = notationStorageServiceLocal.createNotation(notationImpl);
		long id3 = notationStorageServiceLocal.createNotation(notationImpl);
		Assert.assertNotSame(id1, id2);
		Assert.assertNotSame(id2, id3);
		Assert.assertNotSame(id1, id3);

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
