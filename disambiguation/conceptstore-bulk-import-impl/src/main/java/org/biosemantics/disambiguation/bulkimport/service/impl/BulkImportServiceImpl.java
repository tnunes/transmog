package org.biosemantics.disambiguation.bulkimport.service.impl;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.disambiguation.bulkimport.service.NodeCacheService;
import org.biosemantics.disambiguation.bulkimport.service.BulkImportService;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.domain.impl.NotationImpl;
import org.biosemantics.disambiguation.service.local.impl.ConceptStorageServiceLocalImpl;
import org.biosemantics.disambiguation.service.local.impl.DefaultRelationshipType;
import org.biosemantics.disambiguation.service.local.impl.LabelStorageServiceLocalImpl;
import org.biosemantics.disambiguation.service.local.impl.NotationStorageServiceLocalImpl;
import org.neo4j.graphdb.index.BatchInserterIndex;
import org.neo4j.graphdb.index.BatchInserterIndexProvider;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.impl.lucene.LuceneBatchInserterIndexProvider;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

public class BulkImportServiceImpl implements BulkImportService {

	private static final Logger logger = LoggerFactory.getLogger(BulkImportServiceImpl.class);
	private final String storeDir;
	private final BatchInserter batchInserter;
	private UuidGeneratorService uuidGeneratorService;
	private NodeCacheService bulkImportNodeCache;
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
	private long domainParentNode;
	private long predicateParentNode;

	public BulkImportServiceImpl(String storeDir) {
		this.storeDir = storeDir;
		File file = new File(this.storeDir);
		if (!file.exists() || !file.isDirectory()) {
			throw new IllegalArgumentException("storeDir needs to be an existing empty directory");
		}
		this.batchInserter = new BatchInserterImpl(this.storeDir);
	}

	@Required
	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = uuidGeneratorService;
	}

	@Required
	public void setBulkImportNodeCache(NodeCacheService bulkImportNodeCache) {
		this.bulkImportNodeCache = bulkImportNodeCache;
	}

	public void init() {
		logger.info("initing indexes");
		this.indexService = new LuceneBatchInserterIndexProvider(batchInserter);
		conceptNodeIndex = this.indexService.nodeIndex(ConceptStorageServiceLocalImpl.CONCEPT_INDEX,
				MapUtil.stringMap("provider", "lucene", "type", "exact"));
		conceptFullTextIndex = this.indexService.nodeIndex(ConceptStorageServiceLocalImpl.CONCEPT_FULLTEXT_INDEX,
				MapUtil.stringMap("provider", "lucene", "type", "fulltext"));
		labelNodeIndex = this.indexService.nodeIndex(LabelStorageServiceLocalImpl.LABEL_INDEX,
				MapUtil.stringMap("provider", "lucene", "type", "exact"));
		notationNodeIndex = this.indexService.nodeIndex(NotationStorageServiceLocalImpl.NOTATION_INDEX,
				MapUtil.stringMap("provider", "lucene", "type", "exact"));
		logger.info("creating parent nodes");
		conceptParentNode = batchInserter.createNode(null);
		batchInserter.createRelationship(batchInserter.getReferenceNode(), conceptParentNode,
				DefaultRelationshipType.CONCEPTS, null);
		labelParentNode = batchInserter.createNode(null);
		batchInserter.createRelationship(batchInserter.getReferenceNode(), labelParentNode,
				DefaultRelationshipType.LABELS, null);
		notationParentNode = batchInserter.createNode(null);
		batchInserter.createRelationship(batchInserter.getReferenceNode(), notationParentNode,
				DefaultRelationshipType.NOTATIONS, null);
		domainParentNode = batchInserter.createNode(null);
		batchInserter.createRelationship(batchInserter.getReferenceNode(), domainParentNode,
				DefaultRelationshipType.DOMAINS, null);
		predicateParentNode = batchInserter.createNode(null);
		batchInserter.createRelationship(batchInserter.getReferenceNode(), predicateParentNode,
				DefaultRelationshipType.PREDICATES, null);
	}

	@Override
	public String createConcept(ConceptType conceptType, Concept concept) {
		final String conceptUuid = uuidGeneratorService.generateRandomUuid();
		// properties map is reused so make sure to clear it after each storage call
		Map<String, Object> properties = new HashMap<String, Object>();
		Collection<String> fullText = new HashSet<String>();
		properties.put(ConceptImpl.UUID_PROPERTY, conceptUuid);
		long conceptNode = batchInserter.createNode(properties);
		switch (conceptType) {
		case CONCEPT:
			batchInserter.createRelationship(conceptParentNode, conceptNode, DefaultRelationshipType.CONCEPT, null);
			break;
		case DOMAIN:
			batchInserter.createRelationship(domainParentNode, conceptNode, DefaultRelationshipType.CONCEPT, null);
			break;
		case PREDICATE:
			batchInserter.createRelationship(predicateParentNode, conceptNode, DefaultRelationshipType.CONCEPT, null);
			break;
		default:
			break;
		}
		properties.clear();
		Collection<ConceptLabel> labels = concept.getLabels();
		for (ConceptLabel conceptLabel : labels) {
			final String labelUuid = uuidGeneratorService.generateRandomUuid();
			fullText.add(conceptLabel.getText());
			long labelNode = bulkImportNodeCache.getLabelNodeId(conceptLabel.getText(), conceptLabel.getLanguage()
					.getLabel());
			if (labelNode < 0) {
				properties.put(LabelImpl.UUID_PROPERTY, labelUuid);
				properties.put(LabelImpl.TEXT_PROPERTY, conceptLabel.getText());
				properties.put(LabelImpl.LANGUAGE_PROPERTY, conceptLabel.getLanguage().getLabel());
				labelNode = batchInserter.createNode(properties);
				batchInserter.createRelationship(labelParentNode, labelNode, DefaultRelationshipType.LABEL, null);
				properties.clear();
				// index
				properties.put(LabelStorageServiceLocalImpl.UUID_INDEX_KEY, labelUuid);
				labelNodeIndex.add(labelNode, properties);
				properties.clear();
				properties.put(LabelStorageServiceLocalImpl.TEXT_INDEX_KEY, conceptLabel.getText());
				labelNodeIndex.add(labelNode, properties);
				properties.clear();
				bulkImportNodeCache.addLabel(conceptLabel.getText(), conceptLabel.getLanguage().getLabel(), labelNode);
			}
			properties.put(ConceptImpl.LABEL_TYPE_RLSP_PROPERTY, conceptLabel.getLabelType().name());
			batchInserter.createRelationship(conceptNode, labelNode, DefaultRelationshipType.HAS_LABEL, properties);
			properties.clear();

		}
		Collection<Notation> notations = concept.getNotations();
		if (!CollectionUtils.isEmpty(notations)) {
			for (Notation notation : notations) {

				final String notationUuid = uuidGeneratorService.generateRandomUuid();
				fullText.add(notation.getCode());
				long notationNode = bulkImportNodeCache.getNotationNodeId(notation.getDomainUuid(), notation.getCode());
				if (notationNode < 0) {

					properties.put(NotationImpl.UUID_PROPERTY, notationUuid);
					properties.put(NotationImpl.DOMAIN_UUID_PROPERTY, notation.getDomainUuid());
					properties.put(NotationImpl.CODE_PROPERTY, notation.getCode());
					notationNode = batchInserter.createNode(properties);
					batchInserter.createRelationship(notationParentNode, notationNode,
							DefaultRelationshipType.NOTATION, null);
					properties.clear();
					// index
					properties.put(NotationStorageServiceLocalImpl.UUID_INDEX_KEY, notationUuid);
					notationNodeIndex.add(notationNode, properties);
					properties.clear();
					properties.put(NotationStorageServiceLocalImpl.CODE_INDEX_KEY, notation.getCode());
					notationNodeIndex.add(notationNode, properties);
					properties.clear();
					bulkImportNodeCache.addNotation(notation.getDomainUuid(), notation.getCode(), notationNode);
				}
				batchInserter.createRelationship(conceptNode, notationNode, DefaultRelationshipType.HAS_NOTATION, null);
				properties.clear();
			}
		}
		properties.put(ConceptStorageServiceLocalImpl.UUID_INDEX_KEY, conceptUuid);
		conceptNodeIndex.add(conceptNode, properties);
		properties.clear();

		fullText.add(conceptUuid);
		StringBuilder fullTextString = new StringBuilder();
		for (String string : fullText) {
			fullTextString.append(string).append(ConceptStorageServiceLocalImpl.DELIMITER);
		}
		properties.put(ConceptStorageServiceLocalImpl.FULLTEXT_INDEX_KEY, fullTextString.toString());
		conceptFullTextIndex.add(conceptNode, properties);
		properties.clear();
		return conceptUuid;
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
}
