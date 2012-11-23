package org.biosemantics.conceptstore.repository.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.domain.impl.NotationImpl;
import org.biosemantics.conceptstore.repository.NotationRepository;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotationRepositoryImpl implements NotationRepository {

	public NotationRepositoryImpl(GraphDatabaseService graphDatabaseService) {
		this.graphDb = graphDatabaseService;
		notationNodeIndex = this.graphDb.index().forNodes("Notation");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.biosemantics.conceptstore.repository.impl.NotationRepository#create
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public Notation create(String source, String code) {
		return createNewNotation(source, code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.biosemantics.conceptstore.repository.impl.NotationRepository#getById
	 * (long)
	 */
	@Override
	public Notation getById(long id) {
		Node node = graphDb.getNodeById(id);
		if (node == null) {
			throw new IllegalArgumentException("no node for id = " + id);
		} else {
			return new NotationImpl(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.biosemantics.conceptstore.repository.impl.NotationRepository#getByCode
	 * (java.lang.String)
	 */
	@Override
	public Collection<Notation> getByCode(String code) {
		IndexHits<Node> hits = notationNodeIndex.get("code", code);
		Set<Notation> notations = new HashSet<Notation>();
		for (Node node : hits) {
			notations.add(new NotationImpl(node));
		}
		return notations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.biosemantics.conceptstore.repository.impl.NotationRepository#getOrCreate
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public Notation getOrCreate(String source, String code) {
		IndexHits<Node> hits = notationNodeIndex.get("code", code);
		Notation foundNotation = null;
		if (hits != null && hits.size() > 0) {
			for (Node hit : hits) {
				String foundSource = (String) hit.getProperty("source");
				if (foundSource.equals(source)) {
					foundNotation = new NotationImpl(hit);
					break;
				}
			}
		}
		if (foundNotation == null) {
			return createNewNotation(source, code);
		} else {
			return foundNotation;
		}
	}

	private Notation createNewNotation(String source, String code) {
		Transaction tx = graphDb.beginTx();
		try {
			Node node = graphDb.createNode();
			node.setProperty("source", source);
			node.setProperty("code", code);
			notationNodeIndex.add(node, "source", source);
			notationNodeIndex.add(node, "code", code);
			return new NotationImpl(node);
		} finally {
			tx.success();
			tx.finish();
		}
	}

	private GraphDatabaseService graphDb;
	private Index<Node> notationNodeIndex;
	private static final Logger logger = LoggerFactory.getLogger(NotationRepositoryImpl.class);

}
