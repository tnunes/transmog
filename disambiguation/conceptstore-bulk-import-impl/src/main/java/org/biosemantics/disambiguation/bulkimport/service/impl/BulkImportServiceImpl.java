package org.biosemantics.disambiguation.bulkimport.service.impl;

import static org.biosemantics.disambiguation.common.IndexConstant.*;
import static org.biosemantics.disambiguation.common.PropertyConstant.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptRelationship;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.disambiguation.bulkimport.service.BulkImportService;
import org.biosemantics.disambiguation.common.PropertyConstant;
import org.biosemantics.disambiguation.common.RelationshipTypeConstant;
import org.biosemantics.disambiguation.domain.impl.ConceptRelationshipImpl;
import org.biosemantics.disambiguation.service.local.impl.LabelStorageServiceLocalImpl;
import org.biosemantics.disambiguation.service.local.impl.NotationStorageServiceLocalImpl;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.BatchInserterIndex;
import org.neo4j.graphdb.index.BatchInserterIndexProvider;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.impl.lucene.LuceneBatchInserterIndexProvider;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class BulkImportServiceImpl implements BulkImportService {

	private static final Logger logger = LoggerFactory.getLogger(BulkImportServiceImpl.class);
	private final String dataDir;
	private final BatchInserter batchInserter;
	private GraphDatabaseService graphDatabaseService;
	private UuidGeneratorService uuidGeneratorService;

	@Required
	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = uuidGeneratorService;
	}

	// index
	private BatchInserterIndexProvider indexService;
	private BatchInserterIndex conceptNodeIndex;
	private BatchInserterIndex conceptFullTextIndex;
	private BatchInserterIndex labelNodeIndex;
	private BatchInserterIndex notationNodeIndex;
	// parentNodes
	private long conceptParentNode;
	private long labelParentNode;
	private long notationParentNode;
	private long noteParentNode;

	// relationships
	RelationshipType relatedRlspType = new RelationshipType() {

		@Override
		public String name() {
			return ConceptRelationshipType.RELATED.name();
		}
	};
	RelationshipType hasBroaderRlspType = new RelationshipType() {

		@Override
		public String name() {
			return ConceptRelationshipType.HAS_BROADER_CONCEPT.name();
		}
	};
	RelationshipType hasNarrowerRlspType = new RelationshipType() {

		@Override
		public String name() {
			return ConceptRelationshipType.HAS_NARROWER_CONCEPT.name();
		}
	};
	RelationshipType inSchemeRlspType = new RelationshipType() {

		@Override
		public String name() {
			return ConceptRelationshipType.IN_SCHEME.name();
		}
	};

	public BulkImportServiceImpl(String dataDir) {
		this.dataDir = dataDir;
		File file = new File(this.dataDir);
		if (!file.exists() || !file.isDirectory()) {
			throw new IllegalArgumentException("storeDir needs to be an existing empty directory");
		}
		this.batchInserter = new BatchInserterImpl(this.dataDir);
		this.graphDatabaseService = batchInserter.getGraphDbService();
	}

	public BulkImportServiceImpl(String dataDir, Map<String, String> configuration) {
		this.dataDir = dataDir;
		File file = new File(this.dataDir);
		if (!file.exists() || !file.isDirectory()) {
			throw new IllegalArgumentException("\"" + dataDir + "\" needs to be an existing empty directory");
		}
		this.batchInserter = new BatchInserterImpl(this.dataDir, configuration);
		this.graphDatabaseService = batchInserter.getGraphDbService();
	}

	public void init() {
		logger.info("initing indexes");
		this.indexService = new LuceneBatchInserterIndexProvider(batchInserter);
		conceptNodeIndex = this.indexService.nodeIndex(CONCEPT_INDEX.name(),
				MapUtil.stringMap("provider", "lucene", "type", "exact", "to_lower_case", "true"));
		conceptFullTextIndex = this.indexService.nodeIndex(CONCEPT_FULL_TEXT_KEY.name(),
				MapUtil.stringMap("provider", "lucene", "type", "fulltext"));
		labelNodeIndex = this.indexService.nodeIndex(LabelStorageServiceLocalImpl.LABEL_INDEX,
				MapUtil.stringMap("provider", "lucene", "type", "exact", "to_lower_case", "true"));
		notationNodeIndex = this.indexService.nodeIndex(NotationStorageServiceLocalImpl.NOTATION_INDEX,
				MapUtil.stringMap("provider", "lucene", "type", "exact", "to_lower_case", "true"));
		logger.info("creating parent nodes");
		conceptParentNode = batchInserter.createNode(null);
		batchInserter.createRelationship(batchInserter.getReferenceNode(), conceptParentNode,
				RelationshipTypeConstant.CONCEPTS, null);
		labelParentNode = batchInserter.createNode(null);
		batchInserter.createRelationship(batchInserter.getReferenceNode(), labelParentNode,
				RelationshipTypeConstant.LABELS, null);
		notationParentNode = batchInserter.createNode(null);
		batchInserter.createRelationship(batchInserter.getReferenceNode(), notationParentNode,
				RelationshipTypeConstant.NOTATIONS, null);
		noteParentNode = batchInserter.createNode(null);
		batchInserter.createRelationship(batchInserter.getReferenceNode(), noteParentNode,
				RelationshipTypeConstant.NOTES, null);
	}

	public void destroy() {
		logger.info("invoking flush");
		conceptNodeIndex.flush();
		conceptFullTextIndex.flush();
		labelNodeIndex.flush();
		notationNodeIndex.flush();
		logger.info("invoking shutdown");
		indexService.shutdown();
		batchInserter.shutdown();
		logger.info("shutdown complete");
	}

	@Override
	public long validateAndCreateRelationship(final ConceptRelationship conceptRelationship) {
		if (relationshipExists(conceptRelationship)) {
			return -1;
		} else {
			return createRelationship(conceptRelationship);
		}
	}

	@Override
	public boolean relationshipExists(final ConceptRelationship conceptRelationship) {
		boolean found = false;
		long startNodeId = Long.valueOf(conceptRelationship.fromConcept());
		long endNodeId = Long.valueOf(conceptRelationship.toConcept());
		Node startNode = graphDatabaseService.getNodeById(startNodeId);
		Node endNode = graphDatabaseService.getNodeById(endNodeId);
		if (startNode == null || endNode == null) {
			logger.error("nodes not found. start:{} end:{} conceptRelationship:{}", new Object[] { startNode, endNode,
					conceptRelationship });
		} else {
			long start = System.currentTimeMillis();
			switch (conceptRelationship.getType()) {
			case RELATED:
				found = isRelated(startNode, endNode);
				break;
			case HAS_BROADER_CONCEPT:
				found = isBroader(startNode, endNode);
				break;
			case HAS_NARROWER_CONCEPT:
				found = isNarrower(startNode, endNode);
				break;
			case IN_SCHEME:
				found = inScheme(startNode, endNode);
			}
			long time = System.currentTimeMillis() - start;
			logger.debug("is-related time:{} for conceptRelationship:{}", new Object[] { time, conceptRelationship });
		}
		return found;
	}

	private boolean inScheme(Node startNode, Node endNode) {
		Iterable<Relationship> rlsps = startNode.getRelationships(inSchemeRlspType);
		for (Relationship rlsp : rlsps) {
			if ((rlsp.getStartNode().equals(startNode) && rlsp.getEndNode().equals(endNode))
					|| (rlsp.getStartNode().equals(endNode) && rlsp.getEndNode().equals(startNode))) {
				return true;
			}
		}
		return false;
	}

	private boolean isRelated(Node startNode, Node endNode) {
		// simplest case simply look for a related relationship between 2 concepts in both directions
		Iterable<Relationship> rlsps = startNode.getRelationships(relatedRlspType);
		for (Relationship rlsp : rlsps) {
			if ((rlsp.getStartNode().equals(startNode) && rlsp.getEndNode().equals(endNode))
					|| (rlsp.getStartNode().equals(endNode) && rlsp.getEndNode().equals(startNode))) {
				return true;
			}
		}
		return false;
	}

	private boolean isBroader(Node startNode, Node endNode) {
		// we look for HAS_BROADER in primary direction and HAS_NARROWER in opposite direction
		Iterable<Relationship> rlsps = startNode.getRelationships(hasBroaderRlspType, hasNarrowerRlspType);
		for (Relationship rlsp : rlsps) {
			if ((rlsp.getStartNode().equals(startNode) && rlsp.getEndNode().equals(endNode) && rlsp.getType().name()
					.equals(hasBroaderRlspType.name()))
					|| (rlsp.getStartNode().equals(endNode) && rlsp.getEndNode().equals(startNode) && rlsp.getType()
							.name().equals(hasNarrowerRlspType.name()))) {
				return true;
			}
		}
		return false;

	}

	private boolean isNarrower(Node startNode, Node endNode) {
		// we look for HAS_NARROWER in primary direction and HAS_BROADER in opposite direction
		Iterable<Relationship> rlsps = startNode.getRelationships(hasBroaderRlspType, hasNarrowerRlspType);
		for (Relationship rlsp : rlsps) {
			if ((rlsp.getStartNode().equals(startNode) && rlsp.getEndNode().equals(endNode) && rlsp.getType().name()
					.equals(hasNarrowerRlspType.name()))
					|| (rlsp.getStartNode().equals(endNode) && rlsp.getEndNode().equals(startNode) && rlsp.getType()
							.name().equals(hasBroaderRlspType.name()))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public long createRelationship(final ConceptRelationship conceptRelationship) {
		long fromNodeId = Long.valueOf(conceptRelationship.fromConcept());
		long toNodeId = Long.valueOf(conceptRelationship.toConcept());
		if (fromNodeId <= 0 || toNodeId <= 0) {
			// Houston we have a problem!
			logger.error("we didnt find nodes from:{} or to:{} for conceptrelationship:{}", new Object[] { fromNodeId,
					toNodeId, conceptRelationship });
			throw new IllegalStateException();
		} else {
			Map<String, Object> properties = new HashMap<String, Object>();
			String relationshipUuid = uuidGeneratorService.generateRandomUuid();
			properties.put(UUID.name(), relationshipUuid);
			properties.put(ConceptRelationshipImpl.RLSP_CATEGORY_PROPERTY, conceptRelationship
					.getSource().getId());
			if (!StringUtils.isBlank(conceptRelationship.getPredicateConceptUuid())) {
				properties.put(ConceptRelationshipImpl.PREDICATE_CONCEPT_UUID_PROPERTY,
						getUuidforNodeId(Long.valueOf(conceptRelationship.getPredicateConceptUuid())));
			}
			properties.put(WEIGHT.name(), conceptRelationship.getWeight());
			long relationshipId = batchInserter.createRelationship(fromNodeId, toNodeId, new RelationshipType() {
				@Override
				public String name() {
					return conceptRelationship.getType().name();
				}
			}, properties);
			return relationshipId;
		}

	}

	@Override
	public long createLabel(Label label) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyConstant.TEXT.name(), label.getText());
		properties.put(PropertyConstant.LANGUAGE.name(), label.getLanguage().name());
		long labelNode = batchInserter.createNode(properties);
		batchInserter.createRelationship(labelParentNode, labelNode, RelationshipTypeConstant.LABEL, null);
		properties.clear();
		// index text
		properties.put(LabelStorageServiceLocalImpl.TEXT_INDEX_KEY, label.getText());
		labelNodeIndex.add(labelNode, properties);
		properties.clear();
		return labelNode;
	}

	@Override
	public long createNotation(Notation notation) {
		Map<String, Object> properties = new HashMap<String, Object>();
		String domainUuid = (String) batchInserter.getGraphDbService()
				.getNodeById(Long.valueOf(notation.getDomainUuid())).getProperty(UUID.name());
		properties.put(PropertyConstant.DOMAIN.name(), domainUuid);
		properties.put(PropertyConstant.CODE.name(), notation.getCode());
		long notationNodeId = batchInserter.createNode(properties);
		batchInserter.createRelationship(notationParentNode, notationNodeId, RelationshipTypeConstant.NOTATION, null);
		properties.clear();
		// index code
		properties.put(NotationStorageServiceLocalImpl.CODE_INDEX_KEY, notation.getCode());
		notationNodeIndex.add(notationNodeId, properties);
		properties.clear();
		return notationNodeId;
	}

	@Override
	public long createUmlsConcept(ConceptType conceptType, List<ConceptLabel> conceptLabels, List<Long> notations,
			String fullText) {
		Map<String, Object> properties = new HashMap<String, Object>();
		final String conceptUuid = uuidGeneratorService.generateRandomUuid();
		// properties map is reused so make sure to clear it after each storage call
		properties.put(UUID.name(), conceptUuid);
		long conceptNodeId = batchInserter.createNode(properties);
		properties.clear();
		batchInserter.createRelationship(conceptParentNode, conceptNodeId, RelationshipTypeConstant.CONCEPT, null);
		// indexing uuid
		properties.put(CONCEPT_UUID_KEY.name(), conceptUuid);
		properties.put(CONCEPT_TYPE_KEY.name(), conceptType.name());
		conceptNodeIndex.add(conceptNodeId, properties);
		properties.clear();
		// indexing full text
		properties.put(CONCEPT_FULL_TEXT_KEY.name(), fullText);
		conceptFullTextIndex.add(conceptNodeId, properties);
		properties.clear();
		// creating rlsp to labels
		for (ConceptLabel conceptLabel : conceptLabels) {
			properties.put(LABEL_TYPE.name(), conceptLabel.getLabelType().getId());
			batchInserter.createRelationship(conceptNodeId, Long.valueOf(conceptLabel.getText()),
					RelationshipTypeConstant.HAS_LABEL, properties);
			properties.clear();
		}
		// create rlsp to notations
		if (notations != null) {
			for (Long notationNodeId : notations) {
				batchInserter.createRelationship(conceptNodeId, notationNodeId, RelationshipTypeConstant.HAS_NOTATION,
						null);
			}
		}
		return conceptNodeId;
	}

	private String getUuidforNodeId(long nodeId) {
		return (String) batchInserter.getNodeProperties(nodeId).get(UUID.name());
	}

}
