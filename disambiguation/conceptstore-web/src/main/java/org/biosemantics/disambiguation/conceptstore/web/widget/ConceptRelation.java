package org.biosemantics.disambiguation.conceptstore.web.widget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptRelationship;
import org.biosemantics.conceptstore.common.service.ConceptRelationshipStorageService;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.disambiguation.conceptstore.web.ConceptStoreApplication;
import org.biosemantics.disambiguation.conceptstore.web.common.StorageUtility;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class ConceptRelation extends VerticalLayout {

	private Table relationsTable;
	private ConceptStorageService conceptStorageService;
	private ConceptRelationshipStorageService conceptRelationshipStorageService;
	private Map<String, String> conceptCache;
	private static final Logger logger = LoggerFactory.getLogger(ConceptRelation.class);
	private Concept concept;

	public Concept getConcept() {
		return concept;
	}

	public ConceptRelation() {
	}

	public void build(Concept concept) {
		this.concept = concept;
		conceptRelationshipStorageService = ((ConceptStoreApplication) this.getApplication()).getSpringServiceLocator()
				.getConceptRelationshipStorageService();
		Collection<ConceptRelationship> conceptRelationships = conceptRelationshipStorageService
				.getAllRelationshipsForConcept(concept.getUuid());
		if (!CollectionUtils.isEmpty(conceptRelationships)) {
			relationsTable = new Table("Relationships found:");
			relationsTable.setPageLength(10);
			relationsTable.setWidth("100%");
			relationsTable.addContainerProperty("From", String.class, null);
			relationsTable.addContainerProperty("Semantic Relationship Type", String.class, null);
			relationsTable.addContainerProperty("Relationship Type", String.class, null);
			relationsTable.addContainerProperty("To", String.class, null);
			conceptCache = new HashMap<String, String>();
			this.conceptStorageService = ((ConceptStoreApplication) this.getApplication()).getSpringServiceLocator()
					.getConceptStorageService();
			StringBuilder googleChartApiUrl = new StringBuilder(
					"http://chart.googleapis.com/chart?cht=gv:circo&chs=620x480&chl=digraph{");
			// HACK table data needs a uuid, generating one using a counter
			int ctr = 0;
			for (ConceptRelationship conceptRelationship : conceptRelationships) {
				String fromConceptPrefLabel = null;
				if (conceptCache.containsKey(conceptRelationship.fromConcept())) {
					fromConceptPrefLabel = conceptCache.get(conceptRelationship.fromConcept());
				} else {
					Concept retrievedConcept = conceptStorageService.getConcept(conceptRelationship.fromConcept());
					fromConceptPrefLabel = StorageUtility.getPreferredLabel(retrievedConcept, LanguageImpl.EN)
							.getText();
					conceptCache.put(retrievedConcept.getUuid(), fromConceptPrefLabel);
				}

				String toConceptPrefLabel = null;
				if (conceptCache.containsKey(conceptRelationship.toConcept())) {
					toConceptPrefLabel = conceptCache.get(conceptRelationship.toConcept());
				} else {
					Concept retrievedConcept = conceptStorageService.getConcept(conceptRelationship.toConcept());
					toConceptPrefLabel = StorageUtility.getPreferredLabel(retrievedConcept, LanguageImpl.EN).getText();
					conceptCache.put(retrievedConcept.getUuid(), toConceptPrefLabel);
				}
				try {
					googleChartApiUrl.append("\"").append(URLEncoder.encode(fromConceptPrefLabel, "UTF-8"))
							.append("\"").append("->").append("\"")
							.append(URLEncoder.encode(toConceptPrefLabel, "UTF-8")).append("\"");
				} catch (UnsupportedEncodingException e) {
					logger.error("error encoding", e);
				}
				String[] line = new String[] { fromConceptPrefLabel,
						conceptRelationship.getSemanticRelationshipCategory().name(), toConceptPrefLabel };
				relationsTable.addItem(
						new Object[] { fromConceptPrefLabel, conceptRelationship.getSemanticRelationshipCategory(),
								conceptRelationship.getConceptRelationshipCategory(), toConceptPrefLabel }, ++ctr);
			}
			this.addComponent(relationsTable);
			googleChartApiUrl.append("}");
			logger.debug("{} relations found.", ctr);
			logger.debug("{}", googleChartApiUrl);
			ExternalResource externalResource = new ExternalResource(googleChartApiUrl.toString());
			Embedded embeddedImage = new Embedded("", externalResource);
			embeddedImage.setType(Embedded.TYPE_IMAGE);
			this.addComponent(embeddedImage);
		}
	}
}
