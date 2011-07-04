package org.biosemantics.disambiguation.umls.wsd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptRelationship;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.service.ConceptRelationshipStorageService;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class RelatedCuiReader {

	private ConceptRelationshipStorageService conceptRelationshipStorageService;
	private ConceptStorageService conceptStorageService;
	private static final String UMLS_DOMAIN_UUID = "VUPZXhdrymhfkvMM";
	private static final Logger logger = LoggerFactory.getLogger(RelatedCuiReader.class);

	public RelatedCuiReader(ApplicationContext applicationContext) {
		conceptRelationshipStorageService = applicationContext.getBean(ConceptRelationshipStorageService.class);
		conceptStorageService = applicationContext.getBean(ConceptStorageService.class);
	}

	public RelatedConcept getRelatedConcept(String cui) {

		Collection<String> uuids = conceptStorageService.getConceptsByNotation(new NotationImpl(UMLS_DOMAIN_UUID, cui));
		Map<String, ConceptRelationship> cuiConceptRelationshipMap = new HashMap<String, ConceptRelationship>();
		// logically this should be a unique record i.e. collection of size 1.
		if (uuids.size() == 1) {
			for (String uuid : uuids) {
				Collection<ConceptRelationship> conceptRelationships = conceptRelationshipStorageService
						.getAllRelationshipsForConcept(uuid);
				for (ConceptRelationship conceptRelationship : conceptRelationships) {
					String otherUuid = conceptRelationship.fromConcept().equals(uuid) ? conceptRelationship.toConcept()
							: conceptRelationship.fromConcept();
					Concept concept = conceptStorageService.getConcept(otherUuid);
					for (Notation notation : concept.getNotations()) {
						if (notation.getDomainUuid().equals(UMLS_DOMAIN_UUID)) {
							cuiConceptRelationshipMap.put(notation.getCode(), conceptRelationship);
							break;
						}
					}
				}

			}
		} else {
			logger.error("multiple concepts found for cui {}", cui);
		}
		RelatedConcept relatedConcept = new RelatedConcept(cui, cuiConceptRelationshipMap);
		StringBuilder allKeys = new StringBuilder();
		for (String string : cuiConceptRelationshipMap.keySet()) {
			allKeys.append(string).append(" | ");
		}
		logger.debug("all related cuis for cui {} are:{}", new Object[] { cui, allKeys.toString() });
		return relatedConcept;
	}
}
