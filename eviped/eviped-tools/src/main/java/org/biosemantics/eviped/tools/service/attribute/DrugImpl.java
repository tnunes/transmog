package org.biosemantics.eviped.tools.service.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.biosemantics.eviped.tools.service.Annotation;
import org.biosemantics.eviped.tools.service.AnnotationTypeConstant;
import org.biosemantics.utility.peregrine.PeregrineRmiClient;
import org.erasmusmc.data_mining.ontology.api.Concept;
import org.erasmusmc.data_mining.ontology.api.Language;
import org.erasmusmc.data_mining.ontology.api.SemanticType;
import org.erasmusmc.data_mining.peregrine.api.IndexingResult;

public class DrugImpl implements AttributeExtractorService {

	private static final String[] DRUG_SEMANTIC_TYPES = new String[] { "195", "123", "122", "118", "103", "120", "104",
			"200", "111", "196", "126", "131", "125", "129", "130", "197", "119", "124", "114", "109", "115", "121",
			"110", "127", "192" };
	private Map<String, Object> drugMap = new HashMap<String, Object>();
	private PeregrineRmiClient peregrineRmiClient;

	public DrugImpl(PeregrineRmiClient peregrineRmiClient) {
		this.peregrineRmiClient = peregrineRmiClient;
		for (String drugSemanticType : DRUG_SEMANTIC_TYPES) {
			drugMap.put(drugSemanticType, null);
		}
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
				if (isDrug(semanticType.getId())) {
					String term = peregrineRmiClient.getOntology().getTerm(indexingResult.getTermId()).getText();
					Annotation annotation = new Annotation(AnnotationTypeConstant.DRUG_NAME,
							indexingResult.getStartPos(), indexingResult.getEndPos(), 0, term);
					annotations.add(annotation);
					break;
				}
			}
		}
		return annotations;
	}

	private boolean isDrug(String id) {
		return drugMap.containsKey(id);
	}

}
