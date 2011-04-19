package org.biosemantics.datasource.umls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipCategory;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.SemanticRelationshipCategory;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptLabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptRelationshipImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.datasource.umls.cache.UmlsCacheService;
import org.biosemantics.datasource.umls.concept.ConceptIterator;
import org.biosemantics.datasource.umls.concept.ConceptSchemeIterator;
import org.biosemantics.datasource.umls.concept.DomainIterator;
import org.biosemantics.datasource.umls.concept.PredicateIterator;
import org.biosemantics.datasource.umls.concept.UmlsUtils;
import org.biosemantics.datasource.umls.relationship.ConceptSchemeRelationshipIterator;
import org.biosemantics.datasource.umls.relationship.ConceptToSchemeRelationshipIterator;
import org.biosemantics.datasource.umls.relationship.FactualRealtionshipIterator;
import org.biosemantics.datasource.umls.relationship.UmlsRelationship;
import org.biosemantics.disambiguation.bulkimport.service.BulkImportService;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImportClient {

	private Map<String, String> domainMap = new HashMap<String, String>();
	private static final String[] CONTEXTS = new String[] { "import-context.xml" };
	private static final Logger logger = LoggerFactory.getLogger(ImportClient.class);

	private static final int BATCH = 10000;

	public static void main(String[] args) {
		ImportClient importClient = new ImportClient();
		importClient.init();
	}

	public void init() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXTS);

		BulkImportService bulkImportService = applicationContext.getBean(BulkImportService.class);
		UmlsCacheService umlsCacheService = applicationContext.getBean(UmlsCacheService.class);
		// DOMAIN
		DomainIterator domainIterator = applicationContext.getBean(DomainIterator.class);
		while (domainIterator.hasNext()) {
			Concept concept = domainIterator.next();
			String uuid = bulkImportService.createConcept(ConceptType.DOMAIN, concept);
			for (ConceptLabel label : concept.getLabels()) {
				if (label.getLabelType() == LabelType.ALTERNATE) {
					// required later
					umlsCacheService.addDomain(label.getText(), uuid);
				}
			}
		}
		// PREDICATES
		PredicateIterator predicateIterator = applicationContext.getBean(PredicateIterator.class);
		while (predicateIterator.hasNext()) {
			String predicate = predicateIterator.next();
			ConceptImpl conceptImpl = new ConceptImpl();
			conceptImpl.addConceptLabels(new ConceptLabelImpl(LanguageImpl.EN, predicate, LabelType.PREFERRED));
			String uuid = bulkImportService.createConcept(ConceptType.PREDICATE, conceptImpl);
			// required for relationships
			umlsCacheService.addPredicate(predicate, uuid);
		}

		// CONCEPT_SCHEME
		ConceptSchemeIterator conceptSchemeIterator = applicationContext.getBean(ConceptSchemeIterator.class);
		while (conceptSchemeIterator.hasNext()) {
			Concept concept = conceptSchemeIterator.next();
			String uuid = bulkImportService.createConcept(ConceptType.CONCEPT_SCHEME, concept);
			String text = null;
			Collection<ConceptLabel> labels = concept.getLabels();
			if (labels.size() == 1) {
				for (Label label : labels) {
					text = label.getText();
				}
				umlsCacheService.addConceptScheme(text, uuid);
			} else {
				logger.error("cannot have more than 1 label for conceptscheme");
				throw new IllegalStateException("");
			}
		}

		// CONCEPT SCHEME RELATIONSHIPS
		ConceptSchemeRelationshipIterator conceptSchemeRelationshipIterator = applicationContext
				.getBean(ConceptSchemeRelationshipIterator.class);
		while (conceptSchemeRelationshipIterator.hasNext()) {
			UmlsRelationship umlsRelationship = conceptSchemeRelationshipIterator.next();
			String fromConcept = umlsCacheService.getUuidforConceptSchemeText(umlsRelationship.getSubject());
			String predicateUuid = umlsCacheService.getUuidForPredicateText(umlsRelationship.getPredicate());
			String toConcept = umlsCacheService.getUuidforConceptSchemeText(umlsRelationship.getObject());
			ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromConcept, toConcept,
					predicateUuid, SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.AUTHORITATIVE, 1);
			bulkImportService.addRelationship(conceptRelationshipImpl);
		}

		// CONCEPTS
		ConceptIterator conceptIterator = applicationContext.getBean(ConceptIterator.class);
		int ctr = 0;
		while (conceptIterator.hasNext()) {
			Concept concept = conceptIterator.next();
			// update concepts with relevant domain uuids
			Collection<NotationImpl> notations = new ArrayList<NotationImpl>();
			String cui = null;
			for (Notation notation : concept.getNotations()) {
				if (notation.getDomainUuid().equals(UmlsUtils.DEFAULT_SAB)) {
					cui = notation.getCode();
				}
				final String domainUuid = getDomainUuid(notation.getDomainUuid());
				notations.add(new NotationImpl(domainUuid, notation.getCode()));
			}
			concept.getNotations().clear();
			concept.getNotations().addAll(notations);
			String uuid = bulkImportService.createConcept(ConceptType.CONCEPT, concept);
			// needed for relationships
			umlsCacheService.addCui(cui, uuid);
			if (++ctr % BATCH == 0) {
				logger.info("{}", ctr);
			}
		}

		// CONCEPT TO CONCEPT SCHEME RELATIONSHIPS
		ConceptToSchemeRelationshipIterator conceptToSchemeRelationshipIterator = applicationContext
				.getBean(ConceptToSchemeRelationshipIterator.class);
		while (conceptToSchemeRelationshipIterator.hasNext()) {
			UmlsRelationship umlsRelationship = conceptToSchemeRelationshipIterator.next();
			String toConcept = umlsCacheService.getUuidforConceptSchemeText(umlsRelationship.getObject());
			ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(
					umlsRelationship.getSubject(), toConcept, null, SemanticRelationshipCategory.HAS_BROADER_CONCEPT,
					ConceptRelationshipCategory.AUTHORITATIVE, 1);
			bulkImportService.addRelationship(conceptRelationshipImpl);
		}

		// CONCEPT CONCEPT FACTUAL RELATIONSHIPS
		FactualRealtionshipIterator factualRealtionshipIterator = applicationContext
				.getBean(FactualRealtionshipIterator.class);
		while (factualRealtionshipIterator.hasNext()) {
			UmlsRelationship umlsRelationship = factualRealtionshipIterator.next();
			String fromConcept = umlsCacheService.getUuidforCui(umlsRelationship.getSubject());
			String toConcept = umlsCacheService.getUuidforCui(umlsRelationship.getObject());
			SemanticRelationshipCategory semanticRelationshipCategory = UmlsUtils
					.getConceptRelationshipType(umlsRelationship.getPredicate());
			ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromConcept, toConcept, null,
					semanticRelationshipCategory, ConceptRelationshipCategory.AUTHORITATIVE, 1);
			bulkImportService.addRelationship(conceptRelationshipImpl);
		}

		// explicitly closing application context making sure destroy is called on all beans
		applicationContext.close();
		logger.info("application context closed.");

	}

	private String getDomainUuid(String domain) {
		String domainUuid = domainMap.get(domain);
		if (domainUuid == null) {
			logger.error("no domain found for {}", domain);
		}
		return domainUuid;
	}
}
