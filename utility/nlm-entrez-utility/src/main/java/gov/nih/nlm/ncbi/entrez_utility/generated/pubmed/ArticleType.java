
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for ArticleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArticleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Journal" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}JournalType"/>
 *         &lt;element name="ArticleTitle" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ArticleTitleType"/>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="Pagination" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PaginationType"/>
 *             &lt;element name="ELocationID" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ELocationIDType" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element name="ELocationID" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ELocationIDType" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *         &lt;element name="Abstract" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}AbstractType" minOccurs="0"/>
 *         &lt;element name="Affiliation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AuthorList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}AuthorListType" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Language" maxOccurs="unbounded"/>
 *         &lt;element name="DataBankList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}DataBankListType" minOccurs="0"/>
 *         &lt;element name="GrantList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}GrantListType" minOccurs="0"/>
 *         &lt;element name="PublicationTypeList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PublicationTypeListType"/>
 *         &lt;element name="VernacularTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ArticleDate" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ArticleDateType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="PubModel" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="Print"/>
 *             &lt;enumeration value="Print-Electronic"/>
 *             &lt;enumeration value="Electronic"/>
 *             &lt;enumeration value="Electronic-Print"/>
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
@XmlType(name = "ArticleType", propOrder = {
    "content"
})
public class ArticleType {

    @XmlElementRefs({
        @XmlElementRef(name = "VernacularTitle", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "ArticleDate", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "AuthorList", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "ArticleTitle", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "Journal", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "PublicationTypeList", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "Pagination", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "GrantList", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "Abstract", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "ELocationID", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "Language", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "Affiliation", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class),
        @XmlElementRef(name = "DataBankList", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed", type = JAXBElement.class)
    })
    protected List<JAXBElement<?>> content;
    @XmlAttribute(name = "PubModel", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String pubModel;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "ELocationID" is used by two different parts of a schema. See: 
     * line 89 of http://www.ncbi.nlm.nih.gov/entrez/eutils/soap/v2.0/efetch_pubmed.xsd
     * line 87 of http://www.ncbi.nlm.nih.gov/entrez/eutils/soap/v2.0/efetch_pubmed.xsd
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
     * {@link JAXBElement }{@code <}{@link ArticleDateType }{@code >}
     * {@link JAXBElement }{@code <}{@link AuthorListType }{@code >}
     * {@link JAXBElement }{@code <}{@link ArticleTitleType }{@code >}
     * {@link JAXBElement }{@code <}{@link JournalType }{@code >}
     * {@link JAXBElement }{@code <}{@link PublicationTypeListType }{@code >}
     * {@link JAXBElement }{@code <}{@link PaginationType }{@code >}
     * {@link JAXBElement }{@code <}{@link GrantListType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractType }{@code >}
     * {@link JAXBElement }{@code <}{@link ELocationIDType }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link DataBankListType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getContent() {
        if (content == null) {
            content = new ArrayList<JAXBElement<?>>();
        }
        return this.content;
    }

    /**
     * Gets the value of the pubModel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubModel() {
        return pubModel;
    }

    /**
     * Sets the value of the pubModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubModel(String value) {
        this.pubModel = value;
    }

}
