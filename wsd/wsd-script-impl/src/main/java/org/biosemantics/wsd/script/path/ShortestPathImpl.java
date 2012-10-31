package org.biosemantics.wsd.script.path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.repository.ConceptRepository;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.Traversal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;

public class ShortestPathImpl {

	public void setMaxBreadth(int maxBreadth) {
		this.maxBreadth = maxBreadth;
	}

	public Path findShortestHierarchicalPath(String cui, String otherCui) {
		Node from = getNode(cui);
		Node to = getNode(otherCui);
		if (from == null || to == null) {
			logger.error("from:{} to:{}", new Object[] { from, to });
			throw new IllegalStateException("node not found");
		} else {
			return getHierarchicalPath(from, to);
		}
	}

	public Path findShortestRelatedPath(String cui, String otherCui) {
		Node from = getNode(cui);
		Node to = getNode(otherCui);
		if (from == null || to == null) {
			logger.error("from:{} to:{}", new Object[] { from, to });
			throw new IllegalStateException("node not found");
		} else {
			return getRelatedPath(from, to);
		}
	}

	private Node getNode(String id) {
		Concept concept = conceptRepository.getConceptById(id);
		return neo4jTemplate.getNode(concept.getNodeId());
	}

	private Path getHierarchicalPath(Node from, Node to) {
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(new RelationshipType() {
			public String name() {
				return "CHILD";
			}
		}, Direction.BOTH), maxBreadth);
		Path path = finder.findSinglePath(from, to);
		return path;
	}

	private Path getRelatedPath(Node from, Node to) {
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(new RelationshipType() {
			public String name() {
				return "RELATED";
			}
		}, Direction.BOTH), maxBreadth);
		Path path = finder.findSinglePath(from, to);
		return path;
	}

	private List<String> findCommonCuis(String cui, String[] cuis) {
		List<String> myCuis = new ArrayList<String>();
		Collections.addAll(myCuis, cuis);
		Concept concept = conceptRepository.getConceptById(cui);
		Iterable<Concept> relatedConcepts = conceptRepository.getRelatedConcepts(concept);
		List<String> relatedCuis = new ArrayList<String>();
		for (Concept relatedConcept : relatedConcepts) {
			relatedCuis.add(relatedConcept.getId());
		}
		logger.info("{} related cuis found", relatedCuis.size());
		myCuis.retainAll(relatedCuis);
		return myCuis;
	}
	
	
	private void getAllRelatedConcepts(String id) throws IOException {
		Concept concept = conceptRepository.getConceptById(id);
		Set<String> cuis = new HashSet<String>();
		Iterable<Concept> rldConcepts = conceptRepository.getRelatedConcepts(concept);
		
		for (Concept rldConcept : rldConcepts) {
			cuis.add(rldConcept.getId());
		}
		Iterable<Concept> hierConcepts = conceptRepository.getHierarchicalConcepts(concept);
		for (Concept hierConcept : hierConcepts) {
			cuis.add(hierConcept.getId());
		}
		logger.info("{}", cuis.size());

	}

	public static void main(String[] args) throws IOException {
		// scoreFunction();
		//overlap();
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("script-run-context.xml");
		applicationContext.registerShutdownHook();
		ShortestPathImpl shortestPathImpl = applicationContext.getBean(ShortestPathImpl.class);
		shortestPathImpl.getAllRelatedConcepts("C0005910");
	}

//	private static void overlap() {
//		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("script-run-context.xml");
//		applicationContext.registerShutdownHook();
//		ShortestPathImpl shortestPathImpl = applicationContext.getBean(ShortestPathImpl.class);
//		String[] contextCuis = new String[] { "C0079189", "C0127400", "C1280500", "C0918027", "C0005695", "C0007634",
//				"C1533691", "C1302234", "C0009566", "C0021917", "C0004886", "C0039798", "C0205124", "C0005695",
//				"C0700287", "C0918027", "C0005525", "C0004886", "C1280519", "C1254351", "C0039798", "C0005695",
//				"C1292732", "C0079189", "C0127400", "C1280500", "C0918027", "C0598089", "C0086418", "C0005684",
//				"C0443211", "C0682523", "C0005684", "C1533691", "C1321301", "C1708335", "C0220814", "C0918027",
//				"C0440102", "C0439531", "C0220814", "C1550101", "C1273517", "C0162518", "C0079189", "C0220814",
//				"C1550101", "C1280500", "C0918027", "C0220825", "C1947989", "C0205431", "C0005507", "C1947989",
//				"C0205431", "C0243073", "C1524063", "C0162518", "C0010454", "C1947989", "C0220921", "C0007634",
//				"C0311403", "C0162518", "C0369286", "C0010453", "C1321301", "C1439852", "C0918027", "C0086045",
//				"C0439526", "C0162518", "C1321301", "C0918027", "C1547085", "C0439526", "C0311403", "C1947989",
//				"C0220921", "C0007600", "C0205250", "C0086045", "C0021753", "C0041368", "C0442726", "C0162518",
//				"C0918027", "C0369286", "C0009458", "C1321301", "C0439044", "C0205250", "C0086045", "C0021740",
//				"C0442726", "C0162518", "C0918027", "C1547085", "C0332232", "C0439083", "C1628982", "C0007634",
//				"C0369286", "C0162518", "C0003250", "C1628982", "C0007634", "C0332232", "C0445247", "C0003241",
//				"C1628982", "C0007634", "C1547085", "C0162518", "C0003242", "C0021740", "C0746619", "C1321301",
//				"C0918027", "C0311403", "C0033268", "C0079459", "C1321301", "C1628982", "C0600138", "C1705810",
//				"C0441712", "C1280500", "C0918027", "C0005695", "C0007600", "C0005682", "C0085983", "C1705242",
//				"C0427965", "C0079189", "C0079189", "C0205263", "C0918027", "C1548673", "C1446561", "C0918027",
//				"C0220814", "C0439531", "C1705535", "C0021917", "C0918027", "C0039798", "C0005695", "C2698651",
//				"C0178602", "C1272706", "C0184959", "C0750591" };
//		String wordsense = "C0021467";
//		List<String> overlaps = shortestPathImpl.findCommonCuis(wordsense, contextCuis);
//		logger.info("{}", overlaps.size());
//	}
//
//	private static void scoreFunction() {
//		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("script-run-context.xml");
//		applicationContext.registerShutdownHook();
//		ShortestPathImpl shortestPathImpl = applicationContext.getBean(ShortestPathImpl.class);
//		String[] otherCuis = new String[] { "C0010405", "C0205263", "C0014257", "C1299583", "C0035028", "C1963140",
//				"C0039005", "C0205042", "C0040300", "C0332183", "C0009443", "C1698986", "C0205463", "C0947630",
//				"C0205210", "C0040733", "C0700287", "C0010405", "C0205263", "C0035028", "C1963140", "C0369286",
//				"C0684010", "C0011198", "C0039005", "C0205042", "C2827486", "C0226004", "C1948023", "C0917834",
//				"C0369286", "C2603358", "C1518422", "C0449265", "C0035028", "C0439583", "C0010405", "C1318216",
//				"C0439237", "C0369286", "C2603358", "C0449265", "C0040704", "C0035028", "C0444505", "C0035028",
//				"C0369718", "C1514863", "C1442455", "C0010405", "C0369286", "C2603358", "C0035028", "C0311403",
//				"C0025746", "C0284825", "C0030054", "C0019046", "C0470206", "C0369718", "C0439267", "C0003765",
//				"C0369718", "C0439267", "C0003765", "C0033551", "C0021467", "C1280519", "C0369286", "C2603358",
//				"C0035028", "C0599946", "C0439509", "C0145299", "C0439084", "C1518422", "C1999216", "C0597277",
//				"C1706095", "C0028128", "C0205263", "C0035028", "C2349975", "C0010405", "C0917834", "C0009917",
//				"C0040300", "C1140999", "C0439509", "C2349975", "C0028128", "C0205263", "C0035028", "C0010405",
//				"C0522501", "C0547044", "C0917834", "C0007367", "C0470206", "C1553035", "C0439526", "C0439086",
//				"C0040880", "C1999216", "C0007367", "C0392760", "C0369286", "C2603358", "C0035028", "C2926735",
//				"C0369286", "C2603358", "C0035028", "C0442805", "C0439531", "C1980044", "C0439237", "C0178784",
//				"C0150141", "C0332206", "C0021467", "C1157570", "C0018358", "C0021467", "C0597295", "C0010934",
//				"C0010572", "C0011777", "C0470206", "C1999216", "C0132555", "C0205263", "C0332206", "C0442805",
//				"C2926735", "C0369286", "C2603358", "C0035028", "C1274040", "C1705535", "C1267092", "C0205263",
//				"C1518332", "C0035028", "C0449719", "C0127400", "C0597277", "C1706095", "C0669372", "C0162340",
//				"C0022116", "C0684253", "C0871261", "C0009443", "C1698986", "C0226004" };
//		String coldCui = "C0009264";
//		double availableConnections = 0;
//		double totalConnections = 0;
//		double totalConnectionLength = 0;
//		for (String otherCui : otherCuis) {
//			if (!otherCui.equals(coldCui)) {
//				totalConnections++;
//				Path hierPath = shortestPathImpl.findShortestHierarchicalPath(coldCui, otherCui);
//				Path relPath = shortestPathImpl.findShortestRelatedPath(coldCui, otherCui);
//
//				if (hierPath == null && relPath == null) {
//					// no connection
//				} else if (hierPath == null) {
//					availableConnections++;
//					totalConnectionLength += relPath.length();
//				} else if (relPath == null) {
//					availableConnections++;
//					totalConnectionLength += hierPath.length();
//				} else {
//					availableConnections++;
//					int minPath = hierPath.length() <= relPath.length() ? hierPath.length() : relPath.length();
//					totalConnectionLength += minPath;
//				}
//				logger.info("{} {} {}", new Object[] { otherCui, totalConnectionLength, availableConnections });
//			}
//		}
//		logger.info("totalConnectionLength {} availableConnections {} totalconnections {}", new Object[] {
//				totalConnectionLength, availableConnections, totalConnections });
//		logger.info(
//				"score {}",
//				(Math.pow(0.9, (totalConnectionLength / availableConnections)) * (availableConnections / totalConnections)));
//	}

	@Autowired
	private Neo4jTemplate neo4jTemplate;
	@Autowired
	private ConceptRepository conceptRepository;
	private static int maxBreadth = 6;
	private static final Logger logger = LoggerFactory.getLogger(ShortestPathImpl.class);

}
