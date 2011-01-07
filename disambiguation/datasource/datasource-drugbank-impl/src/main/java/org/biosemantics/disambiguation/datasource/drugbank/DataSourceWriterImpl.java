package org.biosemantics.disambiguation.datasource.drugbank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.service.ConceptQueryService;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.disambiguation.datasource.drugbank.DomainIterator.DrugbankDomain;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.ontoware.rdf2go.model.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import ca.drugbank.generated.Drugs;

public class DataSourceWriterImpl implements DataSourceWriter {
	private boolean checkConceptExists = true;
	private ConceptQueryService conceptQueryService;
	private ConceptStorageService conceptStorageService;
	private Map<String, Concept> domainMap = new HashMap<String, Concept>();
	private static final Logger logger = LoggerFactory.getLogger(DataSourceWriterImpl.class);

	@Required
	public void setConceptQueryService(ConceptQueryService conceptQueryService) {
		this.conceptQueryService = conceptQueryService;
	}

	public void setCheckConceptExists(boolean checkConceptExists) {
		this.checkConceptExists = checkConceptExists;
	}

	@Required
	public void setConceptStorageService(ConceptStorageService conceptStorageService) {
		this.conceptStorageService = conceptStorageService;
	}

	public void init() {

	}

	public void writeDomain(DrugbankDomain drugbankDomain) {
		ConceptImpl conceptImpl = new ConceptImpl();
		conceptImpl.addLabelByType(LabelType.PREFERRED, new LabelImpl(drugbankDomain.getOfficialName(), LanguageImpl.EN));
		List<Notation> notations = new ArrayList<Notation>();
		Concept defaultDomain = domainMap.get(DrugbankDomain.getDefaultDomain().getUris()[0]);
		if (defaultDomain == null) {
			logger.warn("No domain found in map for uri, no notations will be added {}", DrugbankDomain
					.getDefaultDomain().getUris()[0]);
		} else {
			for (String uri : drugbankDomain.getUris()) {
				// no notations for concept
				notations.add(new NotationImpl(defaultDomain, drugbankDomain.name()));
				notations.add(new NotationImpl(defaultDomain, uri));
			}
		}
		if (!CollectionUtils.isEmpty(notations)) {
			conceptImpl.addNotations(notations);
		}
		Concept concept = conceptStorageService.createConcept(ConceptType.DOMAIN, conceptImpl);
		// add to domainMap
		for (String uri : drugbankDomain.getUris()) {
			domainMap.put(uri, concept);
		}
	}

	public void writeConcept(Drugs drugs) {
		Collection<Concept> foundConcepts = null;
		if (checkConceptExists) {
			foundConcepts = findConcept(drugs);
		}
		if (CollectionUtils.isEmpty(foundConcepts)) {
			// no luck create new
			Concept concept = createConcept(drugs, false);
			conceptStorageService.createConcept(ConceptType.CONCEPT, concept);
		} else {
			// found! update concept: force all drugbank labels as alternate
			Concept concept = createConcept(drugs, true);
			for (Concept found : foundConcepts) {
				logger.info("to concept uuid {} appending data ", found.getUuid());
				conceptStorageService.appendConcept(found.getUuid(), concept);
			}

		}
	}

	private Concept createConcept(Drugs drugs, boolean forceAlternateLabels) {
		ConceptImpl conceptImpl = new ConceptImpl();
		Collection<Label> preferredLabels = getPreferredLabels(drugs);
		Collection<Label> alternateLabels = getAlternateLabels(drugs);
		Collection<Notation> notations = getNotations(drugs);
		conceptImpl.setNotations(notations);
		if (forceAlternateLabels) {
			conceptImpl.addLabelByType(LabelType.ALTERNATE, preferredLabels);
			conceptImpl.addLabelByType(LabelType.ALTERNATE, alternateLabels);
		} else {
			conceptImpl.addLabelByType(LabelType.PREFERRED, preferredLabels);
			conceptImpl.addLabelByType(LabelType.ALTERNATE, alternateLabels);
		}
		return conceptImpl;
	}

	private Collection<Concept> findConcept(Drugs drugs) {
		// to update get the concept first
		Collection<Label> preferredLabels = getPreferredLabels(drugs);
		Collection<Label> alternateLabels = getAlternateLabels(drugs);
		Set<Label> allLabels = new HashSet<Label>();
		allLabels.addAll(preferredLabels);
		allLabels.addAll(alternateLabels);
		Collection<Concept> foundConcepts = new HashSet<Concept>();
		for (Label label : allLabels) {
			long start = System.currentTimeMillis();
			Collection<Concept> concepts = conceptQueryService.getConceptsByLabel(label);
			logger.info("{} concepts found for label {} in {} (ms) ",
					new Object[] { concepts.size(), label.getText(), (System.currentTimeMillis() - start) });
			if (!CollectionUtils.isEmpty(concepts)) {
				for (Concept concept : concepts) {
					foundConcepts.add(concept);
				}
			}
		}
		StringBuilder preferredName = new StringBuilder();
		for (Label label : preferredLabels) {
			preferredName.append(label.getText()).append("|");
		}
		for (Concept concept : foundConcepts) {
			String cui = null;
			for (Notation notation : concept.getNotations()) {
				if (notation.getDomain().getUuid().equals("75b8870d-8472-4a12-84e6-e4cae0baf5d9")) {
					cui = notation.getCode();
					break;
				}
			}
			if (cui == null) {
				logger.warn("no cui found for concept with uuid {}", concept.getUuid());
			} else {
				logger.info("drugbank drug with preferred name {} has same name as umls cui {} ", new Object[] {
						preferredLabels, cui });
			}
		}
		return foundConcepts;
	}

	private Collection<Notation> getNotations(Drugs drugs) {
		List<Notation> notations = new ArrayList<Notation>();

		// ATC can have multiple values
		if (drugs.hasAtcCode()) {
			Concept domain = domainMap.get(Drugs.ATCCODE.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllAtcCode_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}
		if (drugs.hasAhfsCode()) {
			Concept domain = domainMap.get(Drugs.AHFSCODE.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllAhfsCode_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}
		if (drugs.hasCasRegistryNumber()) {
			Concept domain = domainMap.get(Drugs.CASREGISTRYNUMBER.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllCasRegistryNumber_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasChebiId()) {
			Concept domain = domainMap.get(Drugs.CHEBIID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllChebiId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasChemicalIupacName()) {
			Concept domain = domainMap.get(Drugs.CHEMICALIUPACNAME.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllChemicalIupacName_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasDpdDrugIdNumber()) {
			Concept domain = domainMap.get(Drugs.DPDDRUGIDNUMBER.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllDpdDrugIdNumber_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasFdaLabelFiles()) {
			Concept domain = domainMap.get(Drugs.FDALABELFILES.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllFdaLabelFiles_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasGenbankId()) {
			Concept domain = domainMap.get(Drugs.GENBANKID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllGenbankId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasHetId()) {
			Concept domain = domainMap.get(Drugs.HETID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllHetId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}
		// INCHI is one domain 2 notations
		if (drugs.hasInchiIdentifier()) {
			Concept domain = domainMap.get(Drugs.INCHIIDENTIFIER.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllInchiIdentifier_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasInchiKey()) {
			Concept domain = domainMap.get(Drugs.INCHIKEY.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllInchiKey_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}
		// KEGG is one domain 2 identifiers
		if (drugs.hasKeggCompoundId()) {
			Concept domain = domainMap.get(Drugs.KEGGCOMPOUNDID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllKeggCompoundId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasKeggDrugId()) {
			Concept domain = domainMap.get(Drugs.KEGGDRUGID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllKeggDrugId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasLimsDrugId()) {
			Concept domain = domainMap.get(Drugs.LIMSDRUGID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllLimsDrugId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}
		if (drugs.hasMsdsFiles()) {
			Concept domain = domainMap.get(Drugs.MSDSFILES.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllMsdsFiles_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}
		// PDB has 3 identifiers
		if (drugs.hasPdbId()) {
			Concept domain = domainMap.get(Drugs.PDBID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllPdbId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}
		if (drugs.hasPdbExperimentalId()) {
			Concept domain = domainMap.get(Drugs.PDBEXPERIMENTALID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllPdbExperimentalId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasPdbHomologyId()) {
			Concept domain = domainMap.get(Drugs.PDBHOMOLOGYID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllPdbHomologyId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasPharmgkbId()) {
			Concept domain = domainMap.get(Drugs.PHARMGKBID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllPharmgkbId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}
		// DRUGBANK id: also see secondary accession numbers
		if (drugs.hasPrimaryAccessionNo()) {
			Concept domain = domainMap.get(Drugs.PRIMARYACCESSIONNO.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllPrimaryAccessionNo_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		// can be multiple values
		// see primary accession number
		if (drugs.hasSecondaryAccessionNumber()) {
			Concept domain = domainMap.get(Drugs.SECONDARYACCESSIONNUMBER.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllSecondaryAccessionNumber_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		// PUBCHEM has two identifiers

		if (drugs.hasPubchemCompoundId()) {
			Concept domain = domainMap.get(Drugs.PUBCHEMCOMPOUNDID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllPubchemCompoundId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasPubchemSubstanceId()) {
			Concept domain = domainMap.get(Drugs.PUBCHEMSUBSTANCEID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllPubchemSubstanceId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasRxlistLink()) {
			Concept domain = domainMap.get(Drugs.RXLISTLINK.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllRxlistLink_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		// SMILES has 2 identifiers
		if (drugs.hasSmilesStringCanonical()) {
			Concept domain = domainMap.get(Drugs.SMILESSTRINGCANONICAL.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllSmilesStringCanonical_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasSmilesStringIsomeric()) {
			Concept domain = domainMap.get(Drugs.SMILESSTRINGISOMERIC.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllSmilesStringIsomeric_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		// ignore SP name and SP page
		if (drugs.hasSwissprotId()) {
			Concept domain = domainMap.get(Drugs.SWISSPROTID.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllSwissprotId_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}

		if (drugs.hasChemicalFormula()) {
			Concept domain = domainMap.get(Drugs.CHEMICALFORMULA.toString());
			Collection<String> codes = getStringLiterals(drugs.getAllChemicalFormula_asNode());
			for (String code : codes) {
				notations.add(createNotation(domain, code));
			}
		}
		// FIXME: no WIKIPEDIA domain found in generated files? why?
		return notations;
	}

	private Collection<Label> getAlternateLabels(Drugs drugs) {
		Set<Label> labels = new HashSet<Label>();
		if (drugs.hasBrandName()) {
			Collection<String> texts = getStringLiterals(drugs.getAllBrandName_asNode());
			for (String text : texts) {
				labels.add(createLabel(text));
			}
		}

		if (drugs.hasSynonym()) {
			Collection<String> texts = getStringLiterals(drugs.getAllSynonym_asNode());
			for (String text : texts) {
				labels.add(createLabel(text));
			}
		}

		return labels;
	}

	private Collection<Label> getPreferredLabels(Drugs drugs) {
		// preferred label
		Set<Label> labels = new HashSet<Label>();
		if (drugs.hasGenericName()) {
			Collection<String> texts = getStringLiterals(drugs.getAllGenericName_asNode());
			for (String text : texts) {
				labels.add(createLabel(text));
			}
		}
		return labels;
	}

	private Label createLabel(String text) {
		LabelImpl labelImpl = new LabelImpl(text, LanguageImpl.EN);
		return labelImpl;
	}

	private Notation createNotation(Concept domain, String code) {
		NotationImpl notationImpl = new NotationImpl(domain, code);
		return notationImpl;
	}

	private Collection<String> getStringLiterals(Iterator<Node> nodeIterator) {
		List<String> stringLiterals = new ArrayList<String>();
		while (nodeIterator.hasNext()) {
			stringLiterals.add(nodeIterator.next().toString());
		}
		return stringLiterals;
	}

}
