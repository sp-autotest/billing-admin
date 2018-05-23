package ru.bpc.billing.domain;

import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Krainov
 * Date: 01.09.14
 * Time: 13:49
 */
@Entity
@Table(name = "processing_file")
@Inheritance(strategy = InheritanceType.JOINED)
public class ProcessingFile {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "processingFileSequence")
    @SequenceGenerator(name = "processingFileSequence", sequenceName = "SEQ_PROCESSING_FILE", allocationSize = 1)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "business_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date businessDate;
    @Column(name = "created_date")
    private Date createdDate = new Date();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private ProcessingFile parentFile;
    @OneToMany(mappedBy = "parentFile",fetch = FetchType.LAZY)
    private List<ProcessingFile> files;
    @Column(name = "file_type")
    @Enumerated(EnumType.STRING)
    private FileType fileType;
    @Column(name = "original_file_name")
    private String originalFileName;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.file")
    private List<ProcessingFileRecord> processingFileRecords = new LinkedList<>();

    @Transient
    private File originalFile;

    public List<ProcessingFileRecord> getProcessingFileRecords() {
        return processingFileRecords;
    }

    public void setProcessingFileRecords(List<ProcessingFileRecord> processingFileRecords) {
        this.processingFileRecords = processingFileRecords;
    }

    public ProcessingFile(){}
    public ProcessingFile(FileType fileType){
        this.fileType = fileType;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public ProcessingFile getParentFile() {
        return parentFile;
    }

    public void setParentFile(ProcessingFile parentFile) {
        this.parentFile = parentFile;
    }

    public List<ProcessingFile> getFiles() {
        return files;
    }

    public void setFiles(List<ProcessingFile> files) {
        this.files = files;
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

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    /**
     * Transient field
     * @return
     */
    public File getOriginalFile() {
        return originalFile;
    }

    public void setOriginalFile(File originalFile) {
        this.originalFile = originalFile;
    }

    @Transient
    public List<ProcessingRecord> getRecords() {
        List<ProcessingRecord> processingRecords = new ArrayList<>();
        for (ProcessingFileRecord processingFileRecord : getProcessingFileRecords()) {
            processingRecords.add(processingFileRecord.getRecord());
        }
        return processingRecords;
    }


}
