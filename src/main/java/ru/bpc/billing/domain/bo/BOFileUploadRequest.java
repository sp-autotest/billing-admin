package ru.bpc.billing.domain.bo;

import org.springframework.web.multipart.MultipartFile;

/**
 * User: Krainov
 * Date: 03.09.2014
 * Time: 16:59
 */
public class BOFileUploadRequest {

    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
