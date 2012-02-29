
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MeshHeadingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MeshHeadingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DescriptorName" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}DescriptorNameType"/>
 *         &lt;element name="QualifierName" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}QualifierNameType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeshHeadingType", propOrder = {
    "descriptorName",
    "qualifierName"
})
public class MeshHeadingType {

    @XmlElement(name = "DescriptorName", required = true)
    protected DescriptorNameType descriptorName;
    @XmlElement(name = "QualifierName")
    protected List<QualifierNameType> qualifierName;

    /**
     * Gets the value of the descriptorName property.
     * 
     * @return
     *     possible object is
     *     {@link DescriptorNameType }
     *     
     */
    public DescriptorNameType getDescriptorName() {
        return descriptorName;
    }

    /**
     * Sets the value of the descriptorName property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescriptorNameType }
     *     
     */
    public void setDescriptorName(DescriptorNameType value) {
        this.descriptorName = value;
    }

    /**
     * Gets the value of the qualifierName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qualifierName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQualifierName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QualifierNameType }
     * 
     * 
     */
    public List<QualifierNameType> getQualifierName() {
        if (qualifierName == null) {
            qualifierName = new ArrayList<QualifierNameType>();
        }
        return this.qualifierName;
    }

}
