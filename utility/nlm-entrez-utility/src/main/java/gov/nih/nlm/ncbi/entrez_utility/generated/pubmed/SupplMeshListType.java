
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SupplMeshListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupplMeshListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SupplMeshName" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}SupplMeshNameType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupplMeshListType", propOrder = {
    "supplMeshName"
})
public class SupplMeshListType {

    @XmlElement(name = "SupplMeshName", required = true)
    protected List<SupplMeshNameType> supplMeshName;

    /**
     * Gets the value of the supplMeshName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplMeshName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplMeshName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupplMeshNameType }
     * 
     * 
     */
    public List<SupplMeshNameType> getSupplMeshName() {
        if (supplMeshName == null) {
            supplMeshName = new ArrayList<SupplMeshNameType>();
        }
        return this.supplMeshName;
    }

}
