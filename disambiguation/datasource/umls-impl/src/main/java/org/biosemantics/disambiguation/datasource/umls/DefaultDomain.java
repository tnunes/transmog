package org.biosemantics.disambiguation.datasource.umls;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;

public class DefaultDomain {

	public static Concept getInstance() {
		ConceptImpl conceptImpl = new ConceptImpl();
		conceptImpl.addLabelByType(LabelType.PREFERRED, new LabelImpl("UMLS 2010 AB", LanguageImpl.EN));
		return conceptImpl;
	}

}
