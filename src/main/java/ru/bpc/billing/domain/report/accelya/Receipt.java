
package ru.bpc.billing.domain.report.accelya;

import javax.xml.bind.annotation.*;


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
 *         &lt;element ref="{}statusProcess"/>
 *         &lt;element ref="{}itemsError"/>
 *       &lt;/sequence>
 *       &lt;attribute name="SourceFileName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "statusProcess",
    "itemsError"
})
@XmlRootElement(name = "Receipt")
public class Receipt {

    @XmlElement(required = true)
    protected String statusProcess;
    @XmlElement(required = true)
    protected ItemsError itemsError;
    @XmlAttribute(name = "SourceFileName")
    protected String sourceFileName;

    /**
     * Gets the value of the statusProcess property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusProcess() {
        return statusProcess;
    }

    /**
     * Sets the value of the statusProcess property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusProcess(String value) {
        this.statusProcess = value;
    }

    /**
     * Gets the value of the itemsError property.
     * 
     * @return
     *     possible object is
     *     {@link ItemsError }
     *     
     */
    public ItemsError getItemsError() {
        return itemsError;
    }

    /**
     * Sets the value of the itemsError property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemsError }
     *     
     */
    public void setItemsError(ItemsError value) {
        this.itemsError = value;
    }

    /**
     * Gets the value of the sourceFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceFileName() {
        return sourceFileName;
    }

    /**
     * Sets the value of the sourceFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceFileName(String value) {
        this.sourceFileName = value;
    }

}
