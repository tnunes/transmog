package org.biosemantics.conceptstore.dataimport;

import java.util.Map;

import org.biosemantics.conceptstore.domain.impl.RlspType;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchRelationship;

public class DataImportUtility {

	public DataImportUtility(BatchInserter inserter, BatchInserterIndex labelIndex, BatchInserterIndex notationIndex,
			BatchInserterIndex conceptIndex, BatchInserterIndex relationshipIndex) {
		this.inserter = inserter;
		this.labelIndex = labelIndex;
		this.notationIndex = notationIndex;
		this.conceptIndex = conceptIndex;
		this.relationshipIndex = relationshipIndex;
	}

	Long getConceptNodeForLabelNode(Long labelNode) {
		Long conceptNode = null;
		Iterable<BatchRelationship> relationships = inserter.getRelationships(labelNode);
		for (BatchRelationship batchRelationship : relationships) {
			if (batchRelationship.getType().name().equals(RlspType.HAS_LABEL.toString())) {
				conceptNode = batchRelationship.getStartNode();
				break;
			}
		}
		return conceptNode;
	}

	Long getConceptNodeForNotationNode(Long notationNode) {
		Long conceptNode = null;
		Iterable<BatchRelationship> relationships = inserter.getRelationships(notationNode);
		for (BatchRelationship batchRelationship : relationships) {
			if (batchRelationship.getType().name().equals(RlspType.HAS_NOTATION.toString())) {
				conceptNode = batchRelationship.getStartNode();
				break;
			}
		}
		return conceptNode;
	}

	Long getLabelNode(String labelText, String language) {
		Long labelNode = null;
		IndexHits<Long> hits = labelIndex.get("text", labelText);
		for (Long hit : hits) {
			Map<String, Object> props = inserter.getNodeProperties(hit);
			if (((String) props.get("language")).equalsIgnoreCase(language)) {
				labelNode = hit;
			}
		}
		return labelNode;
	}

	Long createLabelNode(String labelText, String language, Map<String, Object> props) {
		Long labelNode = null;
		props.put("text", labelText);
		props.put("language", language);
		labelNode = inserter.createNode(props);
		props.clear();
		props.put("text", labelText);
		labelIndex.add(labelNode, props);
		props.clear();
		return labelNode;
	}

	Long getNotationNode(String code) {
		Long notationNode = null;
		IndexHits<Long> hits = notationIndex.get("code", code);
		for (Long hit : hits) {
			notationNode = hit;
		}
		return notationNode;
	}

	Long createRelationship(Long from, Long to, RelationshipType rlspType, Map<String, Object> props) {
		Long relaNode = inserter.createRelationship(from, to, rlspType, props);
		props.clear();
		props.put("rlspType", rlspType.toString());
		relationshipIndex.add(relaNode, props);
		props.clear();
		return relaNode;
	}

	Long createNotationNode(String source, String code, Map<String, Object> props) {
		Long notationNode = null;
		props.put("source", source);
		props.put("code", code);
		notationNode = inserter.createNode(props);
		props.clear();
		props.put("code", code);
		notationIndex.add(notationNode, props);
		props.clear();
		return notationNode;
	}

	Long createConceptNode(String conceptType, Map<String, Object> props) {
		props.put("type", conceptType);
		Long conceptNode = inserter.createNode(props);
		conceptIndex.add(conceptNode, props);
		props.clear();
		return conceptNode;
	}

	private BatchInserter inserter;
	private BatchInserterIndex labelIndex;
	private BatchInserterIndex notationIndex;
	private BatchInserterIndex conceptIndex;
	private BatchInserterIndex relationshipIndex;

}
