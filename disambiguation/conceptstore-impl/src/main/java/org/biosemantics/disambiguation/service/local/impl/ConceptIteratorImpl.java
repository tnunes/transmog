package org.biosemantics.disambiguation.service.local.impl;

import java.util.Iterator;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;

public class ConceptIteratorImpl implements Iterator<Concept>, Iterable<Concept> {

	private IndexHits<Node> indexHits;

	public ConceptIteratorImpl(IndexHits<Node> nodes) {
		this.indexHits = nodes;
	}

	@Override
	public boolean hasNext() {
		return indexHits.hasNext();
	}

	@Override
	public Concept next() {
		return new ConceptImpl(indexHits.next());
	}

	@Override
	public void remove() {
		indexHits.remove();

	}

	@Override
	public Iterator<Concept> iterator() {
		return this;
	}

}
