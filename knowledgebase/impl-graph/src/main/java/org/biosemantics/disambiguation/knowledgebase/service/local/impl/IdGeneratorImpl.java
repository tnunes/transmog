package org.biosemantics.disambiguation.knowledgebase.service.local.impl;

import java.util.UUID;

import org.biosemantics.disambiguation.knowledgebase.service.local.IdGenerator;


public class IdGeneratorImpl implements IdGenerator {

	@Override
	public String generateRandomId() {
		return UUID.randomUUID().toString();
	}
	
}
