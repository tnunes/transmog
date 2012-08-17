package org.biosemantics.eviped.web;

import java.util.Stack;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

public class UserSession extends WebSession {
	
	public UserSession(Request request) {
		super(request);
	}

	private Stack<String> previousSearches = new Stack<String>();

	public Stack<String> getPreviousSearches() {
		return previousSearches;
	}

}
