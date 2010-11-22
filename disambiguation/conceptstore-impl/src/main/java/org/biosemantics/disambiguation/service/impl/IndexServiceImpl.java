package org.biosemantics.disambiguation.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.Sort;
import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.domain.impl.NotationImpl;
import org.biosemantics.disambiguation.service.Index;
import org.biosemantics.disambiguation.service.IndexService;
import org.neo4j.graphdb.Node;
import org.neo4j.index.lucene.LuceneFulltextIndexService;
import org.neo4j.index.lucene.LuceneIndexService;
import org.springframework.transaction.annotation.Transactional;

public class IndexServiceImpl implements IndexService {

	private final GraphStorageTemplate graphStorageTemplate;
	private final LuceneIndexService indexService;
	private final LuceneFulltextIndexService fullTextIndexService;

	// TODO implement configuration using a configuration class

	public IndexServiceImpl(GraphStorageTemplate graphStorageTemplate) {
		super();
		this.graphStorageTemplate = checkNotNull(graphStorageTemplate);
		indexService = new LuceneIndexService(this.graphStorageTemplate.getGraphDatabaseService());
		fullTextIndexService = new LuceneFulltextIndexService(this.graphStorageTemplate.getGraphDatabaseService());
	}

	@Override
	@Transactional
	public Collection<Label> getLabelsByText(String text) {
		Iterable<Node> nodes = indexService.getNodes(Index.LABEL_TXT_INDEX, text);
		List<Label> labels = new ArrayList<Label>();
		if (labels != null) {
			for (Node node : nodes) {
				labels.add(new LabelImpl(node));
			}
		}
		return labels;
	}

	@Override
	@Transactional
	public void indexLabel(Label label) {
		LabelImpl labelImpl = (LabelImpl) label;
		indexService.index(labelImpl.getUnderlyingNode(), Index.LABEL_TXT_INDEX, labelImpl.getText());
	}

	@Override
	@Transactional
	public Collection<Notation> getNotationByCode(String code) {
		Iterable<Node> nodes = indexService.getNodes(Index.NOTATION_CODE_INDEX, code);
		List<Notation> notations = new ArrayList<Notation>();
		if (notations != null) {
			for (Node node : nodes) {
				notations.add(new NotationImpl(node));
			}
		}
		return notations;
	}

	@Override
	@Transactional
	public void indexNotation(Notation notation) {
		NotationImpl notationImpl = (NotationImpl) notation;
		indexService.index(notationImpl.getUnderlyingNode(), Index.NOTATION_CODE_INDEX, notationImpl.getCode());
	}

	@Override
	@Transactional
	public Concept getConceptByUuid(String uuid) {
		Node node = indexService.getSingleNode(Index.CONCEPT_ID_INDEX, uuid);
		ConceptImpl conceptImpl = null;
		if (node != null) {
			conceptImpl = new ConceptImpl(node);
		}
		return conceptImpl;
	}

	@Override
	@Transactional
	public void indexConcept(Concept concept) {
		ConceptImpl conceptImpl = (ConceptImpl) concept;
		createFullTextIndex(conceptImpl);
		indexService.index(conceptImpl.getUnderlyingNode(), Index.CONCEPT_ID_INDEX, conceptImpl.getUuid());
	}

	@Override
	@Transactional
	public void updateFullTextIndex(Concept concept) {
		ConceptImpl conceptImpl = (ConceptImpl) concept;
		fullTextIndexService.removeIndex(conceptImpl.getUnderlyingNode(), Index.CONCEPT_FULL_TXT_INDEX);
		createFullTextIndex(conceptImpl);
	}

	@Override
	@Transactional
	public Collection<Concept> fullTextSearch(String text, int maxResults) {
		Iterable<Node> nodes = fullTextIndexService.getNodes(Index.CONCEPT_FULL_TXT_INDEX, text, Sort.RELEVANCE);
		Set<Concept> concepts = new HashSet<Concept>();
		if (nodes != null) {
			for (Node node : nodes) {
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
		indexService.shutdown();
		fullTextIndexService.shutdown();
	}

	private void createFullTextIndex(ConceptImpl conceptImpl) {
		StringBuilder fullText = new StringBuilder(conceptImpl.getUuid()).append(Index.FULL_TEXT_SEPARATOR);
		for (Label label : conceptImpl.getLabels()) {
			fullText.append(label.getText()).append(Index.FULL_TEXT_SEPARATOR);
		}
		if (conceptImpl.getNotations() != null) {
			for (Notation notation : conceptImpl.getNotations()) {
				fullText.append(notation.getCode()).append(Index.FULL_TEXT_SEPARATOR);
			}
		}
		fullTextIndexService.index(conceptImpl.getUnderlyingNode(), Index.CONCEPT_FULL_TXT_INDEX, fullText.toString());
	}

}
