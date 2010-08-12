package org.biosemantics.disambiguation.knowledgebase.service.local.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.service.impl.ConceptImpl;
import org.biosemantics.disambiguation.knowledgebase.service.impl.LabelImpl;
import org.biosemantics.disambiguation.knowledgebase.service.impl.NotationImpl;
import org.biosemantics.disambiguation.knowledgebase.service.local.TextIndexService;
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
	private static final String CONCEPT_FULL_TXT_INDEX = "concept_full_text";
	private static final String LABEL_TXT_INDEX = "label_text";
	private static final String LABEL_ID_INDEX = "label_id";
	private static final String NOTATION_CODE_INDEX = "notation_code";
	private static final String CONCEPT_ID_INDEX = "concept_id";

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
		Iterable<Node> nodes = indexService.getNodes(LABEL_TXT_INDEX, text);
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
		Node node = indexService.getSingleNode(LABEL_ID_INDEX, id);
		Label label = null;
		if (node != null) {
			label = new LabelImpl(node);
		}
		return label;
	}

	@Override
	@Transactional
	public void indexLabel(Label label) {
		indexService.index(((LabelImpl) label).getUnderlyingNode(), LABEL_TXT_INDEX, label.getText());
		indexService.index(((LabelImpl) label).getUnderlyingNode(), LABEL_ID_INDEX, label.getId());
	}

	@Override
	@Transactional
	public void indexNotation(Notation notation) {
		indexService.index(((NotationImpl) notation).getUnderlyingNode(), NOTATION_CODE_INDEX, notation.getCode());

	}

	@Override
	@Transactional
	public void indexConcept(Concept concept) {
		StringBuilder fullText = new StringBuilder(concept.getId()).append(FULL_TEXT_SEPARATOR);
		for (Label label : concept.getLabels()) {
			fullText.append(label.getText()).append(FULL_TEXT_SEPARATOR);
		}
		for (Notation notation : concept.getNotations()) {
			fullText.append(notation.getCode()).append(FULL_TEXT_SEPARATOR);
		}
		ConceptImpl conceptImpl = (ConceptImpl) concept;
		fullTextIndexService.index(conceptImpl.getUnderlyingNode(), CONCEPT_FULL_TXT_INDEX, fullText.toString());
		indexService.index(conceptImpl.getUnderlyingNode(), CONCEPT_ID_INDEX, conceptImpl.getId());
	}

	@Override
	@Transactional
	public Collection<Notation> getNotationsByCode(String code) {
		Iterable<Node> nodes = indexService.getNodes(NOTATION_CODE_INDEX, code);
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
	public Concept getConceptById(String id) {
		Node node = indexService.getSingleNode(CONCEPT_ID_INDEX, id);
		ConceptImpl conceptImpl = null;
		if (node != null) {
			conceptImpl = new ConceptImpl(node);
		}
		return conceptImpl;
	}

	@Override
	@Transactional
	public Collection<Concept> fullTextSearch(String text, int maxResults) {
		Iterable<Node> nodes = fullTextIndexService.getNodes(CONCEPT_FULL_TXT_INDEX, text);
		List<Concept> concepts = new ArrayList<Concept>();
		if (nodes != null) {
			int ctr = 0;
			for (Node node : nodes) {
				if (++ctr > maxResults) {
					break;
				} else {
					concepts.add(new ConceptImpl(node));
				}
			}
		}
		return concepts;
	}

	@Override
	public Notation getNotationsByDomainAndCode(Domain domain, String code) {
		Notation found = null;
		Collection<Notation> notations = getNotationsByCode(code);
		for (Notation notation : notations) {
			if (notation.getDomain().equals(domain)) {
				found = notation;
				break;
			}
		}
		return found;
	}

}
