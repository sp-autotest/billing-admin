
package ru.bpc.billing.domain.report.accelya;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.bpc.phoenix.service.bo.revenue.accelya package. 
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

    private final static QName _Reason_QNAME = new QName("", "reason");
    private final static QName _TicketCurrency_QNAME = new QName("", "ticketCurrency");
    private final static QName _ExpiryDate_QNAME = new QName("", "expiryDate");
    private final static QName _TypeDocument_QNAME = new QName("", "typeDocument");
    private final static QName _TicketNumber_QNAME = new QName("", "ticketNumber");
    private final static QName _ApprovalCode_QNAME = new QName("", "approvalCode");
    private final static QName _TicketAmount_QNAME = new QName("", "ticketAmount");
    private final static QName _StatusProcess_QNAME = new QName("", "statusProcess");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.bpc.phoenix.service.bo.revenue.accelya
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ru.bpc.billing.domain.report.accelya.ItemsError }
     * 
     */
    public ItemsError createItemsError() {
        return new ItemsError();
    }

    /**
     * Create an instance of {@link ru.bpc.billing.domain.report.accelya.ItemError }
     * 
     */
    public ItemError createItemError() {
        return new ItemError();
    }

    /**
     * Create an instance of {@link ru.bpc.billing.domain.report.accelya.Receipt }
     * 
     */
    public Receipt createReceipt() {
        return new Receipt();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "reason")
    public JAXBElement<String> createReason(String value) {
        return new JAXBElement<String>(_Reason_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "ticketCurrency")
    public JAXBElement<String> createTicketCurrency(String value) {
        return new JAXBElement<String>(_TicketCurrency_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "expiryDate")
    public JAXBElement<String> createExpiryDate(String value) {
        return new JAXBElement<String>(_ExpiryDate_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "typeDocument")
    public JAXBElement<String> createTypeDocument(String value) {
        return new JAXBElement<String>(_TypeDocument_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "ticketNumber")
    public JAXBElement<String> createTicketNumber(String value) {
        return new JAXBElement<String>(_TicketNumber_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "approvalCode")
    public JAXBElement<String> createApprovalCode(String value) {
        return new JAXBElement<String>(_ApprovalCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "ticketAmount")
    public JAXBElement<String> createTicketAmount(String value) {
        return new JAXBElement<String>(_TicketAmount_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "statusProcess")
    public JAXBElement<String> createStatusProcess(String value) {
        return new JAXBElement<String>(_StatusProcess_QNAME, String.class, null, value);
    }

}
