package org.biosemantics.disambiguation.datasource.umls;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

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
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.index.lucene.LuceneFulltextIndexBatchInserter;
import org.neo4j.index.lucene.LuceneIndexBatchInserter;
import org.neo4j.index.lucene.LuceneIndexBatchInserterImpl;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class UmlsRdbmsDatasourceImporter implements RdbmsDataSourceImporter {

	private static final int LOG_SIZE = 50000;

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
	// key label text value=node id
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

	private Map<String, Long> existingLabelsMap = new HashMap<String, Long>();
	private Map<NotationDetail, Long> existingNotationsMap = new HashMap<NotationDetail, Long>();

	@Required
	public void setDomainIterator(DomainIterator domainIterator) {
		this.domainIterator = domainIterator;
	}

	@Required
	public void setPredicateIterator(PredicateIterator predicateIterator) {
		this.predicateIterator = predicateIterator;
	}

	@Required
	public void setConceptSchemeIterator(ConceptSchemeIterator conceptSchemeIterator) {
		this.conceptSchemeIterator = conceptSchemeIterator;
	}

	@Required
	public void setConceptSchemeRelationshipIterator(ConceptSchemeRelationshipIterator conceptSchemeRelationshipIterator) {
		this.conceptSchemeRelationshipIterator = conceptSchemeRelationshipIterator;
	}

	@Required
	public void setConceptIterator(ConceptIterator conceptIterator) {
		this.conceptIterator = conceptIterator;
	}

	@Required
	public void setConceptFactualRelationshipIterator(
			ConceptFactualRelationshipIterator conceptFactualRelationshipIterator) {
		this.conceptFactualRelationshipIterator = conceptFactualRelationshipIterator;
	}

	@Required
	public void setConceptCooccuranceRelationshipIterator(
			ConceptCooccuranceRelationshipIterator conceptCooccuranceRelationshipIterator) {
		this.conceptCooccuranceRelationshipIterator = conceptCooccuranceRelationshipIterator;
	}

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
			// populate domain map: should have just one notation
			for (NotationDetail notationDetail : conceptDetail.getNotations()) {
				domainMap.put(notationDetail.getCode(), domainNodeId);
			}
		}
		logger.debug(domainMap.toString());
		// get the predicates
		while (predicateIterator.hasNext()) {
			ConceptDetail conceptDetail = predicateIterator.next();
			long predicateNodeId = addConceptDetail(conceptDetail, DefaultRelationshipType.PREDICATES);
			// populate concept map
			for (NotationDetail notationDetail : conceptDetail.getNotations()) {
				predicateMap.put(notationDetail.getCode(), predicateNodeId);
			}
		}
		logger.debug(predicateMap.toString());
		// get the concept schemes
		while (conceptSchemeIterator.hasNext()) {
			ConceptDetail conceptDetail = conceptSchemeIterator.next();
			long conceptSchemeNodeId = addConceptDetail(conceptDetail, DefaultRelationshipType.CONCEPT_SCHEMES);
			for (LabelDetail labelDetail : conceptDetail.getLabels()) {
				conceptSchemeMap.put(labelDetail.getText(), conceptSchemeNodeId);
			}
		}
		// get relationships for concept schemes
		while (conceptSchemeRelationshipIterator.hasNext()) {
			// clear reused map
			properties.clear();
			RelationshipDetail relationshipDetail = conceptSchemeRelationshipIterator.next();
			Long sourceConceptNodeId = conceptSchemeMap.get(relationshipDetail.getSourceConcept());
			if (sourceConceptNodeId == null) {
				logger.debug("in conceptscheme relationships, cannot have a null source concept id for string {} ",
						relationshipDetail.getSourceConcept());
			}

			Long targetConceptNodeId = conceptSchemeMap.get(relationshipDetail.getTargetConcept());
			if (targetConceptNodeId == null) {
				logger.debug("in conceptscheme relationships, cannot have a null target concept id for string {}",
						relationshipDetail.getTargetConcept());
			}
			Long predicateConceptNodeId = predicateMap.get(relationshipDetail.getPredicateConcept());
			if (predicateConceptNodeId == null) {
				logger.debug("in conceptscheme relationships, predicate no found in map for key={}",
						relationshipDetail.getPredicateConcept());
			}
			/*
			 * check against following entries in table. e.g. Acquired
			 * Abnormality co-occurs_with Acquired Abnormality We cannot
			 * have the same source and target for a relationship in neo4j
			 */
			if (sourceConceptNodeId == targetConceptNodeId) {
				logger.debug("same source and target concept for conceptscheme sourceName={} targetName={}",
						new Object[] { relationshipDetail.getSourceConcept(), relationshipDetail.getTargetConcept() });
			} else {
				if (predicateConceptNodeId != null) {
					properties.put(RelationshipImpl.PREDICATE_CONCEPT_UUID_PROPERTY, predicateConceptNodeId);
				}
				properties.put(RelationshipImpl.RELATIONSHIP_CATEGORY_PROPERTY,
						RelationshipCategory.AUTHORITATIVE.name());
				properties.put(RelationshipImpl.SCORE_PROPERTY, Integer.MAX_VALUE);
				properties.put(RelationshipImpl.UUID_PROPERTY, uuidGeneratorService.generateRandomUuid());
				inserter.createRelationship(sourceConceptNodeId, targetConceptNodeId,
						getRelationshipType(relationshipDetail.getPredicateConcept()), properties);
			}
		}

		// clear and de-refernce concept scheme map: not needed anymore (MIGHT free memory);
		conceptSchemeMap.clear();
		conceptSchemeMap = null;
		int counter = 0;
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
			counter++;
			if (counter % LOG_SIZE == 0) {
				logger.info("concept record: {}", counter);
			}
		}
		counter = 0;
		while (conceptFactualRelationshipIterator.hasNext()) {
			// clear reused map
			properties.clear();
			RelationshipDetail relationshipDetail = conceptFactualRelationshipIterator.next();
			Long sourceConceptNodeId = conceptMap.get(relationshipDetail.getSourceConcept());
			if (sourceConceptNodeId == null) {
				logger.debug("in concept factual rlsp. cannot have a null source concept id for string {} ",
						relationshipDetail.getSourceConcept());
				// log and ignore record
				continue;
			}
			Long targetConceptNodeId = conceptMap.get(relationshipDetail.getTargetConcept());
			if (targetConceptNodeId == null) {
				logger.debug("in concept factual rlsp. cannot have a null target concept id for string {}",
						relationshipDetail.getTargetConcept());
				// log and ignore record
				continue;
			}
			Long predicateConceptNodeId = predicateMap.get(relationshipDetail.getPredicateConcept());
			// lots of entries in database have RELA set to "null"
			if (predicateConceptNodeId == null) {
				logger.debug("in concept factual relationships, predicate not found in map for key={}",
						relationshipDetail.getPredicateConcept());
			}
			/*
			 * check against following entries in table. e.g. Acquired
			 * Abnormality co-occurs_with Acquired Abnormality We cannot
			 * have the same source and target for a relationship in neo4j
			 */
			if (sourceConceptNodeId == targetConceptNodeId) {
				logger.debug("same source and target concept for concept factual rlsp. sourceName={} targetName={}",
						new Object[] { relationshipDetail.getSourceConcept(), relationshipDetail.getTargetConcept() });
			} else {
				if (predicateConceptNodeId != null) {
					properties.put(RelationshipImpl.PREDICATE_CONCEPT_UUID_PROPERTY, predicateConceptNodeId);
				}
				properties.put(RelationshipImpl.RELATIONSHIP_CATEGORY_PROPERTY,
						RelationshipCategory.AUTHORITATIVE.name());
				properties.put(RelationshipImpl.SCORE_PROPERTY, Integer.MAX_VALUE);
				properties.put(RelationshipImpl.UUID_PROPERTY, uuidGeneratorService.generateRandomUuid());
				inserter.createRelationship(sourceConceptNodeId, targetConceptNodeId,
						getConceptRelationshipType(relationshipDetail.getRelationhipType()), properties);
			}
			counter++;
			if (counter % LOG_SIZE == 0) {
				logger.info("factual_rlsp record: {}", counter);

			}
		}
		counter = 0;
		while (conceptCooccuranceRelationshipIterator.hasNext()) {
			// clear reused map
			properties.clear();
			RelationshipDetail relationshipDetail = conceptCooccuranceRelationshipIterator.next();
			Long sourceConceptNodeId = conceptMap.get(relationshipDetail.getSourceConcept());
			if (sourceConceptNodeId == null) {
				logger.debug("in concept cooccurance rlsp. cannot have a null source concept id for string {} ",
						relationshipDetail.getSourceConcept());
				// log and ignore record
				continue;
			}
			Long targetConceptNodeId = conceptMap.get(relationshipDetail.getTargetConcept());
			if (targetConceptNodeId == null) {
				logger.debug("in concept cooccurance rlsp. cannot have a null target concept id for string {}",
						relationshipDetail.getTargetConcept());
				// log and ignore record
				continue;

			}
			/*
			 * check against following entries in table. e.g. Acquired
			 * Abnormality co-occurs_with Acquired Abnormality We cannot
			 * have the same source and target for a relationship in neo4j
			 */
			if (sourceConceptNodeId == targetConceptNodeId) {
				logger.debug(
						"same source and target concept for concept cooccurance rlsp. sourceName={} targetName={}",
						new Object[] { relationshipDetail.getSourceConcept(), relationshipDetail.getTargetConcept() });
			} else {

				properties.put(RelationshipImpl.RELATIONSHIP_CATEGORY_PROPERTY,
						RelationshipCategory.AUTHORITATIVE.name());
				properties.put(RelationshipImpl.SCORE_PROPERTY, relationshipDetail.getStrength());
				properties.put(RelationshipImpl.UUID_PROPERTY, uuidGeneratorService.generateRandomUuid());
				inserter.createRelationship(sourceConceptNodeId, targetConceptNodeId,
						ConceptRelationshipTypeImpl.RELATED, properties);
			}
			counter++;
			if (counter % LOG_SIZE == 0) {
				logger.info("cooccurance record: {}", counter);
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
			// check if we have already created this node is so reuse the node
			final String key = labelDetail.getLanguage() + labelDetail.getText();
			Long existingLabelNode = existingLabelsMap.get(key);
			if (existingLabelNode == null) {
				// clear map
				// create label node if none found
				properties.clear();
				properties.put(LabelImpl.TEXT_PROPERTY, labelDetail.getText());
				properties.put(LabelImpl.LANGUAGE_PROPERTY, labelDetail.getLanguage().getLabel());
				long labelNodeId = inserter.createNode(properties);
				long parentNode = parentNodes.get(DefaultRelationshipType.LABELS);
				inserter.createRelationship(parentNode, labelNodeId, DefaultRelationshipType.LABEL, null);
				// index label
				indexService.index(labelNodeId, Index.LABEL_TXT_INDEX, labelDetail.getText());
				// add to full text
				fulltext.append(labelDetail.getText()).append(Index.FULL_TEXT_SEPARATOR);
				// set in map as well
				existingLabelsMap.put(key, labelNodeId);
				// set the label node
				existingLabelNode = labelNodeId;
			}
			// link label to concept
			// clear map
			properties.clear();
			properties.put(ConceptImpl.LABEL_TYPE_PROPERTY, labelDetail.getLabelType().name());
			inserter.createRelationship(conceptNodeId, existingLabelNode, DefaultRelationshipType.HAS_LABEL, properties);

		}

		for (NotationDetail notationDetail : conceptDetail.getNotations()) {
			// check if we have already created this node is so reuse the node
			Long existingNotationNode = existingNotationsMap.get(notationDetail);
			if (existingNotationNode == null) {
				// create new notation here
				// clear map
				properties.clear();
				properties.put(NotationImpl.CODE_PROPERTY, notationDetail.getCode());
				long notationNodeId = inserter.createNode(properties);
				long parentNode = parentNodes.get(DefaultRelationshipType.NOTATIONS);
				inserter.createRelationship(parentNode, notationNodeId, DefaultRelationshipType.NOTATION, null);

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
						inserter.createRelationship(notationNodeId, domainNodeId, DefaultRelationshipType.HAS_DOMAIN,
								null);
					}
				}
				// add to map
				existingNotationsMap.put(notationDetail, notationNodeId);
				// set the notation node
				existingNotationNode = notationNodeId;
			}
			inserter.createRelationship(conceptNodeId, existingNotationNode, DefaultRelationshipType.HAS_NOTATION, null);
		}
		indexService.index(conceptNodeId, Index.CONCEPT_ID_INDEX, uuid);
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
