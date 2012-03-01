
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PubDateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PubDateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Year"/>
 *           &lt;choice minOccurs="0">
 *             &lt;sequence>
 *               &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Month"/>
 *               &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Day" minOccurs="0"/>
 *             &lt;/sequence>
 *             &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}Season"/>
 *           &lt;/choice>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_pubmed}MedlineDate"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PubDateType", propOrder = {
    "year",
    "month",
    "day",
    "season",
    "medlineDate"
})
public class PubDateType {

    @XmlElement(name = "Year")
    protected String year;
    @XmlElement(name = "Month")
    protected String month;
    @XmlElement(name = "Day")
    protected String day;
    @XmlElement(name = "Season")
    protected String season;
    @XmlElement(name = "MedlineDate")
    protected String medlineDate;

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
     * Gets the value of the season property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeason() {
        return season;
    }

    /**
     * Sets the value of the season property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeason(String value) {
        this.season = value;
    }

    /**
     * Gets the value of the medlineDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedlineDate() {
        return medlineDate;
    }

    /**
     * Sets the value of the medlineDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedlineDate(String value) {
        this.medlineDate = value;
    }

}