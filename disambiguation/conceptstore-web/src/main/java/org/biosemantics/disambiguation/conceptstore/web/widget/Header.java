package org.biosemantics.disambiguation.conceptstore.web.widget;

import org.biosemantics.disambiguation.conceptstore.web.listener.ListenerControllerImpl;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class Header extends HorizontalLayout {

	private TextField txtSearch = new TextField();
	private Button go = new Button(WidgetConstants.BTN_GO);

	public Header() {
		this.setWidth("100%");
		Label expandingGap = new Label(
				WidgetConstants.APPLICATION_HEADER,
				Label.CONTENT_XHTML);
		expandingGap.setWidth("100%");
		this.addComponent(expandingGap);
		this.setExpandRatio(expandingGap, 1.0f);
		this.addComponent(txtSearch);
		go.addListener(ListenerControllerImpl.getInstance());
		this.addComponent(go);
		this.setMargin(true);
		this.setSpacing(true);
	}

	public Object getSearchText() {
		return txtSearch.getValue();
	}

}
