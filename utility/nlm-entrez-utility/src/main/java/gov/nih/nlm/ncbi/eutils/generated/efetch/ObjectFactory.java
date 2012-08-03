//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.03 at 04:22:15 PM CEST 
//


package gov.nih.nlm.ncbi.eutils.generated.efetch;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the gov.nih.nlm.ncbi.eutils.generated.efetch package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _NlmUniqueID_QNAME = new QName("", "NlmUniqueID");
    private final static QName _PublicationStatus_QNAME = new QName("", "PublicationStatus");
    private final static QName _CitationSubset_QNAME = new QName("", "CitationSubset");
    private final static QName _Volume_QNAME = new QName("", "Volume");
    private final static QName _ForeName_QNAME = new QName("", "ForeName");
    private final static QName _ArticleTitle_QNAME = new QName("", "ArticleTitle");
    private final static QName _Country_QNAME = new QName("", "Country");
    private final static QName _LastName_QNAME = new QName("", "LastName");
    private final static QName _Day_QNAME = new QName("", "Day");
    private final static QName _Month_QNAME = new QName("", "Month");
    private final static QName _Title_QNAME = new QName("", "Title");
    private final static QName _Affiliation_QNAME = new QName("", "Affiliation");
    private final static QName _ISSNLinking_QNAME = new QName("", "ISSNLinking");
    private final static QName _Year_QNAME = new QName("", "Year");
    private final static QName _Issue_QNAME = new QName("", "Issue");
    private final static QName _Hour_QNAME = new QName("", "Hour");
    private final static QName _CopyrightInformation_QNAME = new QName("", "CopyrightInformation");
    private final static QName _Initials_QNAME = new QName("", "Initials");
    private final static QName _Minute_QNAME = new QName("", "Minute");
    private final static QName _MedlineTA_QNAME = new QName("", "MedlineTA");
    private final static QName _ISOAbbreviation_QNAME = new QName("", "ISOAbbreviation");
    private final static QName _PublicationType_QNAME = new QName("", "PublicationType");
    private final static QName _Language_QNAME = new QName("", "Language");
    private final static QName _MedlinePgn_QNAME = new QName("", "MedlinePgn");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: gov.nih.nlm.ncbi.eutils.generated.efetch
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ArticleIdList }
     * 
     */
    public ArticleIdList createArticleIdList() {
        return new ArticleIdList();
    }

    /**
     * Create an instance of {@link MedlineJournalInfo }
     * 
     */
    public MedlineJournalInfo createMedlineJournalInfo() {
        return new MedlineJournalInfo();
    }

    /**
     * Create an instance of {@link Author }
     * 
     */
    public Author createAuthor() {
        return new Author();
    }

    /**
     * Create an instance of {@link PubmedArticle }
     * 
     */
    public PubmedArticle createPubmedArticle() {
        return new PubmedArticle();
    }

    /**
     * Create an instance of {@link DateCreated }
     * 
     */
    public DateCreated createDateCreated() {
        return new DateCreated();
    }

    /**
     * Create an instance of {@link Journal }
     * 
     */
    public Journal createJournal() {
        return new Journal();
    }

    /**
     * Create an instance of {@link ArticleId }
     * 
     */
    public ArticleId createArticleId() {
        return new ArticleId();
    }

    /**
     * Create an instance of {@link PubDate }
     * 
     */
    public PubDate createPubDate() {
        return new PubDate();
    }

    /**
     * Create an instance of {@link PublicationTypeList }
     * 
     */
    public PublicationTypeList createPublicationTypeList() {
        return new PublicationTypeList();
    }

    /**
     * Create an instance of {@link PubmedArticleSet }
     * 
     */
    public PubmedArticleSet createPubmedArticleSet() {
        return new PubmedArticleSet();
    }

    /**
     * Create an instance of {@link AbstractText }
     * 
     */
    public AbstractText createAbstractText() {
        return new AbstractText();
    }

    /**
     * Create an instance of {@link PubMedPubDate }
     * 
     */
    public PubMedPubDate createPubMedPubDate() {
        return new PubMedPubDate();
    }

    /**
     * Create an instance of {@link JournalIssue }
     * 
     */
    public JournalIssue createJournalIssue() {
        return new JournalIssue();
    }

    /**
     * Create an instance of {@link PMID }
     * 
     */
    public PMID createPMID() {
        return new PMID();
    }

    /**
     * Create an instance of {@link PubmedData }
     * 
     */
    public PubmedData createPubmedData() {
        return new PubmedData();
    }

    /**
     * Create an instance of {@link Abstract }
     * 
     */
    public Abstract createAbstract() {
        return new Abstract();
    }

    /**
     * Create an instance of {@link History }
     * 
     */
    public History createHistory() {
        return new History();
    }

    /**
     * Create an instance of {@link ArticleDate }
     * 
     */
    public ArticleDate createArticleDate() {
        return new ArticleDate();
    }

    /**
     * Create an instance of {@link Article }
     * 
     */
    public Article createArticle() {
        return new Article();
    }

    /**
     * Create an instance of {@link AuthorList }
     * 
     */
    public AuthorList createAuthorList() {
        return new AuthorList();
    }

    /**
     * Create an instance of {@link ISSN }
     * 
     */
    public ISSN createISSN() {
        return new ISSN();
    }

    /**
     * Create an instance of {@link Pagination }
     * 
     */
    public Pagination createPagination() {
        return new Pagination();
    }

    /**
     * Create an instance of {@link MedlineCitation }
     * 
     */
    public MedlineCitation createMedlineCitation() {
        return new MedlineCitation();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NlmUniqueID")
    public JAXBElement<BigInteger> createNlmUniqueID(BigInteger value) {
        return new JAXBElement<BigInteger>(_NlmUniqueID_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "PublicationStatus")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPublicationStatus(String value) {
        return new JAXBElement<String>(_PublicationStatus_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CitationSubset")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createCitationSubset(String value) {
        return new JAXBElement<String>(_CitationSubset_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Volume")
    public JAXBElement<BigInteger> createVolume(BigInteger value) {
        return new JAXBElement<BigInteger>(_Volume_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ForeName")
    public JAXBElement<String> createForeName(String value) {
        return new JAXBElement<String>(_ForeName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ArticleTitle")
    public JAXBElement<String> createArticleTitle(String value) {
        return new JAXBElement<String>(_ArticleTitle_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Country")
    public JAXBElement<String> createCountry(String value) {
        return new JAXBElement<String>(_Country_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "LastName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLastName(String value) {
        return new JAXBElement<String>(_LastName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Day")
    public JAXBElement<BigInteger> createDay(BigInteger value) {
        return new JAXBElement<BigInteger>(_Day_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Month")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createMonth(String value) {
        return new JAXBElement<String>(_Month_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Title")
    public JAXBElement<String> createTitle(String value) {
        return new JAXBElement<String>(_Title_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Affiliation")
    public JAXBElement<String> createAffiliation(String value) {
        return new JAXBElement<String>(_Affiliation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ISSNLinking")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createISSNLinking(String value) {
        return new JAXBElement<String>(_ISSNLinking_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Year")
    public JAXBElement<BigInteger> createYear(BigInteger value) {
        return new JAXBElement<BigInteger>(_Year_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Issue")
    public JAXBElement<BigInteger> createIssue(BigInteger value) {
        return new JAXBElement<BigInteger>(_Issue_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Hour")
    public JAXBElement<BigInteger> createHour(BigInteger value) {
        return new JAXBElement<BigInteger>(_Hour_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CopyrightInformation")
    public JAXBElement<String> createCopyrightInformation(String value) {
        return new JAXBElement<String>(_CopyrightInformation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Initials")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createInitials(String value) {
        return new JAXBElement<String>(_Initials_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Minute")
    public JAXBElement<BigInteger> createMinute(BigInteger value) {
        return new JAXBElement<BigInteger>(_Minute_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "MedlineTA")
    public JAXBElement<String> createMedlineTA(String value) {
        return new JAXBElement<String>(_MedlineTA_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ISOAbbreviation")
    public JAXBElement<String> createISOAbbreviation(String value) {
        return new JAXBElement<String>(_ISOAbbreviation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "PublicationType")
    public JAXBElement<String> createPublicationType(String value) {
        return new JAXBElement<String>(_PublicationType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Language")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLanguage(String value) {
        return new JAXBElement<String>(_Language_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "MedlinePgn")
    public JAXBElement<String> createMedlinePgn(String value) {
        return new JAXBElement<String>(_MedlinePgn_QNAME, String.class, null, value);
    }

}
