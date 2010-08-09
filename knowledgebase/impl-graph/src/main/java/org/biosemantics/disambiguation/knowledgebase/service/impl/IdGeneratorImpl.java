package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.UUID;

import org.biosemantics.disambiguation.knowledgebase.validation.IdGenerator;


public class IdGeneratorImpl implements IdGenerator {

	@Override
	public String generateRandomId() {
		return UUID.randomUUID().toString();
	}
	
}
