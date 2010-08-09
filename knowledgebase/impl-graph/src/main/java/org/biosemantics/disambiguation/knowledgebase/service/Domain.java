package org.biosemantics.disambiguation.knowledgebase.service;

/**
 * All "." and "-" converted to "_" in domain names as they are illegal characters.
 * 
 */
public enum Domain {
	UMLS("UMLS 2010 aa release"), AIR("AI/RHEUM, 1993"), ALT("Alternative Billing Concepts, 2009"), AOD(
			"	Alcohol and Other Drug Thesaurus, 2000"), AOT("Authorized Osteopathic Thesaurus, 2003"), BI(
			"Beth Israel Vocabulary, 1.0"), CCPSS("Canonical Clinical Problem Statement System, 1999"), CCS(
			"	Clinical Classifications Software, 2005"), COSTAR("COSTAR, 1989-1995"), CPM(
			"Medical Entities Dictionary, 2003"), CPT("Current Procedural Terminology, 2010"), CPTSP(
			"Physicians' Current Procedural Terminology, Spanish Translation, 2001"), CSP("CRISP Thesaurus, 2006"), CST(
			"	COSTART, 1995"), DDB("Diseases Database, 2000"), DMDICD10("German translation of ICD10, 1995"), DMDUMD(
			"German translation of UMDNS, 1996"), DSM3R("DSM-III-R, 1987"), DSM4("DSM-IV, 1994"), DXP("DXplain, 1994"), FMA(
			"Foundational Model of Anatomy Ontology, 2_0"), GO("Gene Ontology, 2009_04_01"), GS(
			"Gold Standard Alchemy, 2010_02_02"), HCDT("Current Dental Terminology (CDT), 2007-2008D"), HCPCS(
			"Healthcare Common Procedure Coding System, 2010"), HCPT(
			"HCPCS Version of Current Procedural Terminology (CPT), 2010"), HHC("Home Health Care Classification, 2003"), HL7V2_5(
			"HL7 Vocabulary Version 2.5, 2003_08_30"), HL7V3_0("HL7 Vocabulary Version 3.0, 2006_05"), HLREL(
			"	ICPC2E-ICD10 relationships from Dr. Henk Lamberts, 1998"), HUGO("HUGO Gene Nomenclature, 2009_04"), ICD10(
			"ICD10, 1998"), ICD10AE("ICD10, American English Equivalents, 1998"), ICD10AM(
			"International Statistical Classification of Diseases and Related Health Problems, 10th Revision, Australian Modification, January 2000 Release"), ICD10AMAE(
			"International Statistical Classification of Diseases and Related Health Problems, Australian Modification, Americanized English Equivalents, 2000"), ICD10DUT(
			"ICD10, Dutch Translation, 200403"), ICD10PCS("ICD-10-PCS, 2009"), ICD9CM("	ICD-9-CM, 2010"), ICF(
			"	International Classification of Functioning, Disability and Health, 2008_12_19"), ICF_CY(
			"International Classification of Functioning, Disability and Health for Children and Youth, 2008"), ICNP(
			"International Classification for Nursing Practice, 2.0"), ICPC(
			"International Classification of Primary Care, 1993"), ICPC2EDUT(
			"International Classification of Primary Care 2nd Edition, Electronic, 2E, Dutch Translation, 200203"), ICPC2EENG(
			"International Classification of Primary Care 2nd Edition, Electronic, 2E, 200203"), ICPC2ICD10DUT(
			"ICPC2-ICD10 Thesaurus, Dutch Translation, 200412"), ICPC2ICD10ENG("ICPC2 - ICD10 Thesaurus, 200412"), ICPC2P(
			"ICPC-2 PLUS"), ICPCBAQ("ICPC, Basque Translation, 1993"), ICPCDAN("ICPC, Danish Translation, 1993"), ICPCDUT(
			"ICPC, Dutch Translation, 1993"), ICPCFIN("ICPC, Finnish Translation, 1993"), ICPCFRE(
			"ICPC, French Translation, 1993"), ICPCGER("	ICPC, German Translation, 1993"), ICPCHEB(
			"	ICPC, Hebrew Translation, 1993"), ICPCHUN("	ICPC, Hungarian Translation, 1993"), ICPCITA(
			"ICPC, Italian Translation, 1993"), ICPCNOR("ICPC, Norwegian Translation, 1993"), ICPCPOR(
			"ICPC, Portuguese Translation, 1993"), ICPCSPA("ICPC, Spanish Translation, 1993"), ICPCSWE(
			"	ICPC, Swedish Translation, 1993"), JABL(
			"Online Congenital Multiple Anomaly/Mental Retardation Syndromes, 1999"), KCD5(
			"Korean Standard Classification of Disease Version 5, 2008"), LCH(
			"	Library of Congress Subject Headings, 1990"), LNC("Logical Observation Identifier Names and Codes, 229"), LNC_BRADEN(
			"Braden Scale for Predicting Pressure Sore Risk, 229_1988"), LNC_CAM(
			"	Confusion Assessment Method (CAM), 229_2003"), LNC_FLACC(
			"Faces, Legs, Activity, Cry, and Consolability (FLACC) Scale, 229_1997"), LNC_MDS20(
			"Minimum Data Set, 2.0, 229_20"), LNC_MDS30("Minimum Data Set, 3.0, 229_30"), LNC_OASIS(
			"Outcome and Assessment Information Set, 229_2002"), LNC_PHQ_9("	Patient Health Questionnaire, 229_1999"), LNC_RHO(
			"	Routine Health Outcomes Ltd. (RHO), 229_2008"), LNC_WHO(
			"	Patient Monitoring Guidelines for HIV care and antiretroviral therapy (ART), 229_2006"), MBD(
			"MEDLINE (2000-2004)"), MCM("McMaster University Epidemiology Terms, 1992"), MDDB(
			"Master Drug Data Base, 2010_02_03"), MDR(
			"Medical Dictionary for Regulatory Activities Terminology (MedDRA), 12.1"), MDRCZE(
			"Medical Dictionary for Regulatory Activities Terminology (MedDRA), Czech Edition, 12.1"), MDRDUT(
			"Medical Dictionary for Regulatory Activities Terminology (MedDRA), Dutch Edition, 12.1"), MDRFRE(
			"Medical Dictionary for Regulatory Activities Terminology (MedDRA), French Edition, 12.1"), MDRGER(
			"Medical Dictionary for Regulatory Activities Terminology (MedDRA), German Edition, 12.1"), MDRITA(
			"Medical Dictionary for Regulatory Activities Terminology (MedDRA), Italian Edition, 12.1"), MDRJPN(
			"Medical Dictionary for Regulatory Activities Terminology (MedDRA), Japanese Edition, 12.0"), MDRPOR(
			"Medical Dictionary for Regulatory Activities Terminology (MedDRA), Portuguese Edition, 12.1"), MDRSPA(
			"	Medical Dictionary for Regulatory Activities Terminology (MedDRA), Spanish Edition, 12.1"), MED(
			"MEDLINE (2005-2010)"), MEDCIN("MEDCIN"), MEDLINEPLUS("MedlinePlus Health Topics, 20080614"), MMSL(
			"Multum MediSource Lexicon, 2010_02_01"), MMX("Micromedex DRUGDEX, 2010_02_01"), MSH(
			"Medical Subject Headings, 2010_2010_02_22"), MSHCZE(
			"Czech translation of the Medical Subject Headings, 2010"), MSHDUT(
			"Nederlandse vertaling van Mesh (Dutch translation of the Medical Subject Headings), 2005"), MSHFIN(
			"	Finnish translation of the Medical Subject Headings, 2008"), MSHFRE(
			"Thesaurus Biomedical Francais/Anglais [French translation of MeSH], 2010"), MSHGER(
			"German translation of the Medical Subject Headings, 2010"), MSHITA(
			"Italian translation of Medical Subject Headings, 2010"), MSHJPN(
			"	Japanese translation of the Medical Subject Headings, 2008"), MSHLAV(
			"Latvian translation of the Medical Subject Headings, 2010"), MSHPOR(
			"Descritores em Ciencias da Saude (Portuguese translation of the Medical Subject Headings), 2010"), MSHRUS(
			"Russian Translation of MeSH, 2010"), MSHSCR("Croatian translation of the Medical Subject Headings, 2010"), MSHSPA(
			"Descritores en Ciencias de la Salud (Spanish translation of the Medical Subject Headings), 2010"), MSHSWE(
			"Swedish translations of the Medical Subject Headings, 2010"), MTH("UMLS Metathesaurus"), MTHCH(
			"Metathesaurus CPT Hierarchical Terms, 2010"), MTHFDA(
			"Metathesaurus FDA National Drug Code Directory, 2010_02_01"), MTHHH(
			"Metathesaurus HCPCS Hierarchical Terms, 2010"), MTHHL7V2_5(
			"HL7 Vocabulary Version 2.5, 7-bit equivalents, 2003_08"), MTHICD9(
			"Metathesaurus additional entry terms for ICD-9-CM, 2010"), MTHICPC2EAE(
			"	International Classification of Primary Care 2nd Edition, Electronic, 2E, American English Equivalents, 200203"), MTHICPC2ICD107B(
			"ICPC2 - ICD10 Thesaurus, 7-bit Equivalents, 0412"), MTHICPC2ICD10AE(
			"ICPC2 - ICD10 Thesaurus, American English Equivalents, 0412"), MTHMST(
			"Metathesaurus Version of Minimal Standard Terminology Digestive Endoscopy, 2001"), MTHMSTFRE(
			"Metathesaurus Version of Minimal Standard Terminology Digestive Endoscopy, French Translation, 2001"), MTHMSTITA(
			"	Metathesaurus Version of Minimal Standard Terminology Digestive Endoscopy, Italian Translation, 2001"), MTHSPL(
			"Metathesaurus FDA Structured Product Labels, 2010_02_22"), NAN(
			"NANDA nursing diagnoses: definitions & classification, 2004"), NCBI("NCBI Taxonomy, 2009_04_13"), NCI(
			"NCI Thesaurus, 2009_04D"), NCISEER("NCI SEER ICD Neoplasm Code Mappings, 1999"), NDDF(
			"National Drug Data File Plus Source Vocabulary, 2010_02_05"), NDFRT(
			"National Drug File - Reference Terminology Public Inferred Edition, 2008_03_11"), NEU(
			"Neuronames Brain Hierarchy, 1999"), NIC("Nursing Interventions Classification (NIC), 2005"), NLM_MED(
			"National Library of Medicine Medline Data"), NOC("Nursing Outcomes Classification, 3rd Edition"), OMIM(
			"Online Mendelian Inheritance in Man, 2009_05_01"), OMS("Omaha System, 2005"), PCDS(
			"Patient Care Data Set, 1997"), PDQ("Physician Data Query, 2007_02"), PNDS(
			"Perioperative Nursing Data Set, 2nd edition, 2002"), PPAC(
			"Pharmacy Practice Activity Classification, 1998"), PSY("Thesaurus of Psychological Index Terms, 2004"), QMR(
			"Quick Medical Reference (QMR), 1996"), RAM("QMR clinically related terms from Randolph A. Miller, 1999"), RCD(
			"Clinical Terms Version 3 (CTV3) (Read Codes), 1999"), RCDAE(
			"Read thesaurus, American English Equivalents, 1999"), RCDSA(
			"Read thesaurus Americanized Synthesized Terms, 1999"), RCDSY("Read thesaurus, Synthesized Terms, 1999"), RXNORM(
			"RxNorm Vocabulary, 09AB_100301F"), SCTSPA(
			"SNOMED Terminos Clinicos (SNOMED CT), Edicion en Espanol, Distribucion Internacional, Octubre de 2009, 2009_10_31"), SNM(
			"SNOMED-2, 2"), SNMI("SNOMED International, 1998"), SNOMEDCT("SNOMED Clinical Terms, 2010_01_31"), SPN(
			"Standard Product Nomenclature, 2003"), SRC("Metathesaurus Source Terminology Names"), ULT(
			"UltraSTAR, 1993"), UMD("UMDNS: product category thesaurus, 2010"), USPMG("USP Model Guidelines, 2004"), UWDA(
			"University of Washington Digital Anatomist, 1.7.3"), VANDF(
			"Veterans Health Administration National Drug File, 2010_01_25"), WHO(
			"WHO Adverse Reaction Terminology, 1997"), WHOFRE("WHOART, French Translation, 1997"), WHOGER(
			"WHOART, German Translation, 1997"), WHOPOR("WHOART, Portuguese Translation, 1997"), WHOSPA(
			"WHOART, Spanish Translation, 1997");

	private final String officialName;

	private Domain(String officialName) {
		this.officialName = officialName;
	}

	public String getOfficialName() {
		return officialName;
	}

}
