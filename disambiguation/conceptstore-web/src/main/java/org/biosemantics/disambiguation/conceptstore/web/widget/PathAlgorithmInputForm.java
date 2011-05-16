package org.biosemantics.disambiguation.conceptstore.web.widget;

import java.util.Collection;

import org.biosemantics.disambiguation.conceptstore.web.listener.ListenerControllerImpl;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class PathAlgorithmInputForm extends Form {

	private Button submit = new Button(WidgetConstants.BTN_SUBMIT);
	private Button reset = new Button(WidgetConstants.BTN_RESET);
	private ComboBox fromConcept = new ComboBox("Concept 1");
	private ComboBox toConcept = new ComboBox("Concept 2");
	private ComboBox comboPathAlgorithms = new ComboBox("Algorithm");
	// private static final String[] pathAlgorithms = new String[] { "Shortest Path", "All Paths" };
	private static final String[] pathAlgorithms = new String[] { "Shortest Path" };

	public Button getSubmit() {
		return submit;
	}

	public Button getReset() {
		return reset;
	}

	public ComboBox getFromConcept() {
		return fromConcept;
	}

	public ComboBox getToConcept() {
		return toConcept;
	}

	public ComboBox getComboPathAlgorithms() {
		return comboPathAlgorithms;
	}

	public PathAlgorithmInputForm() {
		getLayout().setMargin(true);
		fromConcept.setInputPrompt("Select a concept");
		fromConcept.setImmediate(true);
		this.addField("fromConcept", fromConcept);
		toConcept.setInputPrompt("select a concept");
		toConcept.setImmediate(true);
		this.addField("toConcept", toConcept);
		comboPathAlgorithms.setInputPrompt("select a path algorithm");
		comboPathAlgorithms.setImmediate(true);
		for (int i = 0; i < pathAlgorithms.length; i++) {
			comboPathAlgorithms.addItem(pathAlgorithms[i]);
		}
		this.addField("algorithm", comboPathAlgorithms);
		HorizontalLayout footer = new HorizontalLayout();
		// spacer
		footer.setWidth("95%");
		Label expandingGap = new Label();
		expandingGap.setWidth("95%");
		footer.addComponent(expandingGap);
		footer.setExpandRatio(expandingGap, 1.0f);
		// spacer ends
		footer.setSpacing(true);
		submit.addListener(ListenerControllerImpl.getInstance());
		footer.addComponent(submit);
		footer.addComponent(reset);
		reset.addListener(ListenerControllerImpl.getInstance());
		setFooter(footer);
	}

	public void setAvailableConcepts(Collection<String> strings) {
		for (String string : strings) {
			fromConcept.addItem(string);
			toConcept.addItem(string);
		}
	}

}
