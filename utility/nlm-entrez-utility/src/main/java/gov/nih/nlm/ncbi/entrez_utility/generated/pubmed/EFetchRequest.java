
package gov.nih.nlm.ncbi.entrez_utility.generated.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WebEnv" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="query_key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tool" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="retstart" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="retmax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rettype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "id",
    "webEnv",
    "queryKey",
    "tool",
    "email",
    "retstart",
    "retmax",
    "rettype"
})
@XmlRootElement(name = "eFetchRequest")
public class EFetchRequest {

    protected String id;
    @XmlElement(name = "WebEnv")
    protected String webEnv;
    @XmlElement(name = "query_key")
    protected String queryKey;
    protected String tool;
    protected String email;
    protected String retstart;
    protected String retmax;
    protected String rettype;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the webEnv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWebEnv() {
        return webEnv;
    }

    /**
     * Sets the value of the webEnv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWebEnv(String value) {
        this.webEnv = value;
    }

    /**
     * Gets the value of the queryKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryKey() {
        return queryKey;
    }

    /**
     * Sets the value of the queryKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryKey(String value) {
        this.queryKey = value;
    }

    /**
     * Gets the value of the tool property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTool() {
        return tool;
    }

    /**
     * Sets the value of the tool property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTool(String value) {
        this.tool = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the retstart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRetstart() {
        return retstart;
    }

    /**
     * Sets the value of the retstart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRetstart(String value) {
        this.retstart = value;
    }

    /**
     * Gets the value of the retmax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRetmax() {
        return retmax;
    }

    /**
     * Sets the value of the retmax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRetmax(String value) {
        this.retmax = value;
    }

    /**
     * Gets the value of the rettype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRettype() {
        return rettype;
    }

    /**
     * Sets the value of the rettype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRettype(String value) {
        this.rettype = value;
    }

}
