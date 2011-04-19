package org.biosemantics.datasource.umls.cache;

public interface UmlsCacheService {

	void addDomain(String domainName, String domainUuid);

	String getDomainUuidByName(String domainName);

	void addCui(String cui, String conceptUuid);

	String getUuidforCui(String cui);

	void addPredicate(String text, String uuid);

	String getUuidForPredicateText(String text);
	
	void addConceptScheme(String text, String uuid);
	
	String getUuidforConceptSchemeText(String text);

}
