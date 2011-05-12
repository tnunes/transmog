package org.biosemantics.disambiguation.conceptstore.web.listener;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.disambiguation.conceptstore.web.ConceptStoreApplication;
import org.biosemantics.disambiguation.conceptstore.web.widget.ConceptDetail;
import org.biosemantics.disambiguation.conceptstore.web.widget.ConceptHtmlLabel;
import org.biosemantics.disambiguation.conceptstore.web.widget.ConceptRelations;
import org.biosemantics.disambiguation.conceptstore.web.widget.ConceptResultList;
import org.biosemantics.disambiguation.conceptstore.web.widget.WidgetConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Window.Notification;

public class ListenerControllerImpl implements ClickListener, SelectedTabChangeListener, LayoutClickListener,
		ItemClickListener {

	private static ListenerControllerImpl instance;
	private ConceptStoreApplication application;
	private ConceptDetail conceptDetail;
	private ConceptRelations conceptRelations;
	private static final Logger logger = LoggerFactory.getLogger(ListenerControllerImpl.class);

	public static ListenerControllerImpl getInstance() {
		if (instance == null) {
			instance = new ListenerControllerImpl();
		}
		return instance;
	}

	public void setApplication(ConceptStoreApplication application) {
		this.application = application;
	}

	public void buttonClick(ClickEvent event) {
		String btnCaption = event.getComponent().getCaption();

		if (btnCaption.equals(WidgetConstants.BTN_GO)) {
			search(event);
		} else {
			application.getWindow().showNotification(btnCaption);
		}
	}

	private void search(ClickEvent event) {

		Object value = application.getHeader().getSearchText();
		if (value == null || StringUtils.isBlank(value.toString())) {
			application.getWindow().showNotification("Invalid search text", Notification.TYPE_WARNING_MESSAGE);
		} else {
			// application.getWindow().showNotification("fetching results for search text '" + value + "'");
			Collection<Concept> concepts = application.getSpringServiceLocator().getConceptStorageService()
					.getConceptsByFullTextQuery(value.toString(), 50);
			if (CollectionUtils.isEmpty(concepts)) {
				application.getWindow().showNotification("No results found for search text '" + value.toString() + "'");
			} else {
				ConceptResultList conceptResultList = new ConceptResultList(concepts);
				application.getTabbedView().setTabPanel(0, conceptResultList);
				application.getTabbedView().setSelectedTab(0);
				application.getNavigationTree().addToSearchHistory(value.toString());
			}
		}
	}

	public void selectedTabChange(SelectedTabChangeEvent event) {
		TabSheet tabsheet = event.getTabSheet();
		Tab tab = tabsheet.getTab(tabsheet.getSelectedTab());
		if (tab != null) {
			if (tab.getCaption().equals(WidgetConstants.TAB_RELATIONS) && conceptDetail != null
					&& conceptDetail.getConcept() != null) {
				if (conceptRelations == null || ! conceptRelations.getConcept().equals(conceptDetail.getConcept())) {
					application.getWindow().showNotification("fetching relations for concept");
					getRelations(conceptDetail.getConcept());
				}
			}
		}
	}

	private void getRelations(Concept concept) {
		conceptRelations = new ConceptRelations();
		// HACk added first as otherwise we get the getApplication() as null in Conceptrelations
		application.getTabbedView().setTabPanel(1, conceptRelations);
		conceptRelations.build(concept);
	}

	public void layoutClick(LayoutClickEvent event) {
		if (event.getClickedComponent() instanceof ConceptHtmlLabel) {
			getConceptDetails(event);
		}
	}

	private void getConceptDetails(LayoutClickEvent event) {
		ConceptHtmlLabel conceptHtmlLabel = (ConceptHtmlLabel) event.getClickedComponent();
		String uuid = conceptHtmlLabel.getUuid();
		application.getWindow().showNotification("fetching details for concept");
		Concept concept = application.getSpringServiceLocator().getConceptStorageService().getConcept(uuid);
		conceptDetail = new ConceptDetail(concept);
		application.getTabbedView().setTabPanel(0, conceptDetail);

	}

	public void itemClick(ItemClickEvent event) {
		// TODO Auto-generated method stub

	}

}
