package org.biosemantics.datasource.common;

import java.util.Iterator;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Relationship;

public interface DataSourceReader {
	void init();

	void destroy();

	void setDefaultDomain(Concept domain);

	Iterator<ConceptDetail> getConcepts();

	Iterator<Relationship> getRelationships();

}
