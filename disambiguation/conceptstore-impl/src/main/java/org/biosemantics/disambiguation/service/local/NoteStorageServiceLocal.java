package org.biosemantics.disambiguation.service.local;

import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.common.service.NoteStorageService;
import org.neo4j.graphdb.Node;

public interface NoteStorageServiceLocal extends NoteStorageService {

	Node createNoteNode(Note note);

}
