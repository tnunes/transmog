package org.biosemantics.disambiguation.conceptstore.web.widget;

import org.biosemantics.disambiguation.conceptstore.web.listener.ListenerControllerImpl;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.Runo;

public class TabbedView extends TabSheet {

	private static final ThemeResource icon1 = new ThemeResource("../transmog-web/icons/action_save.gif");
	private static final ThemeResource icon2 = new ThemeResource("../transmog-web/icons/comment_yellow.gif");
	private static final ThemeResource icon3 = new ThemeResource("../transmog-web/icons/icon_info.gif");
	private Panel conceptsTab = new Panel();
	private Panel relationsTab = new Panel();
	private Panel algorithmsTab = new Panel();

	public TabbedView() {
		this.setHeight("100%");

		conceptsTab.setStyleName(Runo.PANEL_LIGHT);
		conceptsTab.setHeight("100%");
		addTab(conceptsTab, WidgetConstants.TAB_CONCEPTS, icon1);

		relationsTab.setStyleName(Runo.PANEL_LIGHT);
		relationsTab.setHeight("100%");
		addTab(relationsTab, WidgetConstants.TAB_RELATIONS, icon2);

		algorithmsTab.setStyleName(Runo.PANEL_LIGHT);
		algorithmsTab.setHeight("100%");
		addTab(algorithmsTab, WidgetConstants.TAB_ALGORITHMS, icon3);
		this.addListener(ListenerControllerImpl.getInstance());

	}

	public void setTabPanel(int position, Component component) {
		Panel panel = ((Panel) this.getTab(position).getComponent());
		panel.removeAllComponents();
		panel.addComponent(component);
	}

	public void setSelectedTab(int position) {
		if (position == 0) {
			this.setSelectedTab(conceptsTab);
		} else if (position == 1) {
			this.setSelectedTab(relationsTab);
		} else if (position == 3) {
			this.setSelectedTab(algorithmsTab);
		}
	}
}
