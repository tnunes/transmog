package org.biosemantics.disambiguation.manager.adapter;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.common.domain.Note.NoteType;
import org.biosemantics.disambiguation.manager.common.CommonUtility;
import org.biosemantics.disambiguation.manager.dto.QueryResult;
import org.springframework.util.CollectionUtils;

public class QueryResultAdapterImpl implements QueryResultAdapter {

	@Override
	public QueryResult adapt(Concept concept) {
		QueryResult queryResult = new QueryResult();
		queryResult.setId(concept.getUuid());
		Label label = CommonUtility.getPreferredLabel(concept.getLabelsByType(LabelType.PREFERRED));
		queryResult.setLabel(label.getText());
		queryResult.setLanguage(label.getLanguage().name());
		Collection<Note> notes = concept.getNotes();
		if (!CollectionUtils.isEmpty(notes)) {
			for (Note note : notes) {
				if (note.getNoteType() == NoteType.DEFINITION) {
					queryResult.setDescription(note.getText());
				}
			}
		}
		return queryResult;

	}

}
