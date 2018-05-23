package ru.bpc.billing.controller.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;
import java.util.List;

/**
 * User: Krainov
 * Date: 27.08.14
 * Time: 15:22
 */
public class BoDto {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean success;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String text;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String fileName;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = JsonDateSerializer.class)
    private Date createdDate;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Long size;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<BoDto> children;
    @JsonSerialize
    private String iataCode;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<BoDto> getChildren() {
        return children;
    }

    public void setChildren(List<BoDto> children) {
        this.children = children;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BoDto");
        sb.append("{fileName='").append(fileName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getIataCode() {
        return iataCode;
    }
}
