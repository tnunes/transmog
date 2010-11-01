package org.biosemantics.disambiguation.datasource.writer.api;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Relationship;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.datasource.common.ConceptDetail;
import org.biosemantics.datasource.common.DataSourceWriter;

public class DataSourceWriterApiImpl implements DataSourceWriter {

	private ConceptStorageService conceptStorageService;
	
	
	
	public void setConceptStorageService(ConceptStorageService conceptStorageService) {
		this.conceptStorageService = conceptStorageService;
	}

	@Override
	public boolean writeConcept(ConceptDetail conceptDetail) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean writeRelationship(Relationship relationship) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
