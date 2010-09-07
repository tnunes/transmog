package org.biosemantics.disambiguation.droid.umls.impl;


public interface IntermediateCache {
	
	boolean addToCache(CuiConceptId cuiConceptId);
	CuiConceptId getByCui(String cui);
	long getLabelNodeId(String sui);
	void addLabelNode(LabelNode labelNode);
	long getNotationNodeId(String sab, String code);
	void addNotationNode(NotationNode notationNode);

}
