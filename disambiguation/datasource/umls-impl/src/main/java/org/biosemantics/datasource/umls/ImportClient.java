package org.biosemantics.datasource.umls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.datasource.umls.concept.ConceptIterator;
import org.biosemantics.datasource.umls.concept.DomainIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImportClient {

	private Map<String, String> domainMap = new HashMap<String, String>();
	private static final String[] CONTEXTS = new String[] { "import-context.xml" };
	private static final Logger logger = LoggerFactory.getLogger(ImportClient.class);

	public static void main(String[] args) {
		ImportClient importClient = new ImportClient();
		importClient.init();
	}

	public void init() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXTS);
		DomainIterator domainIterator = applicationContext.getBean(DomainIterator.class);
		ConceptStorageService conceptStorageService = applicationContext.getBean(ConceptStorageService.class);

		while (domainIterator.hasNext()) {
			Concept concept = domainIterator.next();
			String uuid = conceptStorageService.createConcept(ConceptType.DOMAIN, concept);
			for (ConceptLabel label : concept.getLabels()) {
				if (label.getLabelType() == LabelType.ALTERNATE) {
					domainMap.put(label.getText(), uuid);
				}
			}
		}

		ConceptIterator conceptIterator = applicationContext.getBean(ConceptIterator.class);
		while (conceptIterator.hasNext()) {
			Concept concept = conceptIterator.next();
			// update concepts with relevant domain uuids
			Collection<NotationImpl> notations = new ArrayList<NotationImpl>();
			for (Notation notation : concept.getNotations()) {
				String domainUuid = domainMap.get(notation.getDomainUuid());
				if (domainUuid == null) {
					logger.error("no domain found for {}", notation.getDomainUuid());
				}
				notations.add(new NotationImpl(domainUuid, notation.getCode()));
			}
			concept.getNotations().clear();
			concept.getNotations().addAll(notations);
			conceptStorageService.createConcept(ConceptType.CONCEPT, concept);
		}

	}
}
