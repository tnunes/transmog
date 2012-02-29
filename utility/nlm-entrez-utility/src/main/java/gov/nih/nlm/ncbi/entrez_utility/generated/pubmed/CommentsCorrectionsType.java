
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for CommentsCorrectionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommentsCorrectionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}RefSource"/>
 *         &lt;element name="PMID" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PMIDType" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Note" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="RefType" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="CommentOn"/>
 *             &lt;enumeration value="CommentIn"/>
 *             &lt;enumeration value="ErratumIn"/>
 *             &lt;enumeration value="ErratumFor"/>
 *             &lt;enumeration value="PartialRetractionIn"/>
 *             &lt;enumeration value="PartialRetractionOf"/>
 *             &lt;enumeration value="RepublishedFrom"/>
 *             &lt;enumeration value="RepublishedIn"/>
 *             &lt;enumeration value="RetractionOf"/>
 *             &lt;enumeration value="RetractionIn"/>
 *             &lt;enumeration value="UpdateIn"/>
 *             &lt;enumeration value="UpdateOf"/>
 *             &lt;enumeration value="SummaryForPatientsIn"/>
 *             &lt;enumeration value="OriginalReportIn"/>
 *             &lt;enumeration value="ReprintOf"/>
 *             &lt;enumeration value="ReprintIn"/>
 *             &lt;enumeration value="Cites"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommentsCorrectionsType", propOrder = {
    "refSource",
    "pmid",
    "note"
})
public class CommentsCorrectionsType {

    @XmlElement(name = "RefSource", required = true)
    protected String refSource;
    @XmlElement(name = "PMID")
    protected PMIDType pmid;
    @XmlElement(name = "Note")
    protected String note;
    @XmlAttribute(name = "RefType", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String refType;

    /**
     * Gets the value of the refSource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefSource() {
        return refSource;
    }

    /**
     * Sets the value of the refSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefSource(String value) {
        this.refSource = value;
    }

    /**
     * Gets the value of the pmid property.
     * 
     * @return
     *     possible object is
     *     {@link PMIDType }
     *     
     */
    public PMIDType getPMID() {
        return pmid;
    }

    /**
     * Sets the value of the pmid property.
     * 
     * @param value
     *     allowed object is
     *     {@link PMIDType }
     *     
     */
    public void setPMID(PMIDType value) {
        this.pmid = value;
    }

    /**
     * Gets the value of the note property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNote(String value) {
        this.note = value;
    }

    /**
     * Gets the value of the refType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefType() {
        return refType;
    }

    /**
     * Sets the value of the refType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefType(String value) {
        this.refType = value;
    }

}
