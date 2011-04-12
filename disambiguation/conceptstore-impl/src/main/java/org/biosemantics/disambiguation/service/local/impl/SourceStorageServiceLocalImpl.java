package org.biosemantics.disambiguation.service.local.impl;

import org.biosemantics.conceptstore.common.domain.Source;
import org.biosemantics.conceptstore.common.domain.Source.SourceType;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.conceptstore.utils.validation.ValidationUtility;
import org.biosemantics.disambiguation.domain.impl.SourceImpl;
import org.biosemantics.disambiguation.service.local.SourceStorageServiceLocal;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.springframework.beans.factory.annotation.Required;

public class SourceStorageServiceLocalImpl implements SourceStorageServiceLocal {

	private UuidGeneratorService uuidGeneratorService;
	private ValidationUtility validationUtility;
	private static final String SOURCE_INDEX = "source";
	private static final String SOURCE_UUID_INDEX_KEY = "source_uuid";
	private static final String SOURCE_VALUE_INDEX_KEY = "source_value";
	private final GraphStorageTemplate graphStorageTemplate;
	private final Node sourceParentNode;
	private final Index<Node> index;

	public SourceStorageServiceLocalImpl(GraphStorageTemplate graphStorageTemplate) {
		this.graphStorageTemplate = graphStorageTemplate;
		this.sourceParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.NOTES);
		this.index = graphStorageTemplate.getIndexManager().forNodes(SOURCE_INDEX);
	}

	@Required
	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = uuidGeneratorService;
	}

	@Required
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Override
	public String createSource(Source source) {
		validationUtility.validateSource(source);
		String uuid = uuidGeneratorService.generateRandomUuid();
		Node sourceNode = null;
		if (sourceNode == null) {
			sourceNode = graphStorageTemplate.getGraphDatabaseService().createNode();
			sourceNode.setProperty(SourceImpl.UUID_PROPERTY, uuid);
			sourceNode.setProperty(SourceImpl.VALUE_PROPERTY, source.getValue());
			sourceNode.setProperty(SourceImpl.SOURCE_TYPE_PROPERTY, source.getSourceType().name());
			index.add(sourceNode, SOURCE_UUID_INDEX_KEY, uuid);
			index.add(sourceNode, SOURCE_VALUE_INDEX_KEY, source.getValue());
		}
		graphStorageTemplate.createRelationship(sourceParentNode, sourceNode, DefaultRelationshipType.SOURCE);
		return uuid;
	}

	@Override
	public Node getSourceNode(String value, SourceType sourceType) {
		IndexHits<Node> nodes = index.get(SOURCE_VALUE_INDEX_KEY, value);
		for (Node node : nodes) {
			if (SourceType.valueOf((String) node.getProperty(SourceImpl.SOURCE_TYPE_PROPERTY)) == sourceType) {
				return node;
			}
		}
		return null;
	}

	@Override
	public Source getSource(String uuid) {
		validationUtility.validateString(uuid, "uuid");
		return new SourceImpl(getSourceNode(uuid));
	}

	private Node getSourceNode(String uuid) {
		return index.get(SOURCE_UUID_INDEX_KEY, uuid).getSingle();
	}

}
