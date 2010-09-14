package org.biosemantics.disambiguation.droid.umls.impl;


public interface IntermediateCache {
	
	//boolean addToCache(CuiConceptId cuiConceptId);
	//CuiConceptId getByCui(String cui);
	long getLabelNodeId(String sui);
	void addLabelNode(String sui, Long labelNodeId);
	long getNotationNodeId(final String domain, final String code);
	void addNotationNode(String domain, String code, long notationNodeId);
	void addConceptNode(String cui, long conceptNode);
	long getConceptNodeByCui(String cui1);

}
