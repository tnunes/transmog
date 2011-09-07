package org.biosemantics.disambiguation.script.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.script.ConceptTypeScript;
import org.biosemantics.disambiguation.service.local.impl.DefaultRelationshipType;
import org.biosemantics.disambiguation.service.local.impl.GraphStorageTemplate;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ConceptTypeScriptImpl implements ConceptTypeScript {

	private static final Logger logger = LoggerFactory.getLogger(ConceptTypeScriptImpl.class);
	private GraphStorageTemplate graphStorageTemplate;

	@Required
	public void setGraphStorageTemplate(GraphStorageTemplate graphStorageTemplate) {
		this.graphStorageTemplate = graphStorageTemplate;
	}

	@Override
	public Collection<Concept> getConceptsByType(ConceptType conceptType) {
		Collection<Concept> concepts = new ArrayList<Concept>();
		// FIXME: calculate defaultrealtionshiptype from conceptType
		DefaultRelationshipType defaultRelationshipType = DefaultRelationshipType.CONCEPTS;
		logger.debug("found parent node");
		Node parentNode = graphStorageTemplate.getParentNode(defaultRelationshipType);
		Iterable<Relationship> relationships = parentNode.getRelationships(DefaultRelationshipType.CONCEPT,
				Direction.OUTGOING);
		logger.debug("got all relationships. Iterating...");
		for (Relationship relationship : relationships) {
			Node conceptNode = relationship.getOtherNode(parentNode);
			ConceptImpl conceptImpl = new ConceptImpl(conceptNode);
			concepts.add(conceptImpl);
		}
		logger.debug("iterated all relationships");
		return concepts;
	}

}
