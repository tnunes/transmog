package org.biosemantics.disambiguation.manager.adapter;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.common.domain.Note.NoteType;
import org.biosemantics.disambiguation.manager.dto.QueryResult;
import org.springframework.util.CollectionUtils;

public class QueryResultAdapterImpl implements QueryResultAdapter {

	@Override
	public QueryResult adapt(Concept concept) {
		QueryResult queryResult = new QueryResult();
		queryResult.setId(concept.getUuid());
		for (Label label : concept.getLabels()) {
			if (label.getLabelType() == LabelType.PREFERRED) {
				queryResult.setLabel(label.getText());
				queryResult.setLanguage(label.getLanguage().name());
				break;
			}
		}
		if (!CollectionUtils.isEmpty(concept.getNotes())) {
			for (Note note : concept.getNotes()) {
				if (note.getNoteType() == NoteType.DEFINITION) {
					queryResult.setDescription(note.getText());
				}
			}
		}
		return queryResult;

	}

}