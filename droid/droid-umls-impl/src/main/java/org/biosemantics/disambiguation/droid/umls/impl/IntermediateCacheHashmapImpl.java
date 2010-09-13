package org.biosemantics.disambiguation.droid.umls.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntermediateCacheHashmapImpl implements IntermediateCache {

	private Map<String, LabelNode> labelNodeMap;
	private Map<String, NotationNode> notationNodeMap;

	private static final Logger logger = LoggerFactory.getLogger(IntermediateCacheHashmapImpl.class);

	public void init() {
		logger.info("creating hashmaps for intermediate cache");
		labelNodeMap = new HashMap<String, LabelNode>();
		notationNodeMap = new HashMap<String, NotationNode>();
	}

	// @Override
	// public boolean addToCache(CuiConceptId cuiConceptId) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public CuiConceptId getByCui(String cui) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public long getLabelNodeId(String sui) {
		long id = 0;
		LabelNode labelNode = labelNodeMap.get(sui);
		if (labelNode != null) {
			id = labelNode.getNodeId();
		}
		return id;
	}

	@Override
	public void addLabelNode(LabelNode labelNode) {
		labelNodeMap.put(labelNode.getSui(), labelNode);

	}

	@Override
	public void addNotationNode(NotationNode notationNode) {
		notationNodeMap.put(notationNode.getDomain() + notationNode.getCode(), notationNode);

	}

	@Override
	public long getNotationNodeId(String domain, String code) {
		long id = 0;
		NotationNode notationNode = notationNodeMap.get(domain + code);
		if (notationNode != null) {
			id = notationNode.getNodeId();
		}
		return id;
	}

	public void destroy() {
		labelNodeMap.clear();
		notationNodeMap.clear();
		labelNodeMap = null;
		notationNodeMap = null;
	}

}
