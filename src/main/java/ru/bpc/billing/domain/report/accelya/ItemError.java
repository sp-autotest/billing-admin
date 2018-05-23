
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
 *         &lt;element ref="{}ticketNumber"/>
 *         &lt;element ref="{}ticketAmount"/>
 *         &lt;element ref="{}ticketCurrency"/>
 *         &lt;element ref="{}expiryDate"/>
 *         &lt;element ref="{}approvalCode"/>
 *         &lt;element ref="{}typeDocument"/>
 *         &lt;element ref="{}reason"/>
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
    "ticketNumber",
    "ticketAmount",
    "ticketCurrency",
    "expiryDate",
    "approvalCode",
    "typeDocument",
    "reason"
})
@XmlRootElement(name = "itemError")
public class ItemError {

    @XmlElement(required = true)
    protected String ticketNumber;
    @XmlElement(required = true)
    protected String ticketAmount;
    @XmlElement(required = true)
    protected String ticketCurrency;
    @XmlElement(required = true)
    protected String expiryDate;
    @XmlElement(required = true)
    protected String approvalCode;
    @XmlElement(required = true)
    protected String typeDocument;
    @XmlElement(required = true)
    protected String reason;

    /**
     * Gets the value of the ticketNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTicketNumber() {
        return ticketNumber;
    }

    /**
     * Sets the value of the ticketNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTicketNumber(String value) {
        this.ticketNumber = value;
    }

    /**
     * Gets the value of the ticketAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTicketAmount() {
        return ticketAmount;
    }

    /**
     * Sets the value of the ticketAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTicketAmount(String value) {
        this.ticketAmount = value;
    }

    /**
     * Gets the value of the ticketCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTicketCurrency() {
        return ticketCurrency;
    }

    /**
     * Sets the value of the ticketCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTicketCurrency(String value) {
        this.ticketCurrency = value;
    }

    /**
     * Gets the value of the expiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpiryDate(String value) {
        this.expiryDate = value;
    }

    /**
     * Gets the value of the approvalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApprovalCode() {
        return approvalCode;
    }

    /**
     * Sets the value of the approvalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApprovalCode(String value) {
        this.approvalCode = value;
    }

    /**
     * Gets the value of the typeDocument property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeDocument() {
        return typeDocument;
    }

    /**
     * Sets the value of the typeDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeDocument(String value) {
        this.typeDocument = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

}
