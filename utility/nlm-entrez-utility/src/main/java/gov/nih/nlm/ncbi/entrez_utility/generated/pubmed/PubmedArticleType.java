
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PubmedArticleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PubmedArticleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MedlineCitation" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}MedlineCitationType"/>
 *         &lt;element name="PubmedData" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PubmedDataType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PubmedArticleType", propOrder = {
    "medlineCitation",
    "pubmedData"
})
public class PubmedArticleType {

    @XmlElement(name = "MedlineCitation", required = true)
    protected MedlineCitationType medlineCitation;
    @XmlElement(name = "PubmedData")
    protected PubmedDataType pubmedData;

    /**
     * Gets the value of the medlineCitation property.
     * 
     * @return
     *     possible object is
     *     {@link MedlineCitationType }
     *     
     */
    public MedlineCitationType getMedlineCitation() {
        return medlineCitation;
    }

    /**
     * Sets the value of the medlineCitation property.
     * 
     * @param value
     *     allowed object is
     *     {@link MedlineCitationType }
     *     
     */
    public void setMedlineCitation(MedlineCitationType value) {
        this.medlineCitation = value;
    }

    /**
     * Gets the value of the pubmedData property.
     * 
     * @return
     *     possible object is
     *     {@link PubmedDataType }
     *     
     */
    public PubmedDataType getPubmedData() {
        return pubmedData;
    }

    /**
     * Sets the value of the pubmedData property.
     * 
     * @param value
     *     allowed object is
     *     {@link PubmedDataType }
     *     
     */
    public void setPubmedData(PubmedDataType value) {
        this.pubmedData = value;
    }

}
