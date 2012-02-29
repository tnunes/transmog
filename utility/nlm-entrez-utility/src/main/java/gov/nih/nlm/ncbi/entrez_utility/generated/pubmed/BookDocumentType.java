
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BookDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BookDocumentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PMID" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PMIDType"/>
 *         &lt;element name="ArticleIdList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ArticleIdListType"/>
 *         &lt;element name="Book" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}BookType"/>
 *         &lt;element name="LocationLabel" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}LocationLabelType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ArticleTitle" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ArticleTitleType" minOccurs="0"/>
 *         &lt;element name="VernacularTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pagination" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PaginationType" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Language" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="AuthorList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}AuthorListType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}GroupList" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PublicationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Abstract" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}AbstractType" minOccurs="0"/>
 *         &lt;element name="Sections" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}SectionsType" minOccurs="0"/>
 *         &lt;element name="KeywordList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}KeywordListType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ContributionDate" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ContributionDateType" minOccurs="0"/>
 *         &lt;element name="DateRevised" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}DateRevisedType" minOccurs="0"/>
 *         &lt;element name="CitationString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GrantList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}GrantListType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookDocumentType", propOrder = {
    "pmid",
    "articleIdList",
    "book",
    "locationLabel",
    "articleTitle",
    "vernacularTitle",
    "pagination",
    "language",
    "authorList",
    "groupList",
    "publicationType",
    "_abstract",
    "sections",
    "keywordList",
    "contributionDate",
    "dateRevised",
    "citationString",
    "grantList"
})
public class BookDocumentType {

    @XmlElement(name = "PMID", required = true)
    protected PMIDType pmid;
    @XmlElement(name = "ArticleIdList", required = true)
    protected ArticleIdListType articleIdList;
    @XmlElement(name = "Book", required = true)
    protected BookType book;
    @XmlElement(name = "LocationLabel")
    protected List<LocationLabelType> locationLabel;
    @XmlElement(name = "ArticleTitle")
    protected ArticleTitleType articleTitle;
    @XmlElement(name = "VernacularTitle")
    protected String vernacularTitle;
    @XmlElement(name = "Pagination")
    protected PaginationType pagination;
    @XmlElement(name = "Language")
    protected List<String> language;
    @XmlElement(name = "AuthorList")
    protected List<AuthorListType> authorList;
    @XmlElement(name = "GroupList")
    protected GroupList groupList;
    @XmlElement(name = "PublicationType")
    protected List<String> publicationType;
    @XmlElement(name = "Abstract")
    protected AbstractType _abstract;
    @XmlElement(name = "Sections")
    protected SectionsType sections;
    @XmlElement(name = "KeywordList")
    protected List<KeywordListType> keywordList;
    @XmlElement(name = "ContributionDate")
    protected ContributionDateType contributionDate;
    @XmlElement(name = "DateRevised")
    protected DateRevisedType dateRevised;
    @XmlElement(name = "CitationString")
    protected String citationString;
    @XmlElement(name = "GrantList")
    protected GrantListType grantList;

    /**
     * Gets the value of the pmid property.
     * 
     * @return
     *     possible object is
     *     {@link PMIDType }
     *     
     */
    public PMIDType getPMID() {
        return pmid;
    }

    /**
     * Sets the value of the pmid property.
     * 
     * @param value
     *     allowed object is
     *     {@link PMIDType }
     *     
     */
    public void setPMID(PMIDType value) {
        this.pmid = value;
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
     * Gets the value of the book property.
     * 
     * @return
     *     possible object is
     *     {@link BookType }
     *     
     */
    public BookType getBook() {
        return book;
    }

    /**
     * Sets the value of the book property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookType }
     *     
     */
    public void setBook(BookType value) {
        this.book = value;
    }

    /**
     * Gets the value of the locationLabel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the locationLabel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocationLabel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocationLabelType }
     * 
     * 
     */
    public List<LocationLabelType> getLocationLabel() {
        if (locationLabel == null) {
            locationLabel = new ArrayList<LocationLabelType>();
        }
        return this.locationLabel;
    }

    /**
     * Gets the value of the articleTitle property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleTitleType }
     *     
     */
    public ArticleTitleType getArticleTitle() {
        return articleTitle;
    }

    /**
     * Sets the value of the articleTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleTitleType }
     *     
     */
    public void setArticleTitle(ArticleTitleType value) {
        this.articleTitle = value;
    }

    /**
     * Gets the value of the vernacularTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVernacularTitle() {
        return vernacularTitle;
    }

    /**
     * Sets the value of the vernacularTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVernacularTitle(String value) {
        this.vernacularTitle = value;
    }

    /**
     * Gets the value of the pagination property.
     * 
     * @return
     *     possible object is
     *     {@link PaginationType }
     *     
     */
    public PaginationType getPagination() {
        return pagination;
    }

    /**
     * Sets the value of the pagination property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaginationType }
     *     
     */
    public void setPagination(PaginationType value) {
        this.pagination = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the language property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLanguage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLanguage() {
        if (language == null) {
            language = new ArrayList<String>();
        }
        return this.language;
    }

    /**
     * Gets the value of the authorList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authorList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthorList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AuthorListType }
     * 
     * 
     */
    public List<AuthorListType> getAuthorList() {
        if (authorList == null) {
            authorList = new ArrayList<AuthorListType>();
        }
        return this.authorList;
    }

    /**
     * Gets the value of the groupList property.
     * 
     * @return
     *     possible object is
     *     {@link GroupList }
     *     
     */
    public GroupList getGroupList() {
        return groupList;
    }

    /**
     * Sets the value of the groupList property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupList }
     *     
     */
    public void setGroupList(GroupList value) {
        this.groupList = value;
    }

    /**
     * Gets the value of the publicationType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the publicationType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPublicationType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPublicationType() {
        if (publicationType == null) {
            publicationType = new ArrayList<String>();
        }
        return this.publicationType;
    }

    /**
     * Gets the value of the abstract property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractType }
     *     
     */
    public AbstractType getAbstract() {
        return _abstract;
    }

    /**
     * Sets the value of the abstract property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractType }
     *     
     */
    public void setAbstract(AbstractType value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the sections property.
     * 
     * @return
     *     possible object is
     *     {@link SectionsType }
     *     
     */
    public SectionsType getSections() {
        return sections;
    }

    /**
     * Sets the value of the sections property.
     * 
     * @param value
     *     allowed object is
     *     {@link SectionsType }
     *     
     */
    public void setSections(SectionsType value) {
        this.sections = value;
    }

    /**
     * Gets the value of the keywordList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keywordList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeywordList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeywordListType }
     * 
     * 
     */
    public List<KeywordListType> getKeywordList() {
        if (keywordList == null) {
            keywordList = new ArrayList<KeywordListType>();
        }
        return this.keywordList;
    }

    /**
     * Gets the value of the contributionDate property.
     * 
     * @return
     *     possible object is
     *     {@link ContributionDateType }
     *     
     */
    public ContributionDateType getContributionDate() {
        return contributionDate;
    }

    /**
     * Sets the value of the contributionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContributionDateType }
     *     
     */
    public void setContributionDate(ContributionDateType value) {
        this.contributionDate = value;
    }

    /**
     * Gets the value of the dateRevised property.
     * 
     * @return
     *     possible object is
     *     {@link DateRevisedType }
     *     
     */
    public DateRevisedType getDateRevised() {
        return dateRevised;
    }

    /**
     * Sets the value of the dateRevised property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateRevisedType }
     *     
     */
    public void setDateRevised(DateRevisedType value) {
        this.dateRevised = value;
    }

    /**
     * Gets the value of the citationString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitationString() {
        return citationString;
    }

    /**
     * Sets the value of the citationString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitationString(String value) {
        this.citationString = value;
    }

    /**
     * Gets the value of the grantList property.
     * 
     * @return
     *     possible object is
     *     {@link GrantListType }
     *     
     */
    public GrantListType getGrantList() {
        return grantList;
    }

    /**
     * Sets the value of the grantList property.
     * 
     * @param value
     *     allowed object is
     *     {@link GrantListType }
     *     
     */
    public void setGrantList(GrantListType value) {
        this.grantList = value;
    }

}
