
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PubmedBookArticleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PubmedBookArticleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BookDocument" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}BookDocumentType"/>
 *         &lt;element name="PubmedBookData" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PubmedBookDataType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PubmedBookArticleType", propOrder = {
    "bookDocument",
    "pubmedBookData"
})
public class PubmedBookArticleType {

    @XmlElement(name = "BookDocument", required = true)
    protected BookDocumentType bookDocument;
    @XmlElement(name = "PubmedBookData")
    protected PubmedBookDataType pubmedBookData;

    /**
     * Gets the value of the bookDocument property.
     * 
     * @return
     *     possible object is
     *     {@link BookDocumentType }
     *     
     */
    public BookDocumentType getBookDocument() {
        return bookDocument;
    }

    /**
     * Sets the value of the bookDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookDocumentType }
     *     
     */
    public void setBookDocument(BookDocumentType value) {
        this.bookDocument = value;
    }

    /**
     * Gets the value of the pubmedBookData property.
     * 
     * @return
     *     possible object is
     *     {@link PubmedBookDataType }
     *     
     */
    public PubmedBookDataType getPubmedBookData() {
        return pubmedBookData;
    }

    /**
     * Sets the value of the pubmedBookData property.
     * 
     * @param value
     *     allowed object is
     *     {@link PubmedBookDataType }
     *     
     */
    public void setPubmedBookData(PubmedBookDataType value) {
        this.pubmedBookData = value;
    }

}
