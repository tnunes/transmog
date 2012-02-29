
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CommentsCorrectionsListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommentsCorrectionsListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CommentsCorrections" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}CommentsCorrectionsType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommentsCorrectionsListType", propOrder = {
    "commentsCorrections"
})
public class CommentsCorrectionsListType {

    @XmlElement(name = "CommentsCorrections", required = true)
    protected List<CommentsCorrectionsType> commentsCorrections;

    /**
     * Gets the value of the commentsCorrections property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the commentsCorrections property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommentsCorrections().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommentsCorrectionsType }
     * 
     * 
     */
    public List<CommentsCorrectionsType> getCommentsCorrections() {
        if (commentsCorrections == null) {
            commentsCorrections = new ArrayList<CommentsCorrectionsType>();
        }
        return this.commentsCorrections;
    }

}
