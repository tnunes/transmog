package org.biosemantics.disambiguation.service.local.impl;

import java.util.Iterator;

import org.biosemantics.conceptstore.common.domain.extn.RelatedConcept;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;

public class RelatedConceptIterator implements Iterator<RelatedConcept> {

	private Traverser traverser;

	public RelatedConceptIterator(Traverser traverser) {
		this.traverser = traverser;
	}

	@Override
	public boolean hasNext() {
		return traverser.iterator().hasNext();
	}

	@Override
	public RelatedConcept next() {
		Node node = traverser.iterator().next();
		TraversalPosition currentPosition = traverser.currentPosition();
		RelatedConcept childConcept = new RelatedConcept(new ConceptImpl(node), currentPosition.depth());
		return childConcept;
	}

	@Override
	public void remove() {
		traverser.iterator().remove();
	}

}
