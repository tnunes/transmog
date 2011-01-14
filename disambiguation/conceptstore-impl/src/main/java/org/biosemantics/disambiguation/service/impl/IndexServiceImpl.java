package org.biosemantics.disambiguation.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.Relationship;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.domain.impl.NotationImpl;
import org.biosemantics.disambiguation.domain.impl.RelationshipImpl;
import org.biosemantics.disambiguation.service.IndexConstants;
import org.biosemantics.disambiguation.service.IndexService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.impl.lucene.LuceneIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class IndexServiceImpl implements IndexService {

	private GraphStorageTemplate graphStorageTemplate;
	private Index<Node> nodeIndex;
	private Index<Node> fullTextIndex;
	private RelationshipIndex relationshipIndex;
	// "type" isn't a reserved key and isn't indexed automatically
	private static final Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);

	// TODO implement configuration using a configuration class
	public IndexServiceImpl(GraphStorageTemplate graphStorageTemplate) {
		super();
		this.graphStorageTemplate = checkNotNull(graphStorageTemplate);
		this.nodeIndex = this.graphStorageTemplate.getGraphDatabaseService().index()
				.forNodes(IndexConstants.NODE_INDEX_NAME);
		this.fullTextIndex = this.graphStorageTemplate
				.getGraphDatabaseService()
				.index()
				.forNodes(IndexConstants.FULL_TXT_INDEX_NAME,
						MapUtil.stringMap("provider", "lucene", "type", "fulltext"));
		this.relationshipIndex = this.graphStorageTemplate.getGraphDatabaseService().index()
				.forRelationships(IndexConstants.RELATIONSHIP_INDEX_NAME);

	}

	@Required
	public void setCacheSize(int cacheSize) {
		if (cacheSize > 0) {
			((LuceneIndex) nodeIndex).setCacheCapacity(IndexConstants.LABEL_TXT_INDEX_KEY, cacheSize);
			logger.info("cache size for label text index is set to {}",
					(((LuceneIndex) nodeIndex).getCacheCapacity(IndexConstants.LABEL_TXT_INDEX_KEY)));

			((LuceneIndex) nodeIndex).setCacheCapacity(IndexConstants.CONCEPT_ID_INDEX_KEY, cacheSize);
			logger.info("cache size for concept id index is set to {}",
					(((LuceneIndex) nodeIndex).getCacheCapacity(IndexConstants.CONCEPT_ID_INDEX_KEY)));

			((LuceneIndex) nodeIndex).setCacheCapacity(IndexConstants.NOTATION_CODE_INDEX_KEY, cacheSize);
			logger.info("cache size for notation code index is set to {}",
					(((LuceneIndex) nodeIndex).getCacheCapacity(IndexConstants.NOTATION_CODE_INDEX_KEY)));

			((LuceneIndex) fullTextIndex).setCacheCapacity(IndexConstants.CONCEPT_FULL_TEST_KEY, cacheSize);
			logger.info("cache size for concept full text index is set to {}",
					(((LuceneIndex) fullTextIndex).getCacheCapacity(IndexConstants.CONCEPT_FULL_TEST_KEY)));

			((LuceneIndex) relationshipIndex).setCacheCapacity(IndexConstants.RELATIONSHIP_INDEX_KEY, cacheSize);
			logger.info("cache size for relationship id index is set to {}",
					(((LuceneIndex) relationshipIndex).getCacheCapacity(IndexConstants.RELATIONSHIP_INDEX_KEY)));
		}
	}

	@Override
	@Transactional
	public Collection<Label> getLabelsByText(String text) {
		IndexHits<Node> indexHits = nodeIndex.get(IndexConstants.LABEL_TXT_INDEX_KEY, text);
		Set<Label> labels = new HashSet<Label>();
		if (indexHits != null) {
			for (Node node : indexHits) {
				labels.add(new LabelImpl(node));
			}
		}
		return labels;
	}

	@Override
	@Transactional
	public void indexLabel(Label label) {
		LabelImpl labelImpl = (LabelImpl) label;
		nodeIndex.add(labelImpl.getUnderlyingNode(), IndexConstants.LABEL_TXT_INDEX_KEY, labelImpl.getText());
	}

	@Override
	@Transactional
	public void indexNotation(Notation notation) {
		NotationImpl notationImpl = (NotationImpl) notation;
		nodeIndex.add(notationImpl.getUnderlyingNode(), IndexConstants.NOTATION_CODE_INDEX_KEY, notationImpl.getCode());
	}

	@Override
	@Transactional
	public Collection<Notation> getNotationByCode(String code) {
		IndexHits<Node> indexHits = nodeIndex.get(IndexConstants.NOTATION_CODE_INDEX_KEY, code);
		Set<Notation> notations = new HashSet<Notation>();
		if (indexHits != null) {
			for (Node node : indexHits) {
				notations.add(new NotationImpl(node));
			}
		}
		return notations;
	}

	@Override
	@Transactional
	public Concept getConceptByUuid(String uuid) {
		IndexHits<Node> indexHits = nodeIndex.get(IndexConstants.CONCEPT_ID_INDEX_KEY, uuid);
		return new ConceptImpl(indexHits.getSingle());
	}

	@Override
	@Transactional
	public void indexConcept(Concept concept) {
		ConceptImpl conceptImpl = (ConceptImpl) concept;
		String fullText = extractFullText(conceptImpl);
		nodeIndex.add(conceptImpl.getUnderlyingNode(), IndexConstants.CONCEPT_ID_INDEX_KEY, conceptImpl.getUuid());
		fullTextIndex.add(conceptImpl.getUnderlyingNode(), IndexConstants.CONCEPT_FULL_TEST_KEY, fullText);
	}

	@Override
	@Transactional
	public void updateFullTextIndex(Concept concept) {
		ConceptImpl conceptImpl = (ConceptImpl) concept;
		// FIXME
		fullTextIndex.remove(conceptImpl.getUnderlyingNode(), IndexConstants.FULL_TXT_INDEX_NAME, "");
		String fullText = extractFullText(conceptImpl);

	}

	@Override
	@Transactional
	public Collection<Concept> fullTextSearch(String text, int maxResults) {
		IndexHits<Node> indexHits = fullTextIndex.get(IndexConstants.CONCEPT_FULL_TEST_KEY, text);
		Set<Concept> concepts = new HashSet<Concept>();
		if (indexHits != null) {
			for (Node node : indexHits) {
				if (concepts.size() < maxResults) {
					concepts.add(new ConceptImpl(node));
				} else {
					break;
				}
			}
		}
		return concepts;
	}

	public void destroy() {
	}

	private String extractFullText(ConceptImpl conceptImpl) {
		StringBuilder fullText = new StringBuilder(conceptImpl.getUuid()).append(IndexConstants.FULL_TEXT_SEPARATOR);
		for (Label label : conceptImpl.getLabels()) {
			fullText.append(label.getText()).append(IndexConstants.FULL_TEXT_SEPARATOR);
		}
		if (conceptImpl.getNotations() != null) {
			for (Notation notation : conceptImpl.getNotations()) {
				fullText.append(notation.getCode()).append(IndexConstants.FULL_TEXT_SEPARATOR);
			}
		}
		return fullText.toString();
	}

	@Override
	public void indexRelationship(Relationship relationship) {
		relationshipIndex.add(((RelationshipImpl) relationship).getUnderlyingRelationship(),
				IndexConstants.RELATIONSHIP_INDEX_KEY, relationship.getUuid());
	}

	@Override
	public Relationship getRelationshipByUuid(String uuid) {
		IndexHits<org.neo4j.graphdb.Relationship> indexHits = relationshipIndex.get(
				IndexConstants.RELATIONSHIP_INDEX_KEY, uuid);
		return new RelationshipImpl(indexHits.getSingle());
	}

}
