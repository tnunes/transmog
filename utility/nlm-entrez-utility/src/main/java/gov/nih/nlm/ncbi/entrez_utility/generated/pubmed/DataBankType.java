
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataBankType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataBankType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}DataBankName"/>
 *         &lt;element name="AccessionNumberList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}AccessionNumberListType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataBankType", propOrder = {
    "dataBankName",
    "accessionNumberList"
})
public class DataBankType {

    @XmlElement(name = "DataBankName", required = true)
    protected String dataBankName;
    @XmlElement(name = "AccessionNumberList")
    protected AccessionNumberListType accessionNumberList;

    /**
     * Gets the value of the dataBankName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataBankName() {
        return dataBankName;
    }

    /**
     * Sets the value of the dataBankName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataBankName(String value) {
        this.dataBankName = value;
    }

    /**
     * Gets the value of the accessionNumberList property.
     * 
     * @return
     *     possible object is
     *     {@link AccessionNumberListType }
     *     
     */
    public AccessionNumberListType getAccessionNumberList() {
        return accessionNumberList;
    }

    /**
     * Sets the value of the accessionNumberList property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccessionNumberListType }
     *     
     */
    public void setAccessionNumberList(AccessionNumberListType value) {
        this.accessionNumberList = value;
    }

}
