
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArticleIdListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArticleIdListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ArticleId" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ArticleIdType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArticleIdListType", propOrder = {
    "articleId"
})
public class ArticleIdListType {

    @XmlElement(name = "ArticleId", required = true)
    protected List<ArticleIdType> articleId;

    /**
     * Gets the value of the articleId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the articleId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArticleId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArticleIdType }
     * 
     * 
     */
    public List<ArticleIdType> getArticleId() {
        if (articleId == null) {
            articleId = new ArrayList<ArticleIdType>();
        }
        return this.articleId;
    }

}
