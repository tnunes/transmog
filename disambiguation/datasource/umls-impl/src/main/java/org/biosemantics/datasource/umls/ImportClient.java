package org.biosemantics.datasource.umls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipCategory;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.SemanticRelationshipCategory;
import org.biosemantics.conceptstore.common.service.ConceptRelationshipStorageService;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptLabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptRelationshipImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.datasource.umls.concept.ConceptIterator;
import org.biosemantics.datasource.umls.concept.ConceptSchemeIterator;
import org.biosemantics.datasource.umls.concept.DomainIterator;
import org.biosemantics.datasource.umls.concept.PredicateIterator;
import org.biosemantics.datasource.umls.concept.UmlsUtils;
import org.biosemantics.datasource.umls.relationship.ConceptSchemeRelationshipIterator;
import org.biosemantics.datasource.umls.relationship.ConceptToSchemeRelationshipIterator;
import org.biosemantics.datasource.umls.relationship.FactualRealtionshipIterator;
import org.biosemantics.datasource.umls.relationship.UmlsRelationship;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.CollectionUtils;

public class ImportClient {

	private static final String[] CONTEXTS = new String[] { "import-context.xml" };
	private static final Logger logger = LoggerFactory.getLogger(ImportClient.class);
	private static final int BATCH = 10000;
	private Map<String, String> domainNameUuidMap = new HashMap<String, String>();
	private Map<String, String> conceptSchemeNameUuidMap = new HashMap<String, String>();
	private Map<String, String> predicateNameUuidMap = new HashMap<String, String>();
	private final ClassPathXmlApplicationContext applicationContext;
	private final ConceptStorageService conceptStorageService;
	private final ConceptRelationshipStorageService conceptRelationshipStorageService;

	public static void main(String[] args) {
		ImportClient importClient = new ImportClient();
		importClient.init();
	}

	public ImportClient() {
		applicationContext = new ClassPathXmlApplicationContext(CONTEXTS);
		conceptStorageService = applicationContext.getBean(ConceptStorageService.class);
		conceptRelationshipStorageService = applicationContext.getBean(ConceptRelationshipStorageService.class);
	}

	public void init() {
		int ctr = 0;
		// DOMAIN
		logger.info("creating domain concepts");
		DomainIterator domainIterator = applicationContext.getBean(DomainIterator.class);
		while (domainIterator.hasNext()) {
			Concept concept = domainIterator.next();
			String uuid = conceptStorageService.createConcept(ConceptType.DOMAIN, concept);
			for (ConceptLabel label : concept.getLabels()) {
				if (label.getLabelType() == LabelType.ALTERNATE) {
					// required later
					domainNameUuidMap.put(label.getText(), uuid);
				}
			}
			ctr++;
		}
		logger.info("{} domin concepts created", ctr);

		// PREDICATES
		ctr = 0;
		logger.info("creating predicate concepts");
		PredicateIterator predicateIterator = applicationContext.getBean(PredicateIterator.class);

		while (predicateIterator.hasNext()) {
			String predicate = predicateIterator.next();
			ConceptImpl conceptImpl = new ConceptImpl();
			conceptImpl.addConceptLabels(new ConceptLabelImpl(LanguageImpl.EN, predicate, LabelType.PREFERRED));
			String uuid = conceptStorageService.createConcept(ConceptType.PREDICATE, conceptImpl);
			predicateNameUuidMap.put(predicate, uuid);
			ctr++;
		}
		logger.info("{} predicate concepts created", ctr);

		// CONCEPT_SCHEME
		ctr = 0;
		logger.info("creating concept scheme concepts");
		ConceptSchemeIterator conceptSchemeIterator = applicationContext.getBean(ConceptSchemeIterator.class);
		while (conceptSchemeIterator.hasNext()) {
			Concept concept = conceptSchemeIterator.next();
			String uuid = conceptStorageService.createConcept(ConceptType.CONCEPT_SCHEME, concept);
			String text = null;
			Collection<ConceptLabel> labels = concept.getLabels();
			if (labels.size() == 1) {
				for (Label label : labels) {
					text = label.getText();
				}
				conceptSchemeNameUuidMap.put(text, uuid);
			} else {
				logger.error("cannot have more than 1 label for conceptscheme");
				throw new IllegalStateException("");
			}
			ctr++;
		}
		logger.info("{} concept scheme created", ctr);

		// CONCEPT SCHEME RELATIONSHIPS
		ctr = 0;
		logger.info("creating concept scheme relationships");
		ConceptSchemeRelationshipIterator conceptSchemeRelationshipIterator = applicationContext
				.getBean(ConceptSchemeRelationshipIterator.class);
		while (conceptSchemeRelationshipIterator.hasNext()) {
			UmlsRelationship umlsRelationship = conceptSchemeRelationshipIterator.next();

			String fromConcept = conceptSchemeNameUuidMap.get(umlsRelationship.getSubject());
			String predicateUuid = predicateNameUuidMap.get(umlsRelationship.getPredicate());
			String toConcept = conceptSchemeNameUuidMap.get(umlsRelationship.getObject());
			if (fromConcept != null && toConcept != null && predicateUuid != null) {
				ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromConcept, toConcept,
						predicateUuid, SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.AUTHORITATIVE,
						1);
				if (!conceptRelationshipStorageService.relationshipExists(conceptRelationshipImpl)) {
					conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);
				}
			} else {
				logger.error("null value found for umlsRelationship {}", umlsRelationship.toString());
			}
			ctr++;
		}
		logger.info("{} concept schemes created", ctr);

		// CONCEPTS
		ctr = 0;
		logger.info("creating concepts");
		ConceptIterator conceptIterator = applicationContext.getBean(ConceptIterator.class);
		StopWatch stopWatch = new StopWatch();
		while (conceptIterator.hasNext()) {
			stopWatch.start();
			Concept concept = conceptIterator.next();
			// update concepts with relevant domain uuids: we are getting domainNames, we need uuids
			Collection<NotationImpl> notations = new ArrayList<NotationImpl>();
			for (Notation notation : concept.getNotations()) {
				final String domainUuid = domainNameUuidMap.get(notation.getDomainUuid());
				notations.add(new NotationImpl(domainUuid, notation.getCode()));
			}
			concept.getNotations().clear();
			concept.getNotations().addAll(notations);
			String uuid = conceptStorageService.createConcept(ConceptType.CONCEPT, concept);
			if (++ctr % BATCH == 0) {
				logger.debug("CONCEPTS {}", ctr);
			}
			stopWatch.stop();
			logger.debug("created concept uuid:{} time:{}", new Object[] { uuid, stopWatch.getTime() });
			stopWatch.reset();
		}
		logger.info("{} concepts created", ctr);

		// CONCEPT TO CONCEPT SCHEME RELATIONSHIPS
		logger.info("creating concept to concept scheme relationships");
		ctr = 0;
		ConceptToSchemeRelationshipIterator conceptToSchemeRelationshipIterator = applicationContext
				.getBean(ConceptToSchemeRelationshipIterator.class);
		while (conceptToSchemeRelationshipIterator.hasNext()) {
			stopWatch.start();
			UmlsRelationship umlsRelationship = conceptToSchemeRelationshipIterator.next();
			String domainUuid = domainNameUuidMap.get(UmlsUtils.DEFAULT_SAB);
			NotationImpl notationImpl = new NotationImpl(domainUuid, umlsRelationship.getSubject());
			String fromConcept = getConceptUuidForNotation(notationImpl);
			if (fromConcept == null) {
				logger.error("since fromConceptUuid is null, continuing");
				continue;
			}

			String toConcept = conceptSchemeNameUuidMap.get(umlsRelationship.getObject());
			if (toConcept == null) {
				logger.error("moving to next: no concept uuid found for concept sceheme label {}",
						umlsRelationship.getObject());
				continue;
			}

			ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromConcept, toConcept, null,
					SemanticRelationshipCategory.HAS_BROADER_CONCEPT, ConceptRelationshipCategory.AUTHORITATIVE, 1);
			if (!conceptRelationshipStorageService.relationshipExists(conceptRelationshipImpl)) {
				conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);
			}
			if (++ctr % BATCH == 0) {
				logger.info("CONCEPT TO CONCEPT SCHEME RELATIONSHIPS {}", ctr);
			}
			stopWatch.stop();
			logger.debug("added concept to scheme rlsp time:{}", stopWatch.getTime());
			stopWatch.reset();
		}
		logger.info(" {} concept to concept scheme relationships created", ctr);

		// CONCEPT CONCEPT FACTUAL RELATIONSHIPS
		ctr = 0;
		FactualRealtionshipIterator factualRealtionshipIterator = applicationContext
				.getBean(FactualRealtionshipIterator.class);
		while (factualRealtionshipIterator.hasNext()) {
			stopWatch.start();
			UmlsRelationship umlsRelationship = factualRealtionshipIterator.next();
			NotationImpl fromNotationImpl = new NotationImpl(domainNameUuidMap.get(UmlsUtils.DEFAULT_SAB),
					umlsRelationship.getSubject());
			String fromConcept = getConceptUuidForNotation(fromNotationImpl);
			if (fromConcept == null) {
				logger.error("since fromConceptUuid is null, continuing");
				continue;
			}
			NotationImpl toNotationImpl = new NotationImpl(domainNameUuidMap.get(UmlsUtils.DEFAULT_SAB),
					umlsRelationship.getObject());
			String toConcept = getConceptUuidForNotation(toNotationImpl);
			if (toConcept == null) {
				logger.error("since toConceptUuid is null, continuing");
				continue;
			}
			SemanticRelationshipCategory semanticRelationshipCategory = UmlsUtils
					.getConceptRelationshipType(umlsRelationship.getPredicate());
			ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromConcept, toConcept, null,
					semanticRelationshipCategory, ConceptRelationshipCategory.AUTHORITATIVE, 1);
			if (!conceptRelationshipStorageService.relationshipExists(conceptRelationshipImpl)) {
				conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);
			}
			if (++ctr % BATCH == 0) {
				logger.info("CONCEPT CONCEPT FACTUAL RELATIONSHIPS {}", ctr);
			}
			stopWatch.stop();
			logger.debug("added concept to concept rlsp time:{}", stopWatch.getTime());
			stopWatch.reset();
		}
		logger.info(" {} concept to concept factual relationships created", ctr);

		// explicitly closing application context making sure destroy is called on all beans
		applicationContext.close();
		logger.info("application context closed.");

	}

	private String getConceptUuidForNotation(Notation notation) {
		Collection<String> uuids = conceptStorageService.getConceptsByNotation(notation);
		if (CollectionUtils.isEmpty(uuids)) {
			logger.error(" no concept uuid found for notation {}", notation);
			return null;
		}
		if (uuids.size() > 1) {
			logger.error(" multiple concept uuid found for notation {}", notation);
			return null;
		}
		String uuid = null;
		for (String string : uuids) {
			uuid = string;
		}
		return uuid;

	}
}
