/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.biosemantics.eviped.tools.service;

import gov.nih.nlm.ncbi.eutils.generated.efetch.MeshHeading;
import gov.nih.nlm.ncbi.eutils.generated.efetch.PubmedArticle;
import gov.nih.nlm.ncbi.eutils.generated.efetch.QualifierName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author bhsingh
 */
public class Article implements Serializable {

	private int pmid;
	private int weight;
	private String country;
	private int publishedYear;
	private String journalName;
	private String journalAbbreviation;
	private String title;
	private Collection<String> meshHeadings;

	public Article() {
	}

	public Article(int weight, PubmedArticle pubmedArticle) {
		this.weight = weight;
		try {
			this.journalName = pubmedArticle.getMedlineCitation().getArticle().getJournal().getTitle();
		} catch (Exception e) {
		}
		try {
			this.pmid = pubmedArticle.getMedlineCitation().getPMID().getValue().intValue();
		} catch (Exception e) {
		}
		try {
			country = pubmedArticle.getMedlineCitation().getMedlineJournalInfo().getCountry();
		} catch (Exception e) {
		}
		try {
			this.publishedYear = Integer.valueOf(pubmedArticle.getMedlineCitation().getArticle().getJournal().getJournalIssue().getPubDate().getYear().toString());
		} catch (Exception e) {
			this.publishedYear = pubmedArticle.getMedlineCitation().getDateCompleted().getYear().intValue();
		}
		try {
			this.journalAbbreviation = pubmedArticle.getMedlineCitation().getArticle().getJournal().getISOAbbreviation();
		} catch (Exception e) {
		}
		try {
			this.title = pubmedArticle.getMedlineCitation().getArticle().getArticleTitle();
		} catch (Exception e) {
		}
		try {
			this.meshHeadings = new ArrayList<String>();
			List<MeshHeading> meshHeadings = pubmedArticle.getMedlineCitation().getMeshHeadingList().getMeshHeading();
			for (MeshHeading meshHeading : meshHeadings) {
				if (meshHeading.getQualifierName() != null) {
					List<QualifierName> qualifierNames = meshHeading.getQualifierName();
					for (QualifierName qualifierName : qualifierNames) {
						this.meshHeadings.add(meshHeading.getDescriptorName().getContent() + "/" + qualifierName.getContent());
					}
				} else {
					this.meshHeadings.add(meshHeading.getDescriptorName().getContent());
				}
			}
		} catch (Exception e) {
		}
	}

	public String getCountry() {
		return country;
	}

	public String getJournalName() {
		return journalName;
	}

	public int getPmid() {
		return pmid;
	}

	public int getPublishedYear() {
		return publishedYear;
	}

	public int getWeight() {
		return weight;
	}

	public String getJournalAbbreviation() {
		return journalAbbreviation;
	}

	public String getTitle() {
		return title;
	}
}
