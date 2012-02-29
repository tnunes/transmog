
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ERROR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PubmedArticleSet" minOccurs="0"/>
 *         &lt;element name="IdList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}IdListType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "error",
    "pubmedArticleSet",
    "idList"
})
@XmlRootElement(name = "eFetchResult")
public class EFetchResult {

    @XmlElement(name = "ERROR")
    protected String error;
    @XmlElement(name = "PubmedArticleSet")
    protected PubmedArticleSet pubmedArticleSet;
    @XmlElement(name = "IdList")
    protected IdListType idList;

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getERROR() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setERROR(String value) {
        this.error = value;
    }

    /**
     * Gets the value of the pubmedArticleSet property.
     * 
     * @return
     *     possible object is
     *     {@link PubmedArticleSet }
     *     
     */
    public PubmedArticleSet getPubmedArticleSet() {
        return pubmedArticleSet;
    }

    /**
     * Sets the value of the pubmedArticleSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link PubmedArticleSet }
     *     
     */
    public void setPubmedArticleSet(PubmedArticleSet value) {
        this.pubmedArticleSet = value;
    }

    /**
     * Gets the value of the idList property.
     * 
     * @return
     *     possible object is
     *     {@link IdListType }
     *     
     */
    public IdListType getIdList() {
        return idList;
    }

    /**
     * Sets the value of the idList property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdListType }
     *     
     */
    public void setIdList(IdListType value) {
        this.idList = value;
    }

}
