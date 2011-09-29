package org.biosemantics.disambiguation.umls.wsd;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.service.NotationStorageService;
import org.biosemantics.disambiguation.common.PropertyConstant;
import org.biosemantics.disambiguation.service.local.AlgorithmServiceLocal;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Required;

public class SenseScoreServiceGraphAlgoImpl implements SenseScoreService {

	private AlgorithmServiceLocal algorithmServiceLocal;
	private NotationStorageService notationStorageService;

	@Required
	public void setAlgorithmServiceLocal(AlgorithmServiceLocal algorithmServiceLocal) {
		this.algorithmServiceLocal = algorithmServiceLocal;
	}

	@Required
	public void setNotationStorageService(NotationStorageService notationStorageService) {
		this.notationStorageService = notationStorageService;
	}

	@Override
	public double getSenseScore(String cui, String contextCui) {
		double score = 0;
		Collection<Concept> concepts = notationStorageService.getAllRelatedConcepts(cui);
		String fromConceptUuid = null;
		if (CollectionUtils.isEmpty(concepts) && concepts.size() != 1) {
			throw new IllegalStateException("multiple / zero concepts found for cui = " + cui);
		}
		for (Concept concept : concepts) {
			fromConceptUuid = concept.getUuid();
		}

		String toConceptUuid = null;
		Collection<Concept> toConcepts = notationStorageService.getAllRelatedConcepts(contextCui);
		if (CollectionUtils.isEmpty(toConcepts) && toConcepts.size() != 1) {
			throw new IllegalStateException("multiple / zero concepts found for contextcui = " + contextCui);
		}
		for (Concept concept : toConcepts) {
			toConceptUuid = concept.getUuid();
		}
		Path path = algorithmServiceLocal.dijkstra(fromConceptUuid, toConceptUuid);

		for (Relationship relationship : path.relationships()) {
			score += (Integer) relationship.getProperty(PropertyConstant.WEIGHT.name());
		}
		return score;
	}
}
