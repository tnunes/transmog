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
package org.biosemantics.eviped;

import org.biosemantics.eviped.tools.service.QueryBuilder;
import org.biosemantics.eviped.web.service.SearchController;
import org.biosemantics.eviped.web.ui.BodyPanel;
import org.biosemantics.eviped.web.ui.NorthPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class MyVaadinApplication extends Application {

    private Window window;
    @Autowired
    private QueryBuilder queryBuilder;
    private SearchController searchController;
    
    @Override
    public void init() {
        window = new Window("Eviped Web Application");
        setMainWindow(window);
        NorthPanel northPanel = NorthPanel.getInstance();
        BodyPanel bodyPanel = BodyPanel.getInstance();
        // Button button = new Button("Click Me");
        // button.addListener(new Button.ClickListener() {
        // public void buttonClick(ClickEvent event) {
        // window.addComponent(new Label("Thank you for clicking"));
        // }
        // });
        searchController = new SearchController(northPanel, bodyPanel, queryBuilder);
        window.addComponent(northPanel);
        window.addComponent(bodyPanel);
    }
    
    public SearchController getSearchController() {
        return searchController;
    }
}
