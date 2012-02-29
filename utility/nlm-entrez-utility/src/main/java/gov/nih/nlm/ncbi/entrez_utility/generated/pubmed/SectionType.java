
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SectionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SectionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LocationLabel" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}LocationLabelType" minOccurs="0"/>
 *         &lt;element name="SectionTitle" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}SectionTitleType"/>
 *         &lt;element name="Section" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}SectionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SectionType", propOrder = {
    "locationLabel",
    "sectionTitle",
    "section"
})
public class SectionType {

    @XmlElement(name = "LocationLabel")
    protected LocationLabelType locationLabel;
    @XmlElement(name = "SectionTitle", required = true)
    protected SectionTitleType sectionTitle;
    @XmlElement(name = "Section")
    protected List<SectionType> section;

    /**
     * Gets the value of the locationLabel property.
     * 
     * @return
     *     possible object is
     *     {@link LocationLabelType }
     *     
     */
    public LocationLabelType getLocationLabel() {
        return locationLabel;
    }

    /**
     * Sets the value of the locationLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationLabelType }
     *     
     */
    public void setLocationLabel(LocationLabelType value) {
        this.locationLabel = value;
    }

    /**
     * Gets the value of the sectionTitle property.
     * 
     * @return
     *     possible object is
     *     {@link SectionTitleType }
     *     
     */
    public SectionTitleType getSectionTitle() {
        return sectionTitle;
    }

    /**
     * Sets the value of the sectionTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link SectionTitleType }
     *     
     */
    public void setSectionTitle(SectionTitleType value) {
        this.sectionTitle = value;
    }

    /**
     * Gets the value of the section property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the section property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SectionType }
     * 
     * 
     */
    public List<SectionType> getSection() {
        if (section == null) {
            section = new ArrayList<SectionType>();
        }
        return this.section;
    }

}
