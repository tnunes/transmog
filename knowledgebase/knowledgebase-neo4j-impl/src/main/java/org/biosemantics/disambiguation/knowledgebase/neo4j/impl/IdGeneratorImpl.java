package org.biosemantics.disambiguation.knowledgebase.neo4j.impl;

import java.util.UUID;

import org.biosemantics.disambiguation.knowledgebase.api.IdGenerator;

public class IdGeneratorImpl implements IdGenerator {

	@Override
	public String generateRandomId() {
		return UUID.randomUUID().toString();
	}
	
}
