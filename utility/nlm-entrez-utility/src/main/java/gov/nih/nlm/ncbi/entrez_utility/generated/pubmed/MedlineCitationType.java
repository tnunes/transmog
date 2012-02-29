
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for MedlineCitationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MedlineCitationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PMID" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PMIDType"/>
 *         &lt;element name="DateCreated" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}DateCreatedType"/>
 *         &lt;element name="DateCompleted" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}DateCompletedType" minOccurs="0"/>
 *         &lt;element name="DateRevised" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}DateRevisedType" minOccurs="0"/>
 *         &lt;element name="Article" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ArticleType"/>
 *         &lt;element name="MedlineJournalInfo" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}MedlineJournalInfoType"/>
 *         &lt;element name="ChemicalList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}ChemicalListType" minOccurs="0"/>
 *         &lt;element name="SupplMeshList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}SupplMeshListType" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}CitationSubset" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="CommentsCorrectionsList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}CommentsCorrectionsListType" minOccurs="0"/>
 *         &lt;element name="GeneSymbolList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}GeneSymbolListType" minOccurs="0"/>
 *         &lt;element name="MeshHeadingList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}MeshHeadingListType" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}NumberOfReferences" minOccurs="0"/>
 *         &lt;element name="PersonalNameSubjectList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}PersonalNameSubjectListType" minOccurs="0"/>
 *         &lt;element name="OtherID" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}OtherIDType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="OtherAbstract" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}OtherAbstractType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="KeywordList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}KeywordListType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}SpaceFlightMission" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="InvestigatorList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}InvestigatorListType" minOccurs="0"/>
 *         &lt;element name="GeneralNote" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}GeneralNoteType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Owner" default="NLM">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="NLM"/>
 *             &lt;enumeration value="NASA"/>
 *             &lt;enumeration value="PIP"/>
 *             &lt;enumeration value="KIE"/>
 *             &lt;enumeration value="HSR"/>
 *             &lt;enumeration value="HMD"/>
 *             &lt;enumeration value="NOTNLM"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="Status" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="Completed"/>
 *             &lt;enumeration value="In-Process"/>
 *             &lt;enumeration value="PubMed-not-MEDLINE"/>
 *             &lt;enumeration value="In-Data-Review"/>
 *             &lt;enumeration value="Publisher"/>
 *             &lt;enumeration value="MEDLINE"/>
 *             &lt;enumeration value="OLDMEDLINE"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="VersionID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="VersionDate" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MedlineCitationType", propOrder = {
    "pmid",
    "dateCreated",
    "dateCompleted",
    "dateRevised",
    "article",
    "medlineJournalInfo",
    "chemicalList",
    "supplMeshList",
    "citationSubset",
    "commentsCorrectionsList",
    "geneSymbolList",
    "meshHeadingList",
    "numberOfReferences",
    "personalNameSubjectList",
    "otherID",
    "otherAbstract",
    "keywordList",
    "spaceFlightMission",
    "investigatorList",
    "generalNote"
})
public class MedlineCitationType {

    @XmlElement(name = "PMID", required = true)
    protected PMIDType pmid;
    @XmlElement(name = "DateCreated", required = true)
    protected DateCreatedType dateCreated;
    @XmlElement(name = "DateCompleted")
    protected DateCompletedType dateCompleted;
    @XmlElement(name = "DateRevised")
    protected DateRevisedType dateRevised;
    @XmlElement(name = "Article", required = true)
    protected ArticleType article;
    @XmlElement(name = "MedlineJournalInfo", required = true)
    protected MedlineJournalInfoType medlineJournalInfo;
    @XmlElement(name = "ChemicalList")
    protected ChemicalListType chemicalList;
    @XmlElement(name = "SupplMeshList")
    protected SupplMeshListType supplMeshList;
    @XmlElement(name = "CitationSubset")
    protected List<String> citationSubset;
    @XmlElement(name = "CommentsCorrectionsList")
    protected CommentsCorrectionsListType commentsCorrectionsList;
    @XmlElement(name = "GeneSymbolList")
    protected GeneSymbolListType geneSymbolList;
    @XmlElement(name = "MeshHeadingList")
    protected MeshHeadingListType meshHeadingList;
    @XmlElement(name = "NumberOfReferences")
    protected String numberOfReferences;
    @XmlElement(name = "PersonalNameSubjectList")
    protected PersonalNameSubjectListType personalNameSubjectList;
    @XmlElement(name = "OtherID")
    protected List<OtherIDType> otherID;
    @XmlElement(name = "OtherAbstract")
    protected List<OtherAbstractType> otherAbstract;
    @XmlElement(name = "KeywordList")
    protected List<KeywordListType> keywordList;
    @XmlElement(name = "SpaceFlightMission")
    protected List<String> spaceFlightMission;
    @XmlElement(name = "InvestigatorList")
    protected InvestigatorListType investigatorList;
    @XmlElement(name = "GeneralNote")
    protected List<GeneralNoteType> generalNote;
    @XmlAttribute(name = "Owner")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String owner;
    @XmlAttribute(name = "Status", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String status;
    @XmlAttribute(name = "VersionID")
    protected String versionID;
    @XmlAttribute(name = "VersionDate")
    protected String versionDate;

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
     * Gets the value of the dateCreated property.
     * 
     * @return
     *     possible object is
     *     {@link DateCreatedType }
     *     
     */
    public DateCreatedType getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the value of the dateCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateCreatedType }
     *     
     */
    public void setDateCreated(DateCreatedType value) {
        this.dateCreated = value;
    }

    /**
     * Gets the value of the dateCompleted property.
     * 
     * @return
     *     possible object is
     *     {@link DateCompletedType }
     *     
     */
    public DateCompletedType getDateCompleted() {
        return dateCompleted;
    }

    /**
     * Sets the value of the dateCompleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateCompletedType }
     *     
     */
    public void setDateCompleted(DateCompletedType value) {
        this.dateCompleted = value;
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
     * Gets the value of the article property.
     * 
     * @return
     *     possible object is
     *     {@link ArticleType }
     *     
     */
    public ArticleType getArticle() {
        return article;
    }

    /**
     * Sets the value of the article property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArticleType }
     *     
     */
    public void setArticle(ArticleType value) {
        this.article = value;
    }

    /**
     * Gets the value of the medlineJournalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link MedlineJournalInfoType }
     *     
     */
    public MedlineJournalInfoType getMedlineJournalInfo() {
        return medlineJournalInfo;
    }

    /**
     * Sets the value of the medlineJournalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link MedlineJournalInfoType }
     *     
     */
    public void setMedlineJournalInfo(MedlineJournalInfoType value) {
        this.medlineJournalInfo = value;
    }

    /**
     * Gets the value of the chemicalList property.
     * 
     * @return
     *     possible object is
     *     {@link ChemicalListType }
     *     
     */
    public ChemicalListType getChemicalList() {
        return chemicalList;
    }

    /**
     * Sets the value of the chemicalList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChemicalListType }
     *     
     */
    public void setChemicalList(ChemicalListType value) {
        this.chemicalList = value;
    }

    /**
     * Gets the value of the supplMeshList property.
     * 
     * @return
     *     possible object is
     *     {@link SupplMeshListType }
     *     
     */
    public SupplMeshListType getSupplMeshList() {
        return supplMeshList;
    }

    /**
     * Sets the value of the supplMeshList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupplMeshListType }
     *     
     */
    public void setSupplMeshList(SupplMeshListType value) {
        this.supplMeshList = value;
    }

    /**
     * Gets the value of the citationSubset property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the citationSubset property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCitationSubset().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCitationSubset() {
        if (citationSubset == null) {
            citationSubset = new ArrayList<String>();
        }
        return this.citationSubset;
    }

    /**
     * Gets the value of the commentsCorrectionsList property.
     * 
     * @return
     *     possible object is
     *     {@link CommentsCorrectionsListType }
     *     
     */
    public CommentsCorrectionsListType getCommentsCorrectionsList() {
        return commentsCorrectionsList;
    }

    /**
     * Sets the value of the commentsCorrectionsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommentsCorrectionsListType }
     *     
     */
    public void setCommentsCorrectionsList(CommentsCorrectionsListType value) {
        this.commentsCorrectionsList = value;
    }

    /**
     * Gets the value of the geneSymbolList property.
     * 
     * @return
     *     possible object is
     *     {@link GeneSymbolListType }
     *     
     */
    public GeneSymbolListType getGeneSymbolList() {
        return geneSymbolList;
    }

    /**
     * Sets the value of the geneSymbolList property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneSymbolListType }
     *     
     */
    public void setGeneSymbolList(GeneSymbolListType value) {
        this.geneSymbolList = value;
    }

    /**
     * Gets the value of the meshHeadingList property.
     * 
     * @return
     *     possible object is
     *     {@link MeshHeadingListType }
     *     
     */
    public MeshHeadingListType getMeshHeadingList() {
        return meshHeadingList;
    }

    /**
     * Sets the value of the meshHeadingList property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeshHeadingListType }
     *     
     */
    public void setMeshHeadingList(MeshHeadingListType value) {
        this.meshHeadingList = value;
    }

    /**
     * Gets the value of the numberOfReferences property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberOfReferences() {
        return numberOfReferences;
    }

    /**
     * Sets the value of the numberOfReferences property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberOfReferences(String value) {
        this.numberOfReferences = value;
    }

    /**
     * Gets the value of the personalNameSubjectList property.
     * 
     * @return
     *     possible object is
     *     {@link PersonalNameSubjectListType }
     *     
     */
    public PersonalNameSubjectListType getPersonalNameSubjectList() {
        return personalNameSubjectList;
    }

    /**
     * Sets the value of the personalNameSubjectList property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonalNameSubjectListType }
     *     
     */
    public void setPersonalNameSubjectList(PersonalNameSubjectListType value) {
        this.personalNameSubjectList = value;
    }

    /**
     * Gets the value of the otherID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OtherIDType }
     * 
     * 
     */
    public List<OtherIDType> getOtherID() {
        if (otherID == null) {
            otherID = new ArrayList<OtherIDType>();
        }
        return this.otherID;
    }

    /**
     * Gets the value of the otherAbstract property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherAbstract property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherAbstract().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OtherAbstractType }
     * 
     * 
     */
    public List<OtherAbstractType> getOtherAbstract() {
        if (otherAbstract == null) {
            otherAbstract = new ArrayList<OtherAbstractType>();
        }
        return this.otherAbstract;
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
     * Gets the value of the spaceFlightMission property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spaceFlightMission property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpaceFlightMission().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSpaceFlightMission() {
        if (spaceFlightMission == null) {
            spaceFlightMission = new ArrayList<String>();
        }
        return this.spaceFlightMission;
    }

    /**
     * Gets the value of the investigatorList property.
     * 
     * @return
     *     possible object is
     *     {@link InvestigatorListType }
     *     
     */
    public InvestigatorListType getInvestigatorList() {
        return investigatorList;
    }

    /**
     * Sets the value of the investigatorList property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvestigatorListType }
     *     
     */
    public void setInvestigatorList(InvestigatorListType value) {
        this.investigatorList = value;
    }

    /**
     * Gets the value of the generalNote property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the generalNote property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGeneralNote().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GeneralNoteType }
     * 
     * 
     */
    public List<GeneralNoteType> getGeneralNote() {
        if (generalNote == null) {
            generalNote = new ArrayList<GeneralNoteType>();
        }
        return this.generalNote;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwner() {
        if (owner == null) {
            return "NLM";
        } else {
            return owner;
        }
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwner(String value) {
        this.owner = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the versionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersionID() {
        return versionID;
    }

    /**
     * Sets the value of the versionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersionID(String value) {
        this.versionID = value;
    }

    /**
     * Gets the value of the versionDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersionDate() {
        return versionDate;
    }

    /**
     * Sets the value of the versionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersionDate(String value) {
        this.versionDate = value;
    }

}
