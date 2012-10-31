package org.biosemantics.disambiguation.conceptstore.web.widget;

import com.vaadin.ui.VerticalSplitPanel;

public class AlgorithmLayout extends VerticalSplitPanel {

	private PathAlgorithmInputForm algorithmInputForm;

	public AlgorithmLayout() {
		setSplitPosition(40);
		algorithmInputForm = new PathAlgorithmInputForm();
		setFirstComponent(algorithmInputForm);
	}

}
