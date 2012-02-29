//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.02.28 at 06:51:56 PM CET 
//


package gov.nih.nlm.ncbi.eutils.generated;

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
 * generated in the gov.nih.nlm.ncbi.eutils.generated package. 
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

    private final static QName _Field_QNAME = new QName("", "Field");
    private final static QName _Count_QNAME = new QName("", "Count");
    private final static QName _RetStart_QNAME = new QName("", "RetStart");
    private final static QName _RetMax_QNAME = new QName("", "RetMax");
    private final static QName _Explode_QNAME = new QName("", "Explode");
    private final static QName _To_QNAME = new QName("", "To");
    private final static QName _Id_QNAME = new QName("", "Id");
    private final static QName _QueryTranslation_QNAME = new QName("", "QueryTranslation");
    private final static QName _OP_QNAME = new QName("", "OP");
    private final static QName _From_QNAME = new QName("", "From");
    private final static QName _Term_QNAME = new QName("", "Term");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: gov.nih.nlm.ncbi.eutils.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ESearchResult }
     * 
     */
    public ESearchResult createESearchResult() {
        return new ESearchResult();
    }

    /**
     * Create an instance of {@link TranslationSet }
     * 
     */
    public TranslationSet createTranslationSet() {
        return new TranslationSet();
    }

    /**
     * Create an instance of {@link IdList }
     * 
     */
    public IdList createIdList() {
        return new IdList();
    }

    /**
     * Create an instance of {@link TermSet }
     * 
     */
    public TermSet createTermSet() {
        return new TermSet();
    }

    /**
     * Create an instance of {@link Translation }
     * 
     */
    public Translation createTranslation() {
        return new Translation();
    }

    /**
     * Create an instance of {@link TranslationStack }
     * 
     */
    public TranslationStack createTranslationStack() {
        return new TranslationStack();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Field")
    public JAXBElement<String> createField(String value) {
        return new JAXBElement<String>(_Field_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Count")
    public JAXBElement<BigInteger> createCount(BigInteger value) {
        return new JAXBElement<BigInteger>(_Count_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "RetStart")
    public JAXBElement<BigInteger> createRetStart(BigInteger value) {
        return new JAXBElement<BigInteger>(_RetStart_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "RetMax")
    public JAXBElement<BigInteger> createRetMax(BigInteger value) {
        return new JAXBElement<BigInteger>(_RetMax_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Explode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createExplode(String value) {
        return new JAXBElement<String>(_Explode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "To")
    public JAXBElement<String> createTo(String value) {
        return new JAXBElement<String>(_To_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Id")
    public JAXBElement<BigInteger> createId(BigInteger value) {
        return new JAXBElement<BigInteger>(_Id_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "QueryTranslation")
    public JAXBElement<String> createQueryTranslation(String value) {
        return new JAXBElement<String>(_QueryTranslation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "OP")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createOP(String value) {
        return new JAXBElement<String>(_OP_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "From")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createFrom(String value) {
        return new JAXBElement<String>(_From_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Term")
    public JAXBElement<String> createTerm(String value) {
        return new JAXBElement<String>(_Term_QNAME, String.class, null, value);
    }

}
