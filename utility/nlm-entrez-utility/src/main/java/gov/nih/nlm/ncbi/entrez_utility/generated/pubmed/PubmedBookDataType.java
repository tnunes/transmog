
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PubmedBookDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PubmedBookDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="History" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}HistoryType" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PublicationStatus"/>
 *         &lt;element name="ArticleIdList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ArticleIdListType"/>
 *         &lt;element name="ObjectList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ObjectListType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PubmedBookDataType", propOrder = {
    "history",
    "publicationStatus",
    "articleIdList",
    "objectList"
})
public class PubmedBookDataType {

    @XmlElement(name = "History")
    protected HistoryType history;
    @XmlElement(name = "PublicationStatus", required = true)
    protected String publicationStatus;
    @XmlElement(name = "ArticleIdList", required = true)
    protected ArticleIdListType articleIdList;
    @XmlElement(name = "ObjectList")
    protected ObjectListType objectList;

    /**
     * Gets the value of the history property.
     * 
     * @return
     *     possible object is
     *     {@link HistoryType }
     *     
     */
    public HistoryType getHistory() {
        return history;
    }

    /**
     * Sets the value of the history property.
     * 
     * @param value
     *     allowed object is
     *     {@link HistoryType }
     *     
     */
    public void setHistory(HistoryType value) {
        this.history = value;
    }

    /**
     * Gets the value of the publicationStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicationStatus() {
        return publicationStatus;
    }

    /**
     * Sets the value of the publicationStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicationStatus(String value) {
        this.publicationStatus = value;
    }

    /**
     * Gets the value of the articleIdList property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleIdListType }
     *     
     */
    public ArticleIdListType getArticleIdList() {
        return articleIdList;
    }

    /**
     * Sets the value of the articleIdList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleIdListType }
     *     
     */
    public void setArticleIdList(ArticleIdListType value) {
        this.articleIdList = value;
    }

    /**
     * Gets the value of the objectList property.
     * 
     * @return
     *     possible object is
     *     {@link ObjectListType }
     *     
     */
    public ObjectListType getObjectList() {
        return objectList;
    }

    /**
     * Sets the value of the objectList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectListType }
     *     
     */
    public void setObjectList(ObjectListType value) {
        this.objectList = value;
    }

}
