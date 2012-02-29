
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for PubMedPubDateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PubMedPubDateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Year"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Month"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Day"/>
 *         &lt;sequence minOccurs="0">
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Hour"/>
 *           &lt;sequence minOccurs="0">
 *             &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Minute"/>
 *             &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Second" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="PubStatus" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="received"/>
 *             &lt;enumeration value="accepted"/>
 *             &lt;enumeration value="epublish"/>
 *             &lt;enumeration value="ppublish"/>
 *             &lt;enumeration value="revised"/>
 *             &lt;enumeration value="aheadofprint"/>
 *             &lt;enumeration value="retracted"/>
 *             &lt;enumeration value="pmc"/>
 *             &lt;enumeration value="pmcr"/>
 *             &lt;enumeration value="pubmed"/>
 *             &lt;enumeration value="pubmedr"/>
 *             &lt;enumeration value="premedline"/>
 *             &lt;enumeration value="medline"/>
 *             &lt;enumeration value="medliner"/>
 *             &lt;enumeration value="entrez"/>
 *             &lt;enumeration value="pmc-release"/>
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
@XmlType(name = "PubMedPubDateType", propOrder = {
    "year",
    "month",
    "day",
    "hour",
    "minute",
    "second"
})
public class PubMedPubDateType {

    @XmlElement(name = "Year", required = true)
    protected String year;
    @XmlElement(name = "Month", required = true)
    protected String month;
    @XmlElement(name = "Day", required = true)
    protected String day;
    @XmlElement(name = "Hour")
    protected String hour;
    @XmlElement(name = "Minute")
    protected String minute;
    @XmlElement(name = "Second")
    protected String second;
    @XmlAttribute(name = "PubStatus", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String pubStatus;

    /**
     * Gets the value of the year property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYear(String value) {
        this.year = value;
    }

    /**
     * Gets the value of the month property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonth() {
        return month;
    }

    /**
     * Sets the value of the month property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonth(String value) {
        this.month = value;
    }

    /**
     * Gets the value of the day property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDay() {
        return day;
    }

    /**
     * Sets the value of the day property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDay(String value) {
        this.day = value;
    }

    /**
     * Gets the value of the hour property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHour() {
        return hour;
    }

    /**
     * Sets the value of the hour property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHour(String value) {
        this.hour = value;
    }

    /**
     * Gets the value of the minute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinute() {
        return minute;
    }

    /**
     * Sets the value of the minute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinute(String value) {
        this.minute = value;
    }

    /**
     * Gets the value of the second property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecond() {
        return second;
    }

    /**
     * Sets the value of the second property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecond(String value) {
        this.second = value;
    }

    /**
     * Gets the value of the pubStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubStatus() {
        return pubStatus;
    }

    /**
     * Sets the value of the pubStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubStatus(String value) {
        this.pubStatus = value;
    }

}
