package org.biosemantics.eviped.web;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.biosemantics.eviped.web.ui.HomePage;

public class BaseApplication extends WebApplication {

	@Override
	public Class<HomePage> getHomePage() {
		return HomePage.class; // return default page
	}

	@Override
	protected void init() {
		super.init();
		// initialize Spring
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));

	}

	@Override
	public Session newSession(Request request, Response response) {
		return new UserSession(request);
	}

}
