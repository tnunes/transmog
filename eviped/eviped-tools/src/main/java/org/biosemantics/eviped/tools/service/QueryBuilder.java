package org.biosemantics.eviped.tools.service;

import java.util.Collection;

import javax.xml.bind.JAXBException;

public interface QueryBuilder {

	Collection<QueryResult> searchMedline(String drugName) throws JAXBException;
	
	Collection<Article> getArticles(String drugName) throws JAXBException;
	
}
