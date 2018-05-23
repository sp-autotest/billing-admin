package ru.bpc.billing.controller.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Krainov
 * Date: 18.09.2014
 * Time: 11:34
 */
public class ProcessingFileDto {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean success;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String text;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Long id;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String name;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private FileType fileType;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = JsonDateSerializer.class)
    private Date businessDate;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = JsonDateSerializer.class)
    private Date createdDate;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean leaf = true;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<ProcessingFileDto> children;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private boolean canDownloaded;

    public ProcessingFileDto(){}
    public ProcessingFileDto(ProcessingFile processingFile){
        setProcessingFile(processingFile);
    }
    public ProcessingFileDto(ProcessingFile processingFile, boolean success){
        setProcessingFile(processingFile);
        this.success = success;
    }
    public ProcessingFileDto(ProcessingFile processingFile, boolean success, String text){
        setProcessingFile(processingFile);
        this.success = success;
        this.text = text;
    }

    public ProcessingFileDto setProcessingFile(ProcessingFile processingFile) {
        if ( null == processingFile) return this;
        this.id = processingFile.getId();
        this.name = processingFile.getName();
        this.businessDate = processingFile.getBusinessDate();
        this.createdDate = processingFile.getCreatedDate();
        this.fileType = processingFile.getFileType();
        this.canDownloaded = null != processingFile.getOriginalFile() && processingFile.getOriginalFile().exists() && processingFile.getOriginalFile().canRead();
        return this;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isLeaf() {
        return null != children && 0 < children.size() ? false : true;
    }

    public List<ProcessingFileDto> getChildren() {
        return children;
    }

    public void setChildren(List<ProcessingFileDto> children) {
        this.children = children;
    }

    public boolean isCanDownloaded() {
        return canDownloaded;
    }

    public void setCanDownloaded(boolean canDownloaded) {
        this.canDownloaded = canDownloaded;
    }

    public ProcessingFileDto addChild(ProcessingFileDto billingDto) {
        if ( null == children ) {
            children = new ArrayList<>();
        }
        children.add(billingDto);
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProcessingFileDto{");
        sb.append("success=").append(success);
        sb.append(", id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", fileType=").append(fileType);
        sb.append(", businessDate=").append(businessDate);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", canDownloaded=").append(canDownloaded);
        sb.append('}');
        return sb.toString();
    }
}
