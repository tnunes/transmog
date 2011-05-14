package org.biosemantics.disambiguation.conceptstore.web.widget;

import org.biosemantics.disambiguation.conceptstore.web.listener.ListenerControllerImpl;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.Runo;

public class TabbedView extends TabSheet {

	private static final ThemeResource icon1 = new ThemeResource("../runo/icons/16/arrow-down.png");
	private Panel homeTab = new Panel();
	private Panel searchTab = new Panel();
	private Panel conceptTab = new Panel();
	private Panel relationTab = new Panel();
	private Panel algorithmTab = new Panel();

	public TabbedView() {
		this.setHeight("100%");

		homeTab.setStyleName(Runo.PANEL_LIGHT);
		homeTab.setHeight("100%");
		addTab(homeTab, WidgetConstants.TAB_HOME, icon1);
		homeTab.addComponent(new Label(WidgetConstants.HOME_PAGE_HTML_TEXT, Label.CONTENT_XHTML));

		searchTab.setStyleName(Runo.PANEL_LIGHT);
		searchTab.setHeight("100%");
		addTab(searchTab, WidgetConstants.TAB_SEARCH, icon1);

		conceptTab.setStyleName(Runo.PANEL_LIGHT);
		conceptTab.setHeight("100%");
		addTab(conceptTab, WidgetConstants.TAB_CONCEPT, icon1);

		relationTab.setStyleName(Runo.PANEL_LIGHT);
		relationTab.setHeight("100%");
		addTab(relationTab, WidgetConstants.TAB_RELATION, icon1);

		algorithmTab.setStyleName(Runo.PANEL_LIGHT);
		algorithmTab.setHeight("100%");
		addTab(algorithmTab, WidgetConstants.TAB_ALGORITHM, icon1);
		this.addListener(ListenerControllerImpl.getInstance());

	}

	public void setTabPanel(int position, Component component) {
		Panel panel = ((Panel) this.getTab(position).getComponent());
		panel.removeAllComponents();
		panel.addComponent(component);
	}

	public void setSelectedTab(int position) {
		switch (position) {
		case 0:
			this.setSelectedTab(homeTab);
			break;
		case 1:
			this.setSelectedTab(searchTab);
			break;
		case 2:
			this.setSelectedTab(conceptTab);
			break;
		case 3:
			this.setSelectedTab(relationTab);
			break;
		case 4:
			this.setSelectedTab(algorithmTab);
			break;
		default:
			break;
		}
	}
}
