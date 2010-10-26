package org.biosemantics.disambiguation.manager.common;

import java.util.ArrayList;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.common.service.LabelStorageService;
import org.biosemantics.conceptstore.common.service.NotationStorageService;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DummyDataProviderImpl {

	private LabelStorageService labelStorageService;
	private NotationStorageService notationStorageService;
	private ConceptStorageService conceptStorageService;
	private UuidGeneratorService uuidGeneratorService;
	private boolean loadData = true;
	private int dataSize = 1000;
	private static final Logger logger = LoggerFactory.getLogger(DummyDataProviderImpl.class);

	@Required
	public void setLabelStorageService(LabelStorageService labelStorageService) {
		this.labelStorageService = labelStorageService;
	}

	@Required
	public void setNotationStorageService(NotationStorageService notationStorageService) {
		this.notationStorageService = notationStorageService;
	}

	@Required
	public void setConceptStorageService(ConceptStorageService conceptStorageService) {
		this.conceptStorageService = conceptStorageService;
	}

	@Required
	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = uuidGeneratorService;
	}

	public void setLoadData(boolean loadData) {
		this.loadData = loadData;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	public void init() {
		if (loadData) {
			logger.info("----------STARTING DATA LOAD----------");
			Concept domain = createDomain();
			List<Label> labels = new ArrayList<Label>();
			List<Notation> notations = new ArrayList<Notation>();
			for (int i = 0; i < dataSize; i++) {
				LabelImpl labelImpl = new LabelImpl(LabelType.PREFERRED, uuidGeneratorService.generateRandomUuid(),
						Language.EN);
				labels.add(labelStorageService.createLabel(labelImpl));
				NotationImpl notationImpl = new NotationImpl(domain, uuidGeneratorService.generateRandomUuid());
				notations.add(notationStorageService.createNotation(notationImpl));
				conceptStorageService.createConcept(new ConceptImpl.Builder(labels, DummyDataProviderImpl.class
						.getName()).notations(notations).build());
				labels.clear();
				notations.clear();
				
			}
			logger.info("----------DATA LOAD COMPLETED----------");

		}
	}

	private Concept createDomain() {
		Label label = labelStorageService.createLabel(new LabelImpl(LabelType.PREFERRED, "UMLS", Language.EN));
		ConceptImpl conceptImpl = new ConceptImpl.Builder("", label).build();
		return conceptStorageService.createDomain(conceptImpl);

	}
}
