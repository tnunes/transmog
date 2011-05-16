/*
 * Copyright 2009 IT Mill Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.biosemantics.disambiguation.conceptstore.web;

import org.biosemantics.disambiguation.conceptstore.web.common.SpringServiceLocator;
import org.biosemantics.disambiguation.conceptstore.web.common.StorageCache;
import org.biosemantics.disambiguation.conceptstore.web.listener.ListenerControllerImpl;
import org.biosemantics.disambiguation.conceptstore.web.widget.Header;
import org.biosemantics.disambiguation.conceptstore.web.widget.NavigationTree;
import org.biosemantics.disambiguation.conceptstore.web.widget.TabbedView;

import com.vaadin.Application;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class ConceptStoreApplication extends Application {
	private Window window;
	private Header header;
	private HorizontalSplitPanel horizontalSplit;
	private NavigationTree navigationTree;
	private TabbedView tabbedView;
	private SpringServiceLocator springServiceLocator;
	private StorageCache storageCache = new StorageCache();

	public Window getWindow() {
		return window;
	}

	public SpringServiceLocator getSpringServiceLocator() {
		return springServiceLocator;
	}

	public Header getHeader() {
		return header;
	}

	public TabbedView getTabbedView() {
		return tabbedView;
	}

	public NavigationTree getNavigationTree() {
		return navigationTree;
	}

	public StorageCache getStorageCache() {
		return storageCache;
	}

	@Override
	public void init() {
		window = new Window("Transmog Web");
		setMainWindow(window);
		setTheme("runo");

		// add spring helper
		springServiceLocator = new SpringServiceLocator(this);
		// add listener
		ListenerControllerImpl.getInstance().setApplication(this);
		buildLayout();
	}

	private void buildLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		header = new Header();
		layout.addComponent(header);
		horizontalSplit = new HorizontalSplitPanel();
		layout.addComponent(horizontalSplit);
		/* Allocate all available extra space to the horizontal split panel */
		layout.setExpandRatio(horizontalSplit, 1);
		/* Set the initial split position so we can have a 200 pixel menu to the left */
		horizontalSplit.setSplitPosition(200, HorizontalSplitPanel.UNITS_PIXELS);
		navigationTree = new NavigationTree();
		horizontalSplit.setFirstComponent(navigationTree);
		setMainComponent(getDefaultMainView());
		getMainWindow().setContent(layout);

	}

	private void setMainComponent(Component c) {
		horizontalSplit.setSecondComponent(c);
	}

	private Component getDefaultMainView() {
		if (tabbedView == null) {
			tabbedView = new TabbedView();
		}
		return tabbedView;
	}

}
