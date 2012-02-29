
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaginationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaginationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}StartPage"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}EndPage" minOccurs="0"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}MedlinePgn" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}MedlinePgn"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaginationType", propOrder = {
    "content"
})
public class PaginationType {

    @XmlElementRefs({
        @XmlElementRef(name = "StartPage", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "EndPage", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "MedlinePgn", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class)
    })
    protected List<JAXBElement<String>> content;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "MedlinePgn" is used by two different parts of a schema. See: 
     * line 889 of http://www.ncbi.nlm.nih.gov/entrez/eutils/soap/v2.0/efetch_pubmed.xsd
     * line 887 of http://www.ncbi.nlm.nih.gov/entrez/eutils/soap/v2.0/efetch_pubmed.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * 
     */
    public List<JAXBElement<String>> getContent() {
        if (content == null) {
            content = new ArrayList<JAXBElement<String>>();
        }
        return this.content;
    }

}
