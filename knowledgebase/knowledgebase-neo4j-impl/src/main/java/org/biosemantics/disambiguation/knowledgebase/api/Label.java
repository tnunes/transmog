package org.biosemantics.disambiguation.knowledgebase.api;

public interface Label {
	
	public enum LabelType{
		PREFERRED, ALTERNATE, HIDDEN;
	}
	String getId();
	LabelType getLabelType();
	String getText();
	Language getLanguage();
}
