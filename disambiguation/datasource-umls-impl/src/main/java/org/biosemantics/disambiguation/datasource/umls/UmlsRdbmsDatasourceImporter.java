package org.biosemantics.disambiguation.datasource.umls;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.RelationshipCategory;
import org.biosemantics.conceptstore.utils.domain.impl.ErrorMessage;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.disambiguation.datasource.RdbmsDataSourceImporter;
import org.biosemantics.disambiguation.datasource.umls.DomainIterator.UmlsDomain;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.domain.impl.NotationImpl;
import org.biosemantics.disambiguation.domain.impl.RelationshipImpl;
import org.biosemantics.disambiguation.service.Index;
import org.biosemantics.disambiguation.service.impl.ConceptRelationshipTypeImpl;
import org.biosemantics.disambiguation.service.impl.DefaultRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.index.lucene.LuceneFulltextIndexBatchInserter;
import org.neo4j.index.lucene.LuceneIndexBatchInserter;
import org.neo4j.index.lucene.LuceneIndexBatchInserterImpl;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;
import org.omg.CORBA.FREE_MEM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.mysql.jdbc.log.LogFactory;

public class UmlsRdbmsDatasourceImporter implements RdbmsDataSourceImporter {

	private static final Logger logger = LoggerFactory.getLogger(UmlsRdbmsDatasourceImporter.class);

	private DataSource dataSource;
	private File outputDir;
	private boolean cleanImport = true;

	// other data members
	private BatchInserter inserter;
	private LuceneIndexBatchInserter indexService;
	private LuceneFulltextIndexBatchInserter fulltextIndexService;

	// housekeeping subnodes
	private Map<DefaultRelationshipType, Long> parentNodes = new HashMap<DefaultRelationshipType, Long>();
	// cache domains
	private Map<String, Long> domainMap = new HashMap<String, Long>();
	// Key: predicate-notation value: predicateNodeId
	private Map<String, Long> predicateMap = new HashMap<String, Long>();

	private Map<String, Long> conceptSchemeMap = new HashMap<String, Long>();
	// KEY = cui value = cocneptNodeId
	private Map<String, Long> conceptMap = new HashMap<String, Long>();
	// resuable properties map
	Map<String, Object> properties = new HashMap<String, Object>();
	// services needed
	UuidGeneratorService uuidGeneratorService;

	// other classes
	private DomainIterator domainIterator;
	private PredicateIterator predicateIterator;
	private ConceptSchemeIterator conceptSchemeIterator;
	private ConceptSchemeRelationshipIterator conceptSchemeRelationshipIterator;
	private ConceptIterator conceptIterator;
	private ConceptFactualRelationshipIterator conceptFactualRelationshipIterator;
	private ConceptCooccuranceRelationshipIterator conceptCooccuranceRelationshipIterator;

	@Required
	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = uuidGeneratorService;
	}

	@Override
	public void setOutputDir(String dirPath) {
		checkNotNull(dirPath);
		checkArgument(!dirPath.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, "dirPath");
		outputDir = new File(dirPath);
		checkState(outputDir.exists());
		checkState(outputDir.isDirectory());
		checkState(outputDir.canWrite());
	}

	@Override
	public void setCleanImport(boolean cleanImport) {
		this.cleanImport = cleanImport;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = checkNotNull(dataSource);
	}

	@Override
	public void init() {
		inserter = new BatchInserterImpl(outputDir.getAbsolutePath());
		indexService = new LuceneIndexBatchInserterImpl(inserter);
		fulltextIndexService = new LuceneFulltextIndexBatchInserter(inserter);
		// subnodes
		populateSubNodes();
		// get the domain
		while (domainIterator.hasNext()) {
			ConceptDetail conceptDetail = domainIterator.next();
			long domainNodeId = addConceptDetail(conceptDetail, DefaultRelationshipType.DOMAINS);
			// populate domain map
			domainMap.put((String) conceptDetail.getNotations().get(0).getDomain(), domainNodeId);
		}
		// get the predicates
		while (predicateIterator.hasNext()) {
			ConceptDetail conceptDetail = predicateIterator.next();
			long predicateNodeId = addConceptDetail(conceptDetail, DefaultRelationshipType.PREDICATES);
			// populate concept map
			predicateMap.put((String) conceptDetail.getNotations().get(0).getCode(), predicateNodeId);
		}
		// get the concept schemes
		while (conceptSchemeIterator.hasNext()) {
			ConceptDetail conceptDetail = conceptSchemeIterator.next();
			long conceptSchemeNodeId = addConceptDetail(conceptDetail, DefaultRelationshipType.CONCEPT_SCHEMES);
			conceptSchemeMap.put((String) conceptDetail.getNotations().get(0).getCode(), conceptSchemeNodeId);
		}
		// get relationships for concept schemes
		while (conceptSchemeRelationshipIterator.hasNext()) {
			// clear reused map
			properties.clear();
			RelationshipDetail relationshipDetail = conceptSchemeRelationshipIterator.next();
			long sourceConceptNodeId = conceptSchemeMap.get(relationshipDetail.getSourceConcept());
			long targetConceptNodeId = conceptSchemeMap.get(relationshipDetail.getTargetConcept());
			Long predicateConceptNodeId = predicateMap.get(relationshipDetail.getPredicateConcept());
			if (predicateConceptNodeId == null) {
				logger.warn("in conceptscheme relationships, predicate no found in map for key={}",
						relationshipDetail.getPredicateConcept());
			}
			/*
			 * check against following entries in table. e.g. Acquired
			 * Abnormality co-occurs_with Acquired Abnormality We cannot
			 * have the same source and target for a relationship in neo4j
			 */
			if (sourceConceptNodeId == targetConceptNodeId) {
				logger.info("same source and target concept for conceptscheme sourceName={} targetName={}",
						new Object[] { relationshipDetail.getSourceConcept(), relationshipDetail.getTargetConcept() });
			} else {
				if (predicateConceptNodeId != null) {
					properties.put(RelationshipImpl.PREDICATE_CONCEPT_UUID_PROPERTY, predicateConceptNodeId);
				}
				properties.put(RelationshipImpl.RELATIONSHIP_CATEGORY_PROPERTY, RelationshipCategory.AUTHORITATIVE);
				properties.put(RelationshipImpl.SCORE_PROPERTY, Integer.MAX_VALUE);
				properties.put(RelationshipImpl.UUID_PROPERTY, uuidGeneratorService.generateRandomUuid());
				inserter.createRelationship(sourceConceptNodeId, targetConceptNodeId,
						getRelationshipType(relationshipDetail.getPredicateConcept()), properties);
			}
		}

		// clear and de-refernce concept scheme map: not needed anymore (MIGHT free memory);
		conceptSchemeMap.clear();
		conceptSchemeMap = null;

		while (conceptIterator.hasNext()) {
			ConceptDetail conceptDetail = conceptIterator.next();
			long conceptNodeId = addConceptDetail(conceptDetail, DefaultRelationshipType.CONCEPTS);
			// populate concept map
			String cui = null;
			for (NotationDetail notationDetail : conceptDetail.getNotations()) {
				if (notationDetail.getDomain().equals(UmlsDomain.getDefaultDomain().name())) {
					cui = notationDetail.getCode();
					break;
				}
			}
			if (cui == null) {
				throw new IllegalStateException("cui cannot be null for a concept extracted from UMLS ref: "
						+ conceptDetail.toString());
			}
			conceptMap.put(cui, conceptNodeId);
		}

		while (conceptFactualRelationshipIterator.hasNext()) {
			// clear reused map
			properties.clear();
			RelationshipDetail relationshipDetail = conceptFactualRelationshipIterator.next();
			long sourceConceptNodeId = conceptSchemeMap.get(relationshipDetail.getSourceConcept());
			long targetConceptNodeId = conceptSchemeMap.get(relationshipDetail.getTargetConcept());
			Long predicateConceptNodeId = predicateMap.get(relationshipDetail.getPredicateConcept());
			if (predicateConceptNodeId == null) {
				logger.warn("in conceptscheme relationships, predicate no found in map for key={}",
						relationshipDetail.getPredicateConcept());
			}
			/*
			 * check against following entries in table. e.g. Acquired
			 * Abnormality co-occurs_with Acquired Abnormality We cannot
			 * have the same source and target for a relationship in neo4j
			 */
			if (sourceConceptNodeId == targetConceptNodeId) {
				logger.info("same source and target concept for conceptscheme sourceName={} targetName={}",
						new Object[] { relationshipDetail.getSourceConcept(), relationshipDetail.getTargetConcept() });
			} else {
				if (predicateConceptNodeId != null) {
					properties.put(RelationshipImpl.PREDICATE_CONCEPT_UUID_PROPERTY, predicateConceptNodeId);
				}
				properties.put(RelationshipImpl.RELATIONSHIP_CATEGORY_PROPERTY, RelationshipCategory.AUTHORITATIVE);
				properties.put(RelationshipImpl.SCORE_PROPERTY, Integer.MAX_VALUE);
				properties.put(RelationshipImpl.UUID_PROPERTY, uuidGeneratorService.generateRandomUuid());
				inserter.createRelationship(sourceConceptNodeId, targetConceptNodeId,
						getConceptRelationshipType(relationshipDetail.getRelationhipType()), properties);
			}
		}

		while (conceptCooccuranceRelationshipIterator.hasNext()) {
			// clear reused map
			properties.clear();
			RelationshipDetail relationshipDetail = conceptFactualRelationshipIterator.next();
			long sourceConceptNodeId = conceptSchemeMap.get(relationshipDetail.getSourceConcept());
			long targetConceptNodeId = conceptSchemeMap.get(relationshipDetail.getTargetConcept());
			/*
			 * check against following entries in table. e.g. Acquired
			 * Abnormality co-occurs_with Acquired Abnormality We cannot
			 * have the same source and target for a relationship in neo4j
			 */
			if (sourceConceptNodeId == targetConceptNodeId) {
				logger.info("same source and target concept for conceptscheme sourceName={} targetName={}",
						new Object[] { relationshipDetail.getSourceConcept(), relationshipDetail.getTargetConcept() });
			} else {

				properties.put(RelationshipImpl.RELATIONSHIP_CATEGORY_PROPERTY, RelationshipCategory.AUTHORITATIVE);
				properties.put(RelationshipImpl.SCORE_PROPERTY, relationshipDetail.getStrength());
				properties.put(RelationshipImpl.UUID_PROPERTY, uuidGeneratorService.generateRandomUuid());
				inserter.createRelationship(sourceConceptNodeId, targetConceptNodeId,
						ConceptRelationshipTypeImpl.RELATED, properties);
			}
		}

	}

	private void populateSubNodes() {
		// create sub nodes only if its a clean import
		if (cleanImport) {
			long referenceNode = inserter.getReferenceNode();

			DefaultRelationshipType[] defaultRelationshipTypes = new DefaultRelationshipType[] {
					DefaultRelationshipType.LABELS, DefaultRelationshipType.NOTATIONS,
					DefaultRelationshipType.CONCEPTS, DefaultRelationshipType.PREDICATES,
					DefaultRelationshipType.CONCEPT_SCHEMES, DefaultRelationshipType.DOMAINS };

			for (DefaultRelationshipType defaultRelationshipType : defaultRelationshipTypes) {
				long parentNode = inserter.createNode(null);
				inserter.createRelationship(referenceNode, parentNode, defaultRelationshipType, null);
				parentNodes.put(defaultRelationshipType, parentNode);
			}

		}
		// else get existing subnodes
		else {
			throw new UnsupportedOperationException("cleanImport = false not supported yet!");
		}

	}

	@Override
	public void destroy() {
		indexService.optimize();
		fulltextIndexService.optimize();
		indexService.shutdown();
		fulltextIndexService.shutdown();
		inserter.shutdown();
	}

	private long addConceptDetail(ConceptDetail conceptDetail, DefaultRelationshipType defaultRelationshipType) {
		StringBuilder fulltext = new StringBuilder();
		String uuid = uuidGeneratorService.generateRandomUuid();
		// create concept node
		// clear map
		properties.clear();
		properties.put(ConceptImpl.UUID_PROPERTY, uuid);
		long conceptNodeId = inserter.createNode(properties);
		fulltext.append(uuid).append(Index.FULL_TEXT_SEPARATOR);
		long conceptParentNode = parentNodes.get(defaultRelationshipType);
		inserter.createRelationship(conceptParentNode, conceptNodeId, DefaultRelationshipType.CONCEPT, null);

		for (LabelDetail labelDetail : conceptDetail.getLabels()) {
			// clear map
			// create label node
			properties.clear();
			properties.put(LabelImpl.TEXT_PROPERTY, labelDetail.getText());
			properties.put(LabelImpl.LANGUAGE_PROPERTY, labelDetail.getLanguage().name());
			long labelNodeId = inserter.createNode(properties);
			long parentNode = parentNodes.get(DefaultRelationshipType.LABELS);
			inserter.createRelationship(parentNode, labelNodeId, DefaultRelationshipType.LABEL, null);
			// link label to concept
			// clear map
			properties.clear();
			properties.put(ConceptImpl.LABEL_TYPE_PROPERTY, labelDetail.getLabelType().name());
			inserter.createRelationship(conceptNodeId, labelNodeId, DefaultRelationshipType.HAS_LABEL, properties);
			// index label
			indexService.index(labelNodeId, Index.LABEL_TXT_INDEX, labelDetail.getText());
			fulltext.append(labelDetail.getText()).append(Index.FULL_TEXT_SEPARATOR);
		}

		for (NotationDetail notationDetail : conceptDetail.getNotations()) {
			// clear map
			properties.clear();
			properties.put(NotationImpl.CODE_PROPERTY, notationDetail.getCode());
			long notationNodeId = inserter.createNode(properties);
			long parentNode = parentNodes.get(DefaultRelationshipType.NOTATIONS);
			inserter.createRelationship(parentNode, notationNodeId, DefaultRelationshipType.NOTATION, null);
			inserter.createRelationship(conceptNodeId, notationNodeId, DefaultRelationshipType.HAS_NOTATION, null);
			// index notation
			indexService.index(notationNodeId, Index.NOTATION_CODE_INDEX, notationDetail.getCode());
			fulltext.append(notationDetail.getCode()).append(Index.FULL_TEXT_SEPARATOR);
			// link to domain
			Long domainNodeId = domainMap.get(notationDetail.getDomain());
			if (domainNodeId == null) {
				logger.warn("no domain node id found for text {}", notationDetail.getDomain());
			} else {
				if (notationNodeId == domainNodeId) {
					logger.warn("notationNodeId and DomainNodeId are {} for notation code {}", new Object[] {
							notationNodeId, notationDetail.getCode() });
				} else {
					inserter.createRelationship(notationNodeId, domainNodeId, DefaultRelationshipType.HAS_DOMAIN, null);
				}
			}
		}
		fulltextIndexService.index(conceptNodeId, Index.CONCEPT_FULL_TXT_INDEX, fulltext.toString());
		return conceptNodeId;
	}

	private RelationshipType getRelationshipType(String rl) {
		ConceptRelationshipTypeImpl conceptRelationshipType = ConceptRelationshipTypeImpl.RELATED;
		if (rl.equalsIgnoreCase("isa")) {
			conceptRelationshipType = ConceptRelationshipTypeImpl.HAS_BROADER_CONCEPT;
		}
		return conceptRelationshipType;
	}

	private RelationshipType getConceptRelationshipType(String rel) {
		// available values for rel in UMLS:
		// http://www.nlm.nih.gov/research/umls/knowledge_sources/metathesaurus/release/abbreviations.html
		/*
		 * 
		 * AQ Allowed qualifier CHD has child relationship in a Metathesaurus
		 * source vocabulary DEL Deleted concept PAR has parent relationship in
		 * a Metathesaurus source vocabulary QB can be qualified by. RB has a
		 * broader relationship RL the relationship is similar or "alike". the
		 * two concepts are similar or "alike". In the current edition of the
		 * Metathesaurus, most relationships with this attribute are mappings
		 * provided by a source, named in SAB and SL; hence concepts linked by
		 * this relationship may be synonymous, i.e. self-referential: CUI1 =
		 * CUI2. In previous releases, some MeSH Supplementary Concept
		 * relationships were represented in this way. RN has a narrower
		 * relationship RO has relationship other than synonymous, narrower, or
		 * broader RQ related and possibly synonymous. RU Related, unspecified
		 * SIB has sibling relationship in a Metathesaurus source vocabulary. SY
		 * source asserted synonymy. XR Not related, no mapping Empty
		 * relationship
		 */
		ConceptRelationshipTypeImpl conceptRelationshipType = ConceptRelationshipTypeImpl.RELATED;
		String relUpper = rel.toUpperCase().trim();
		if (relUpper.equals("CHD") || relUpper.equals("RN")) {
			conceptRelationshipType = ConceptRelationshipTypeImpl.HAS_NARROWER_CONCEPT;
		} else if (relUpper.equals("PAR") || relUpper.equals("RB")) {
			conceptRelationshipType = ConceptRelationshipTypeImpl.HAS_BROADER_CONCEPT;
		} else if (relUpper.equals("RQ") || relUpper.equals("SY") || relUpper.equals("RL")) {
			conceptRelationshipType = ConceptRelationshipTypeImpl.CLOSE_MATCH;
		}
		return conceptRelationshipType;
	}

}
