package org.biosemantics.disambiguation.droid.umls.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntermediateCacheHashmapImpl implements IntermediateCache {

	private Map<String, Long> labelNodeMap;
	private Map<String, Long> notationNodeMap;
	private Map<String, Long> cuiConceptMap;

	private static final Logger logger = LoggerFactory.getLogger(IntermediateCacheHashmapImpl.class);

	public void init() {
		labelNodeMap = new HashMap<String, Long>();
		notationNodeMap = new HashMap<String, Long>();
		cuiConceptMap = new HashMap<String, Long>();
		logger.info("created hashmaps for intermediate cache");
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
		Long labelNodeId = labelNodeMap.get(sui);
		if (labelNodeId == null)
			return 0;
		else
			return labelNodeId;
	}

	@Override
	public void addLabelNode(String sui, Long labelNodeId) {
		labelNodeMap.put(sui, labelNodeId);

	}

	@Override
	public void addNotationNode(String domain, String code, long notationNodeId) {
		notationNodeMap.put(domain + code, notationNodeId);

	}

	@Override
	public long getNotationNodeId(String domain, String code) {
		Long id = notationNodeMap.get(domain + code);
		if (id == null)
			return 0;
		else
			return id;
	}

	public void destroy() {
		labelNodeMap.clear();
		notationNodeMap.clear();
		labelNodeMap = null;
		notationNodeMap = null;
	}

	@Override
	public void addConceptNode(String cui, long conceptNode) {
		cuiConceptMap.put(cui, conceptNode);

	}

	@Override
	public long getConceptNodeByCui(String cui1) {
		Long id = cuiConceptMap.get(cui1);
		if (id == null)
			return 0;
		else
			return id;
	}

}
