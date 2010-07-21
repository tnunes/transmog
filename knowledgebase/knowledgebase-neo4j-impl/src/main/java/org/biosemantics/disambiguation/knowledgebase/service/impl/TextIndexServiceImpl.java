package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.service.TextIndexService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.index.IndexService;
import org.neo4j.index.lucene.LuceneFulltextIndexService;
import org.neo4j.index.lucene.LuceneIndexService;
import org.springframework.transaction.annotation.Transactional;

public class TextIndexServiceImpl implements TextIndexService {

	private final GraphDatabaseService graphDb;
	private final IndexService indexService;
	private final IndexService fullTextIndexService;
	private static final String CONCEPT_FULL_TXT_INDEX_NAME = "concept_full_text";
	private static final String LABEL_TXT_INDEX_NAME = "label_text";
	private static final String LABEL_ID_INDEX_NAME = "label_id";
	private static final String NOTATION_TXT_INDEX_NAME = "notation_text";

	private static final String FULL_TEXT_SEPARATOR = " ";

	// private static final Logger LOGGER = LoggerFactory.getLogger(IndexDaoImpl.class);
	public TextIndexServiceImpl(GraphDatabaseService graphDb) {
		if (graphDb == null)
			throw new NullArgumentException("grahDb");
		this.graphDb = graphDb;
		indexService = new LuceneIndexService(this.graphDb);
		fullTextIndexService = new LuceneFulltextIndexService(this.graphDb);
	}

	@Override
	@Transactional
	public Collection<Label> getLabelsByText(final String text) {
		Iterable<Node> nodes = indexService.getNodes(LABEL_TXT_INDEX_NAME, text);
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
	public Label getLabelById(final String id) {
		Node node = indexService.getSingleNode(LABEL_ID_INDEX_NAME, id);
		Label label = null;
		if (node != null) {
			label = new LabelImpl(node);
		}
		return label;
	}

	@Override
	@Transactional
	public void indexLabelText(Label label) {
		indexService.index(((LabelImpl) label).getUnderlyingNode(), LABEL_TXT_INDEX_NAME, label.getText());
		indexService.index(((LabelImpl) label).getUnderlyingNode(), LABEL_ID_INDEX_NAME, label.getText());
	}

	@Override
	@Transactional
	public void indexNotation(Notation notation) {
		indexService.index(((NotationImpl) notation).getUnderlyingNode(), NOTATION_TXT_INDEX_NAME, notation.getText());

	}

	@Override
	@Transactional
	public void indexConcept(Concept concept) {
		StringBuilder fullText = new StringBuilder(concept.getId()).append(FULL_TEXT_SEPARATOR);
		for (Label label : concept.getLabels()) {
			fullText.append(label.getText()).append(FULL_TEXT_SEPARATOR);
		}
		for (Notation notation : concept.getNotations()) {
			fullText.append(notation.getText()).append(FULL_TEXT_SEPARATOR);
		}
		ConceptImpl conceptImpl = (ConceptImpl) concept;
		fullTextIndexService.index(conceptImpl.getUnderlyingNode(), CONCEPT_FULL_TXT_INDEX_NAME, fullText.toString());
	}

}
