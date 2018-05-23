package ru.bpc.billing.service.mail;

import java.io.File;
import java.util.Set;


public interface Mailer {
    DefaultMailer.Status sendMail(String sender, String recipient, String subject, String body, Set<File> attachmentFiles, boolean shadowCopy,
                                  String contentMimeType, String encoding);
}
