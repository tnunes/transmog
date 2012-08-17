package org.biosemantics.eviped.web.ui;

import com.vaadin.ui.VerticalLayout;

public class BodyPanel extends VerticalLayout {

	private BodyPanel() {

	}

	public static BodyPanel getInstance() {
		return new BodyPanel();
	}

}
