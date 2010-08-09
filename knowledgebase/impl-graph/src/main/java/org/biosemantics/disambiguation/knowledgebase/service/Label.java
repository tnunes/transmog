package org.biosemantics.disambiguation.knowledgebase.service;

public interface Label {
	
	public enum LabelType{
		PREFERRED, ALTERNATE, HIDDEN;
	}
	String getId();
	LabelType getLabelType();
	String getText();
	Language getLanguage();
}
