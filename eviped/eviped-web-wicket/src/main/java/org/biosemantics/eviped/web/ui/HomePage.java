package org.biosemantics.eviped.web.ui;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.biosemantics.eviped.web.service.HelloService;

public class HomePage extends WebPage {

	@SpringBean
	private HelloService helloService;

	public HomePage(final PageParameters parameters) {

		add(new Label("msg", helloService.sayHello()));
		add(new FeedbackPanel("feedback"));
		add(new SearchForm("searchPanel"));

	}

}
