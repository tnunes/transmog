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
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.datasource.umls.concept.ConceptIterator;
import org.biosemantics.datasource.umls.concept.DomainIterator;
import org.biosemantics.disambiguation.bulkimport.service.BulkImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

public class ImportClient {

	private Map<String, String> domainMap = new HashMap<String, String>();
	private static final String[] CONTEXTS = new String[] { "import-context.xml" };
	private static final Logger logger = LoggerFactory.getLogger(ImportClient.class);
	
	private static final int BATCH = 1000;

	public static void main(String[] args) {
		ImportClient importClient = new ImportClient();
		importClient.init();
	}

	@Transactional
	public void init() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXTS);
		DomainIterator domainIterator = applicationContext.getBean(DomainIterator.class);
		BulkImportService bulkImportService = applicationContext.getBean(BulkImportService.class);

		while (domainIterator.hasNext()) {
			Concept concept = domainIterator.next();
			String uuid = bulkImportService.createConcept(ConceptType.DOMAIN, concept);
			for (ConceptLabel label : concept.getLabels()) {
				if (label.getLabelType() == LabelType.ALTERNATE) {
					domainMap.put(label.getText(), uuid);
				}
			}
		}

		ConceptIterator conceptIterator = applicationContext.getBean(ConceptIterator.class);
		int ctr = 0;
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
			bulkImportService.createConcept(ConceptType.CONCEPT, concept);
			if(++ctr % BATCH == 0){
				logger.info("{}",ctr);
			}
		}

	}
}
