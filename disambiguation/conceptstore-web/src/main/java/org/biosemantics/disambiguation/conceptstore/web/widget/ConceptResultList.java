package org.biosemantics.disambiguation.conceptstore.web.widget;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.disambiguation.conceptstore.web.common.StorageUtility;
import org.biosemantics.disambiguation.conceptstore.web.listener.ListenerControllerImpl;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ConceptResultList extends VerticalLayout {

	private static final Language DEFAULT = Language.EN;

	public ConceptResultList(Collection<Concept> concepts) {
		this.addComponent(new Label(WidgetConstants.LBL_SEARCH_RESULTS_HEADER, Label.CONTENT_XHTML));
		for (Concept concept : concepts) {
			ConceptLabel label = StorageUtility.getPreferredLabel(concept, DEFAULT);
			addComponent(new ConceptHtmlLabel(concept.getUuid(), label));
			addListener(ListenerControllerImpl.getInstance());
		}
	}
}
