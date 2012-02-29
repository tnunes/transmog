
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="BookDocument" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}BookDocumentType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="DeleteDocument" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}DeleteDocumentType" minOccurs="0"/>
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
    "bookDocument",
    "deleteDocument"
})
@XmlRootElement(name = "BookDocumentSet")
public class BookDocumentSet {

    @XmlElement(name = "BookDocument")
    protected List<BookDocumentType> bookDocument;
    @XmlElement(name = "DeleteDocument")
    protected DeleteDocumentType deleteDocument;

    /**
     * Gets the value of the bookDocument property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bookDocument property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBookDocument().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BookDocumentType }
     * 
     * 
     */
    public List<BookDocumentType> getBookDocument() {
        if (bookDocument == null) {
            bookDocument = new ArrayList<BookDocumentType>();
        }
        return this.bookDocument;
    }

    /**
     * Gets the value of the deleteDocument property.
     * 
     * @return
     *     possible object is
     *     {@link DeleteDocumentType }
     *     
     */
    public DeleteDocumentType getDeleteDocument() {
        return deleteDocument;
    }

    /**
     * Sets the value of the deleteDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeleteDocumentType }
     *     
     */
    public void setDeleteDocument(DeleteDocumentType value) {
        this.deleteDocument = value;
    }

}
