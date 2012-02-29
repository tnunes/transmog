
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JournalType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JournalType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ISSN" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ISSNType" minOccurs="0"/>
 *         &lt;element name="JournalIssue" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}JournalIssueType"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Title" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ISOAbbreviation" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JournalType", propOrder = {
    "issn",
    "journalIssue",
    "title",
    "isoAbbreviation"
})
public class JournalType {

    @XmlElement(name = "ISSN")
    protected ISSNType issn;
    @XmlElement(name = "JournalIssue", required = true)
    protected JournalIssueType journalIssue;
    @XmlElement(name = "Title")
    protected String title;
    @XmlElement(name = "ISOAbbreviation")
    protected String isoAbbreviation;

    /**
     * Gets the value of the issn property.
     * 
     * @return
     *     possible object is
     *     {@link ISSNType }
     *     
     */
    public ISSNType getISSN() {
        return issn;
    }

    /**
     * Sets the value of the issn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ISSNType }
     *     
     */
    public void setISSN(ISSNType value) {
        this.issn = value;
    }

    /**
     * Gets the value of the journalIssue property.
     * 
     * @return
     *     possible object is
     *     {@link JournalIssueType }
     *     
     */
    public JournalIssueType getJournalIssue() {
        return journalIssue;
    }

    /**
     * Sets the value of the journalIssue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JournalIssueType }
     *     
     */
    public void setJournalIssue(JournalIssueType value) {
        this.journalIssue = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the isoAbbreviation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getISOAbbreviation() {
        return isoAbbreviation;
    }

    /**
     * Sets the value of the isoAbbreviation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setISOAbbreviation(String value) {
        this.isoAbbreviation = value;
    }

}
