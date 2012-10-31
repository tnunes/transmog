package org.biosemantics.disambiguation.conceptstore.web.listener;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.disambiguation.conceptstore.web.ConceptStoreApplication;
import org.biosemantics.disambiguation.conceptstore.web.common.StorageUtility;
import org.biosemantics.disambiguation.conceptstore.web.widget.AlgorithmOutput;
import org.biosemantics.disambiguation.conceptstore.web.widget.ConceptDetail;
import org.biosemantics.disambiguation.conceptstore.web.widget.ConceptHtmlLabel;
import org.biosemantics.disambiguation.conceptstore.web.widget.ConceptRelation;
import org.biosemantics.disambiguation.conceptstore.web.widget.ConceptResultList;
import org.biosemantics.disambiguation.conceptstore.web.widget.WidgetConstants;
import org.neo4j.graphdb.Path;
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
	private Concept selectedConcept;
	private Concept existingRelationsConcept;
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
			long start = System.currentTimeMillis();
			search(event);
			logger.debug("search: {}(ms)", System.currentTimeMillis() - start);
		} else if (btnCaption.equals(WidgetConstants.BTN_SUBMIT)) {
			long start = System.currentTimeMillis();
			executeAlgorithm();
			logger.debug("path algorithm: {}(ms)", System.currentTimeMillis() - start);
		} else {
			application.getWindow().showNotification(btnCaption);
		}

	}

	private void executeAlgorithm() {
		Object fromConcept = application.getTabbedView().getPathAlgorithmInputForm().getFromConcept().getValue()
				.toString();
		Object toConcept = application.getTabbedView().getPathAlgorithmInputForm().getToConcept().getValue();
		Object algorithm = application.getTabbedView().getPathAlgorithmInputForm().getComboPathAlgorithms().getValue();
		if (fromConcept != null && toConcept != null && algorithm != null) {
			String fromConceptUuid = application.getStorageCache().getUuid(fromConcept.toString());
			String toConceptUuid = application.getStorageCache().getUuid(toConcept.toString());
			Iterable<Path> paths = application.getSpringServiceLocator().getAlgorithmServiceLocal()
					.shortestPath(fromConceptUuid, toConceptUuid, 5);
			AlgorithmOutput algorithmOutput = new AlgorithmOutput(paths);
			application.getTabbedView().getPathAlgorithmTab().setSecondComponent(algorithmOutput);
		} else {
			application.getWindow().showNotification("Invalid input parameter for algorithm execution");
		}
	}

	private void search(ClickEvent event) {
		Object value = application.getHeader().getSearchText();
		if (value == null || StringUtils.isBlank(value.toString())) {
			application.getWindow().showNotification("Invalid search text", Notification.TYPE_WARNING_MESSAGE);
		} else {
			// application.getWindow().showNotification("fetching results for search text '" + value + "'");
			String strValue = value.toString();
			Collection<Concept> concepts = null;
			if (strValue.startsWith("lbl:")) {
				String searchString = strValue.substring(4, strValue.length());
				logger.debug("looking for labels with string: {}", searchString);
				concepts = application.getSpringServiceLocator().getLabelStorageService()
						.getAllRelatedConceptsForLabelText(searchString);
			}else if (strValue.startsWith("not:")) {
				String searchString = strValue.substring(4, strValue.length());
				logger.debug("looking for notations with code: {}", searchString);
				concepts = application.getSpringServiceLocator().getNotationStorageService()
						.getAllRelatedConcepts(searchString);
			} else {
				concepts = application.getSpringServiceLocator().getConceptStorageService()
						.getConceptsByFullTextQuery(value.toString(), 30);
			}
			if (CollectionUtils.isEmpty(concepts)) {
				application.getWindow().showNotification("No results found for search text '" + value.toString() + "'");
			} else {
				ConceptResultList conceptResultList = new ConceptResultList(concepts);
				application.getTabbedView().setTabComponent(1, conceptResultList);
				application.getTabbedView().setSelectedTab(1);
				application.getNavigationTree().addToSearchHistory(value.toString());
			}
		}
	}

	public void selectedTabChange(SelectedTabChangeEvent event) {
		TabSheet tabsheet = event.getTabSheet();
		Tab tab = tabsheet.getTab(tabsheet.getSelectedTab());
		if (tab != null) {
			if (tab.getCaption().equals(WidgetConstants.TAB_RELATION)) {
				long start = System.currentTimeMillis();
				// check if we already have the relations for the currently selected concept
				if (existingRelationsConcept == null || !existingRelationsConcept.equals(selectedConcept)) {
					getRelations(selectedConcept);
				}
				logger.debug("relations: {}(ms)", System.currentTimeMillis() - start);
			} else if (tab.getCaption().equals(WidgetConstants.TAB_PATH_ALGORITHM)) {
				application.getTabbedView().getPathAlgorithmInputForm()
						.setAvailableConcepts(application.getStorageCache().getAllConceptLabels());
			}

		}
	}

	private void getRelations(Concept concept) {
		ConceptRelation conceptRelation = new ConceptRelation();
		// HACK this component needs to be added first as otherwise the getApplication() call in the component will give
		// a null and we need it to get a reference to our spring service
		application.getTabbedView().setTabComponent(3, conceptRelation);
		conceptRelation.build(concept);
		existingRelationsConcept = concept;
	}

	public void layoutClick(LayoutClickEvent event) {
		if (event.getClickedComponent() instanceof ConceptHtmlLabel) {
			long start = System.currentTimeMillis();
			getConceptDetails(event);
			logger.debug("details: {}(ms)", System.currentTimeMillis() - start);
		}

	}

	private void getConceptDetails(LayoutClickEvent event) {
		ConceptHtmlLabel conceptHtmlLabel = (ConceptHtmlLabel) event.getClickedComponent();
		String uuid = conceptHtmlLabel.getUuid();
		application.getWindow().showNotification("fetching details for concept");
		selectedConcept = application.getSpringServiceLocator().getConceptStorageService().getConcept(uuid);
		ConceptDetail conceptDetail = new ConceptDetail(selectedConcept);
		application.getTabbedView().setTabComponent(2, conceptDetail);
		application.getTabbedView().setSelectedTab(2);
		String preferredLabel = StorageUtility.getPreferredLabel(selectedConcept, null).getText();
		application.getNavigationTree().addToConceptHistory(selectedConcept.getUuid(), preferredLabel);
		application.getStorageCache().putConcept(preferredLabel, uuid);
	}

	public void itemClick(ItemClickEvent event) {
		// TODO Auto-generated method stub

	}

}
