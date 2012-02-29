
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="lang">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="AF"/>
 *             &lt;enumeration value="AR"/>
 *             &lt;enumeration value="AZ"/>
 *             &lt;enumeration value="BG"/>
 *             &lt;enumeration value="CS"/>
 *             &lt;enumeration value="DA"/>
 *             &lt;enumeration value="DE"/>
 *             &lt;enumeration value="EN"/>
 *             &lt;enumeration value="EL"/>
 *             &lt;enumeration value="ES"/>
 *             &lt;enumeration value="FA"/>
 *             &lt;enumeration value="FI"/>
 *             &lt;enumeration value="FR"/>
 *             &lt;enumeration value="HE"/>
 *             &lt;enumeration value="HU"/>
 *             &lt;enumeration value="HY"/>
 *             &lt;enumeration value="IN"/>
 *             &lt;enumeration value="IS"/>
 *             &lt;enumeration value="IT"/>
 *             &lt;enumeration value="IW"/>
 *             &lt;enumeration value="JA"/>
 *             &lt;enumeration value="KA"/>
 *             &lt;enumeration value="KO"/>
 *             &lt;enumeration value="LT"/>
 *             &lt;enumeration value="MK"/>
 *             &lt;enumeration value="ML"/>
 *             &lt;enumeration value="NL"/>
 *             &lt;enumeration value="NO"/>
 *             &lt;enumeration value="PL"/>
 *             &lt;enumeration value="PT"/>
 *             &lt;enumeration value="PS"/>
 *             &lt;enumeration value="RO"/>
 *             &lt;enumeration value="RU"/>
 *             &lt;enumeration value="SL"/>
 *             &lt;enumeration value="SK"/>
 *             &lt;enumeration value="SQ"/>
 *             &lt;enumeration value="SR"/>
 *             &lt;enumeration value="SV"/>
 *             &lt;enumeration value="SW"/>
 *             &lt;enumeration value="TH"/>
 *             &lt;enumeration value="TR"/>
 *             &lt;enumeration value="UK"/>
 *             &lt;enumeration value="VI"/>
 *             &lt;enumeration value="ZH"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="Type">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="FullText"/>
 *             &lt;enumeration value="Summary"/>
 *             &lt;enumeration value="fulltext"/>
 *             &lt;enumeration value="summary"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "URL")
public class URL {

    @XmlValue
    protected String value;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String lang;
    @XmlAttribute(name = "Type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the lang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
