package org.biosemantics.disambiguation.service.impl;

import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Note.NoteType;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptLabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NoteImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;

public abstract class TestUtility {

	public static final ConceptImpl createFullConcept() {
		ConceptImpl concept1 = new ConceptImpl();
		ConceptLabelImpl label1 = new ConceptLabelImpl(LanguageImpl.DE, "deutch", LabelType.ALTERNATE);
		ConceptLabelImpl label2 = new ConceptLabelImpl(LanguageImpl.ES, "espanol", LabelType.ALTERNATE);
		ConceptLabelImpl label3 = new ConceptLabelImpl(LanguageImpl.ES, "espanol", LabelType.ALTERNATE);
		concept1.addConceptLabels(label1);
		concept1.addConceptLabels(label2);
		concept1.addConceptLabels(label3);
		NotationImpl notation1 = new NotationImpl("someDomain", "someCode");
		NotationImpl notation2 = new NotationImpl("otherDomain", "otherCode");
		NotationImpl notation3 = new NotationImpl("someDomain", "someCode");
		concept1.addNotations(notation1);
		concept1.addNotations(notation2);
		concept1.addNotations(notation3);
		NoteImpl definition = new NoteImpl(NoteType.DEFINITION, LanguageImpl.EN, "some definition");
		NoteImpl change = new NoteImpl(NoteType.CHANGE_NOTE, LanguageImpl.EN, "created concept");
		concept1.addNotes(definition);
		concept1.addNotes(change);
		return concept1;
	}

}
