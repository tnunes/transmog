package org.biosemantics.eviped.tools.service.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biosemantics.eviped.tools.service.Annotation;
import org.biosemantics.eviped.tools.service.AnnotationType;
import org.biosemantics.utility.peregrine.PeregrineRmiClient;
import org.erasmusmc.data_mining.ontology.api.Concept;
import org.erasmusmc.data_mining.ontology.api.Language;
import org.erasmusmc.data_mining.ontology.api.SemanticType;
import org.erasmusmc.data_mining.peregrine.api.IndexingResult;

public class DrugImpl implements AttributeExtractorService {

	private static final String DRUG_SEMANTIC_TYPE = "197";
	private PeregrineRmiClient peregrineRmiClient;

	public DrugImpl(PeregrineRmiClient peregrineRmiClient) {
		this.peregrineRmiClient = peregrineRmiClient;
	}

	@Override
	public List<Annotation> getAnnotations(String text, int sentenceNumber) {
		List<IndexingResult> indexingResults = peregrineRmiClient.getPeregrine().index(text, Language.EN);
		List<Annotation> annotations = new ArrayList<Annotation>();
		for (IndexingResult indexingResult : indexingResults) {
			int conceptId = (Integer) indexingResult.getTermId().getConceptId();
			Concept concept = peregrineRmiClient.getOntology().getConcept(conceptId);
			Collection<SemanticType> semanticTypes = concept.getSemanticTypes();
			for (SemanticType semanticType : semanticTypes) {
				if (semanticType.getId().equalsIgnoreCase(DRUG_SEMANTIC_TYPE)) {
					String term = peregrineRmiClient.getOntology().getTerm(indexingResult.getTermId()).getText();
					Annotation annotation = new Annotation(AnnotationType.DRUG, indexingResult.getStartPos(),
							indexingResult.getEndPos(), 0, term);
					annotations.add(annotation);
					break;
				}
			}
		}
		return annotations;
	}

}
