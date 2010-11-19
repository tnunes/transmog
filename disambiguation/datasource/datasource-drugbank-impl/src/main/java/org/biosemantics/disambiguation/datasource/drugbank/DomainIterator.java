package org.biosemantics.disambiguation.datasource.drugbank;

import java.util.Iterator;

import org.biosemantics.disambiguation.datasource.drugbank.DomainIterator.DrugbankDomain;

import ca.drugbank.generated.Drugs;

public class DomainIterator implements Iterator<DrugbankDomain> {

	private int counter = 0;

	public enum DrugbankDomain {

		DRUGBANK("Drugbank", new String[] { Drugs.PRIMARYACCESSIONNO.toString(),
				Drugs.SECONDARYACCESSIONNUMBER.toString() }), ATC(
				"Anatomical Therapeutic Chemical (ATC) classification system", Drugs.ATCCODE.toString()), AHFS(
				"AHFS Drug Information", Drugs.AHFSCODE.toString()), CAS_REGISTRY(
				"American Chemical Society Registry Number", Drugs.CASREGISTRYNUMBER.toString()), CHEBI(
				"Chemical Entities of Biological Interest", Drugs.CHEBIID.toString()), IUPAC(
				"International Union of Pure and Applied Chemistry", Drugs.CHEMICALIUPACNAME.toString()), DPD(
				"Drug Product Database", Drugs.DPDDRUGIDNUMBER.toString()), FDA_LABEL_FILES("FDA Label Files",
				Drugs.FDALABELFILES.toString()), GENE_BANK("Gene Bank", Drugs.GENBANKID.toString()), HET("HET ID",
				Drugs.HETID.toString()), INCHI("International Chemical Identifier", new String[] {
				Drugs.INCHIIDENTIFIER.toString(), Drugs.INCHIKEY.toString() }), KEGG(
				"Kyoto Encyclopedia of Genes and Genomes", new String[] { Drugs.KEGGCOMPOUNDID.toString(),
						Drugs.KEGGDRUGID.toString() }), LIMS("Laboratory information management system",
				Drugs.LIMSDRUGID.toString()), PBD("RCSB Protein Data Bank", new String[] { Drugs.PDBID.toString(),
				Drugs.PDBEXPERIMENTALID.toString(), Drugs.PDBHOMOLOGYID.toString() }), PHARMGKB(
				"Pharmacogenomics Knowledge Base", Drugs.PHARMGKBID.toString()), PUBCHEM("PubChem", new String[] {
				Drugs.PUBCHEMCOMPOUNDID.toString(), Drugs.PUBCHEMSUBSTANCEID.toString() }), RXLIST("RxList",
				Drugs.RXLISTLINK.toString()), SMILES("Simplified molecular input line entry specification",
				new String[] { Drugs.SMILESSTRINGCANONICAL.toString(), Drugs.SMILESSTRINGISOMERIC.toString() }), SWISSPROT(
				"Uni Prot", Drugs.SWISSPROTID.toString());
		private final String[] uris;
		private final String officialName;

		private DrugbankDomain(String officialName, String... uris) {
			this.uris = uris;
			this.officialName = officialName;
		}

		public String[] getUris() {
			return uris;
		}

		public String getOfficialName() {
			return officialName;
		}

		public static final DrugbankDomain getDefaultDomain() {
			return DRUGBANK;
		}
	}

	@Override
	public boolean hasNext() {
		return counter < DrugbankDomain.values().length;
	}

	@Override
	public DrugbankDomain next() {
		return DrugbankDomain.values()[counter++];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

}
