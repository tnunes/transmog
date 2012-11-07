package org.biosemantics.conceptstore.repository;

import org.biosemantics.conceptstore.domain.Label;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface LabelRepository extends GraphRepository<Label> {

	@Query("start label=node:Label(text={0}) where label.language = {1} return label")
	Label getLabel(String text, String language);

}
