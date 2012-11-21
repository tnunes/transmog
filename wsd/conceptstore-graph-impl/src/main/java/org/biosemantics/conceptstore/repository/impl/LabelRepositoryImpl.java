package org.biosemantics.conceptstore.repository.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.impl.LabelImpl;
import org.biosemantics.conceptstore.repository.LabelRepository;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelRepositoryImpl implements LabelRepository {
	public LabelRepositoryImpl(GraphDatabaseService graphDatabaseService) {
		this.graphDb = graphDatabaseService;
		labelNodeIndex = this.graphDb.index().forNodes("Label");
	}

	/* (non-Javadoc)
	 * @see org.biosemantics.conceptstore.repository.impl.LabelRepository#create(java.lang.String, java.lang.String)
	 */
	@Override
	public Label create(String text, String language) {
		return createNewlabel(text, language);
	}

	/* (non-Javadoc)
	 * @see org.biosemantics.conceptstore.repository.impl.LabelRepository#getById(long)
	 */
	@Override
	public Label getById(long id) {
		Node node = graphDb.getNodeById(id);
		if (node == null) {
			throw new IllegalArgumentException("no node for id = " + id);
		} else {
			return new LabelImpl(node);
		}
	}

	/* (non-Javadoc)
	 * @see org.biosemantics.conceptstore.repository.impl.LabelRepository#getOrCreate(java.lang.String, java.lang.String)
	 */
	@Override
	public Label getOrCreate(String text, String language) {
		IndexHits<Node> hits = labelNodeIndex.get("text", text);
		Label foundLabel = null;
		if (hits != null && hits.size() > 0) {
			for (Node hit : hits) {
				String foundLanguage = (String) hit.getProperty("language");
				if (foundLanguage.equals(language)) {
					foundLabel = new LabelImpl(hit);
					break;
				}
			}
		}
		if (foundLabel == null) {
			return createNewlabel(text, language);
		} else {
			return foundLabel;
		}
	}

	/* (non-Javadoc)
	 * @see org.biosemantics.conceptstore.repository.impl.LabelRepository#getByText(java.lang.String)
	 */
	@Override
	public Collection<Label> getByText(String text) {
		IndexHits<Node> hits = labelNodeIndex.get("text", text);
		Set<Label> labels = new HashSet<Label>();
		for (Node node : hits) {
			labels.add(new LabelImpl(node));
		}
		return labels;
	}

	private Label createNewlabel(String text, String language) {
		Transaction tx = graphDb.beginTx();
		try {
			Node node = graphDb.createNode();
			node.setProperty("text", text);
			node.setProperty("language", language);
			labelNodeIndex.add(node, "text", text);
			labelNodeIndex.add(node, "language", language);
			return new LabelImpl(node);
		} finally {
			tx.success();
			tx.finish();
		}
	}

	private GraphDatabaseService graphDb;
	private Index<Node> labelNodeIndex;
	private static final Logger logger = LoggerFactory.getLogger(LabelRepositoryImpl.class);

}
