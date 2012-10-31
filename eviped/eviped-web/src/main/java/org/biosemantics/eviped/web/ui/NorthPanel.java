package org.biosemantics.eviped.web.ui;

import org.biosemantics.eviped.MyVaadinApplication;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class NorthPanel extends HorizontalLayout implements Button.ClickListener {
	private TextField searchTxt = new TextField();
	private Button searchBtn = new Button("Go");

	private NorthPanel() {
		this.addComponent(searchTxt);
		this.addComponent(searchBtn);
		searchBtn.addListener(this);
	}

	public static final NorthPanel getInstance() {
		return new NorthPanel();
	}

	public void buttonClick(ClickEvent event) {
		((MyVaadinApplication) this.getApplication()).getSearchController().search();
	}

	public String getSearchText() {
		return (String) searchTxt.getValue();
	}

}
