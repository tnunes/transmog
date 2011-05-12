package org.biosemantics.disambiguation.conceptstore.web.widget;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.common.domain.Note.NoteType;
import org.springframework.util.CollectionUtils;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class ConceptDetail extends VerticalLayout {

	private Concept concept;

	public ConceptDetail(Concept concept) {
		this.concept = concept;
		Collection<Note> notes = concept.getNotes();
		if (!CollectionUtils.isEmpty(notes)) {
			this.addComponent(getNoteComponent(notes));
		}
		Collection<ConceptLabel> conceptLabels = concept.getLabels();
		this.addComponent(getLabelComponent(conceptLabels));
		Collection<Notation> notations = concept.getNotations();
		if (!CollectionUtils.isEmpty(notations)) {
			this.addComponent(getNotationComponent(notations));
		}
		this.setSpacing(true);
		this.setMargin(true);
	}

	private Component getLabelComponent(Collection<ConceptLabel> conceptLabels) {
		Table labelTable = new Table("Labels:");
		labelTable.setPageLength(10);
		labelTable.setWidth("100%");
		labelTable.addContainerProperty("Label Text", String.class, null);
		labelTable.addContainerProperty("Language", String.class, null);
		labelTable.addContainerProperty("Label Type", String.class, null);
		for (ConceptLabel conceptLabel : conceptLabels) {
			Object label = labelTable.addItem(new Object[] { conceptLabel.getText(),
					conceptLabel.getLanguage().getIso6392Code(), conceptLabel.getLabelType().name() },
					conceptLabel.getUuid());
			System.err.println("lable: " + label);
		}
		return labelTable;
	}

	private Component getNotationComponent(Collection<Notation> notations) {
		Table notationTable = new Table("Notations:");
		notationTable.setWidth("100%");
		notationTable.setPageLength(10);
		notationTable.addContainerProperty("Notation Code", String.class, null);
		notationTable.addContainerProperty("Domain", String.class, null);
		for (Notation notation : notations) {
			Object not = notationTable.addItem(new Object[] { notation.getCode(), notation.getDomainUuid() },
					notation.getUuid());
			System.err.println("not: " + not);
		}
		return notationTable;
	}

	private Component getNoteComponent(Collection<Note> notes) {
		Label label = new Label("Definitions");
		label.setWidth("100%");
		label.setContentMode(Label.CONTENT_XHTML);
		StringBuilder definitions = new StringBuilder();
		for (Note note : notes) {
			if (note.getNoteType() == NoteType.DEFINITION) {
				definitions.append("<p>").append(note.getText()).append("</p>").append("</br>");
			}
		}
		label.setValue(definitions.toString());
		return label;
	}

	public Concept getConcept() {
		return concept;
	}

}
