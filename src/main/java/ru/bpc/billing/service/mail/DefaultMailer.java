package ru.bpc.billing.service.mail;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Service for sending emails
 */
public class DefaultMailer implements Mailer {
    private static final Logger logger = LoggerFactory.getLogger(DefaultMailer.class);

    private static final String DEFAULT_ENCODING = "UTF-8";

    private Session mailSession;
    private String defaultSender;
    private String debugAddress;
    private boolean debugMode = false;


    public enum Status {
        SUCCESS(0),
        INVALID_ADDRESS(1),
        PREPARATION_ERROR(2),
        SENDING_FAILED(50),
        GENERAL_ERROR(100);

        private int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public DefaultMailer(String host, int port, String login, String password, String auth, String protocol) {
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", auth);
        props.setProperty("mail.transport.protocol", protocol);
        props.setProperty("mail.host", host);
        props.setProperty("mail.smtp.port", Integer.toString(port));
        props.setProperty("mail.smtp.starttls.enable", "true");
        if (login != null)
            props.setProperty("mail.user", login);
        if (password != null)
            props.setProperty("mail.password", password);
        if ("true".equals(auth)) {
            Authenticator authenticator = new Authenticator(login, password);
            mailSession = Session.getInstance(props, authenticator);
        } else
            mailSession = Session.getInstance(props, null);
    }

    @Override
    public Status sendMail(
            String sender,
            String recipients,
            String subject,
            String body,
            Set<File> attachmentFiles,
            boolean shadowCopy,
            String contentMimeType,
            String encoding
    ) {
        logger.info(String.format("Sending email to: %s, from: %s, with subject: %s", recipients, sender, subject));
        MimeMessage message = new MimeMessage(mailSession);
        try {
            if (debugMode) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(debugAddress));
                message.setFrom(new InternetAddress(debugAddress));
            } else {
                if (sender != null) {
                    message.setFrom(new InternetAddress(sender));
                } else {
                    message.setFrom(new InternetAddress(defaultSender));
                }
                for (String recipient : recipients.split(",")) {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                }
                if (shadowCopy) {
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(sender));
                }
            }
        } catch (MessagingException e) {
            logger.error("Cannot send mail to " + recipients, e);
            return Status.INVALID_ADDRESS;
        }

        try {
            message.setSentDate(new Date());
            message.setSubject(subject, "UTF-8");
            message.setText(body, isNotBlank(encoding) ? encoding : DEFAULT_ENCODING);
            if (isNotBlank(contentMimeType)) {
                String contentTypeHeader = contentMimeType + (isNotBlank(encoding) ? "; charset=" + encoding : "");
                message.setHeader("Content-Type", contentTypeHeader);
            } else {
                message.setHeader("Content-Type", "text/plain; charset=UTF-8");
            }

            if (attachmentFiles != null && !attachmentFiles.isEmpty()) {
                MimeBodyPart messagePart = new MimeBodyPart();
                if (StringUtils.isEmpty(contentMimeType))
                    messagePart.setText(body, isNotBlank(encoding) ? encoding : DEFAULT_ENCODING);
                else
                    messagePart.setContent(body, contentMimeType + (isNotBlank(encoding) ? "; charset=" + encoding : ""));
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messagePart);

                for (File attachmentFile : attachmentFiles) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    FileDataSource fileDataSource = new FileDataSource(attachmentFile) {
                        @Override
                        public String getContentType() {
                            return "application/octet-stream";
                        }
                    };
                    attachmentPart.setDataHandler(new DataHandler(fileDataSource));
                    attachmentPart.setFileName(attachmentFile.getName());
                    attachmentPart.setContentID(attachmentFile.getName());
                    attachmentPart.setDisposition(MimeBodyPart.INLINE);
                    attachmentPart.setHeader("Content-ID", "<" + attachmentFile.getName() + ">");
                    multipart.addBodyPart(attachmentPart);
                }
                message.setContent(multipart);
            }

        } catch (MessagingException e) {
            logger.error("Message preparation failed", e);
            return Status.PREPARATION_ERROR;
        }
        return sendOverWire(message);
    }

    protected Status sendOverWire(MimeMessage message) {
        Transport transport;
        try {
            transport = mailSession.getTransport(mailSession.getProperty("mail.transport.protocol"));
        } catch (NoSuchProviderException e) {
            logger.error("Cannot find mail provider", e);
            return Status.SENDING_FAILED;
        }
        try {
            if ("true".equals(mailSession.getProperty("mail.smtp.auth")))
                transport.connect(mailSession.getProperty("mail.host"), Integer.valueOf(mailSession.getProperty("mail.smtp.port")), mailSession.getProperty("mail.user"), mailSession.getProperty("mail.password"));
            else
                transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
        } catch (MessagingException e) {
            logger.error("Message sending failed", e);
            return Status.SENDING_FAILED;
        }
        return Status.SUCCESS;
    }

    private class Authenticator extends javax.mail.Authenticator {
        private PasswordAuthentication authentication;

        public Authenticator(String username, String password) {
            authentication = new PasswordAuthentication(username, password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }

    public String getDefaultSender() {
        return defaultSender;
    }

    public void setDefaultSender(String defaultSender) {
        this.defaultSender = defaultSender;
    }

    public String getDebugAddress() {
        return debugAddress;
    }

    public void setDebugAddress(String debugAddress) {
        this.debugAddress = debugAddress;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
