package org.biosemantics.disambiguation.manager.dto;

import java.io.Serializable;

public class QueryResult implements Serializable {

	private static final long serialVersionUID = -7909445737824872671L;
	private String id;
	private String label;
	private String description;
	private String language;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
