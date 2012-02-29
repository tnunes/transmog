
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BookType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BookType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Publisher" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PublisherType"/>
 *         &lt;element name="BookTitle" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}BookTitleType"/>
 *         &lt;element name="PubDate" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PubDateType"/>
 *         &lt;element name="BeginningDate" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}BeginningDateType" minOccurs="0"/>
 *         &lt;element name="EndingDate" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}EndingDateType" minOccurs="0"/>
 *         &lt;element name="AuthorList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}AuthorListType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Volume" minOccurs="0"/>
 *         &lt;element name="VolumeTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Edition" minOccurs="0"/>
 *         &lt;element name="CollectionTitle" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}CollectionTitleType" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Isbn" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ELocationID" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ELocationIDType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Medium" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ReportNumber" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookType", propOrder = {
    "publisher",
    "bookTitle",
    "pubDate",
    "beginningDate",
    "endingDate",
    "authorList",
    "volume",
    "volumeTitle",
    "edition",
    "collectionTitle",
    "isbn",
    "eLocationID",
    "medium",
    "reportNumber"
})
public class BookType {

    @XmlElement(name = "Publisher", required = true)
    protected PublisherType publisher;
    @XmlElement(name = "BookTitle", required = true)
    protected BookTitleType bookTitle;
    @XmlElement(name = "PubDate", required = true)
    protected PubDateType pubDate;
    @XmlElement(name = "BeginningDate")
    protected BeginningDateType beginningDate;
    @XmlElement(name = "EndingDate")
    protected EndingDateType endingDate;
    @XmlElement(name = "AuthorList")
    protected List<AuthorListType> authorList;
    @XmlElement(name = "Volume")
    protected String volume;
    @XmlElement(name = "VolumeTitle")
    protected String volumeTitle;
    @XmlElement(name = "Edition")
    protected String edition;
    @XmlElement(name = "CollectionTitle")
    protected CollectionTitleType collectionTitle;
    @XmlElement(name = "Isbn")
    protected List<String> isbn;
    @XmlElement(name = "ELocationID")
    protected List<ELocationIDType> eLocationID;
    @XmlElement(name = "Medium")
    protected String medium;
    @XmlElement(name = "ReportNumber")
    protected String reportNumber;

    /**
     * Gets the value of the publisher property.
     * 
     * @return
     *     possible object is
     *     {@link PublisherType }
     *     
     */
    public PublisherType getPublisher() {
        return publisher;
    }

    /**
     * Sets the value of the publisher property.
     * 
     * @param value
     *     allowed object is
     *     {@link PublisherType }
     *     
     */
    public void setPublisher(PublisherType value) {
        this.publisher = value;
    }

    /**
     * Gets the value of the bookTitle property.
     * 
     * @return
     *     possible object is
     *     {@link BookTitleType }
     *     
     */
    public BookTitleType getBookTitle() {
        return bookTitle;
    }

    /**
     * Sets the value of the bookTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookTitleType }
     *     
     */
    public void setBookTitle(BookTitleType value) {
        this.bookTitle = value;
    }

    /**
     * Gets the value of the pubDate property.
     * 
     * @return
     *     possible object is
     *     {@link PubDateType }
     *     
     */
    public PubDateType getPubDate() {
        return pubDate;
    }

    /**
     * Sets the value of the pubDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link PubDateType }
     *     
     */
    public void setPubDate(PubDateType value) {
        this.pubDate = value;
    }

    /**
     * Gets the value of the beginningDate property.
     * 
     * @return
     *     possible object is
     *     {@link BeginningDateType }
     *     
     */
    public BeginningDateType getBeginningDate() {
        return beginningDate;
    }

    /**
     * Sets the value of the beginningDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BeginningDateType }
     *     
     */
    public void setBeginningDate(BeginningDateType value) {
        this.beginningDate = value;
    }

    /**
     * Gets the value of the endingDate property.
     * 
     * @return
     *     possible object is
     *     {@link EndingDateType }
     *     
     */
    public EndingDateType getEndingDate() {
        return endingDate;
    }

    /**
     * Sets the value of the endingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link EndingDateType }
     *     
     */
    public void setEndingDate(EndingDateType value) {
        this.endingDate = value;
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
     * Gets the value of the volume property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVolume() {
        return volume;
    }

    /**
     * Sets the value of the volume property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVolume(String value) {
        this.volume = value;
    }

    /**
     * Gets the value of the volumeTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVolumeTitle() {
        return volumeTitle;
    }

    /**
     * Sets the value of the volumeTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVolumeTitle(String value) {
        this.volumeTitle = value;
    }

    /**
     * Gets the value of the edition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEdition() {
        return edition;
    }

    /**
     * Sets the value of the edition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEdition(String value) {
        this.edition = value;
    }

    /**
     * Gets the value of the collectionTitle property.
     * 
     * @return
     *     possible object is
     *     {@link CollectionTitleType }
     *     
     */
    public CollectionTitleType getCollectionTitle() {
        return collectionTitle;
    }

    /**
     * Sets the value of the collectionTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link CollectionTitleType }
     *     
     */
    public void setCollectionTitle(CollectionTitleType value) {
        this.collectionTitle = value;
    }

    /**
     * Gets the value of the isbn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the isbn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIsbn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIsbn() {
        if (isbn == null) {
            isbn = new ArrayList<String>();
        }
        return this.isbn;
    }

    /**
     * Gets the value of the eLocationID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eLocationID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getELocationID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ELocationIDType }
     * 
     * 
     */
    public List<ELocationIDType> getELocationID() {
        if (eLocationID == null) {
            eLocationID = new ArrayList<ELocationIDType>();
        }
        return this.eLocationID;
    }

    /**
     * Gets the value of the medium property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedium() {
        return medium;
    }

    /**
     * Sets the value of the medium property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedium(String value) {
        this.medium = value;
    }

    /**
     * Gets the value of the reportNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportNumber() {
        return reportNumber;
    }

    /**
     * Sets the value of the reportNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportNumber(String value) {
        this.reportNumber = value;
    }

}
