
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
 *         &lt;element name="MedlineCitation" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}MedlineCitationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="DeleteCitation" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}DeleteCitationType" minOccurs="0"/>
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
    "medlineCitation",
    "deleteCitation"
})
@XmlRootElement(name = "MedlineCitationSet")
public class MedlineCitationSet {

    @XmlElement(name = "MedlineCitation")
    protected List<MedlineCitationType> medlineCitation;
    @XmlElement(name = "DeleteCitation")
    protected DeleteCitationType deleteCitation;

    /**
     * Gets the value of the medlineCitation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the medlineCitation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMedlineCitation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MedlineCitationType }
     * 
     * 
     */
    public List<MedlineCitationType> getMedlineCitation() {
        if (medlineCitation == null) {
            medlineCitation = new ArrayList<MedlineCitationType>();
        }
        return this.medlineCitation;
    }

    /**
     * Gets the value of the deleteCitation property.
     * 
     * @return
     *     possible object is
     *     {@link DeleteCitationType }
     *     
     */
    public DeleteCitationType getDeleteCitation() {
        return deleteCitation;
    }

    /**
     * Sets the value of the deleteCitation property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeleteCitationType }
     *     
     */
    public void setDeleteCitation(DeleteCitationType value) {
        this.deleteCitation = value;
    }

}
