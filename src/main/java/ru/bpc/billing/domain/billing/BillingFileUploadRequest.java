package ru.bpc.billing.domain.billing;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * User: Krainov
 * Date: 20.08.14
 * Time: 13:37
 */
public class BillingFileUploadRequest {

    private Date businessDate;
    private MultipartFile file;

    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

}
