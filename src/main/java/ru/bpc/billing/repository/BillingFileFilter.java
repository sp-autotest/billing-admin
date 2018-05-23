package ru.bpc.billing.repository;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileFormat;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: Krainov
 * Date: 23.09.2014
 * Time: 17:39
 */
public class BillingFileFilter implements Specification<BillingFile> {

    private Long id;
    private String filename;
    private Date fromCreateDate;
    private Date toCreateDate;
    private SearchMode mode = SearchMode.BY_BILLING;
    private FileType fileType;
    private Date processingDate;
    private BillingFileFormat billingFileFormat;
    private Integer countLines;
    private boolean checkAvailability;


    public BillingFileFilter() {
        initFromDate();
        initToDate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BillingFileFilter(String filename, Date fromCreateDate, Date toCreateDate) {
        this.filename = filename;
        this.fromCreateDate = fromCreateDate;
        this.toCreateDate = toCreateDate;
    }

    public BillingFileFilter(ProcessingFileFilter filter) {
        this.filename = filter.getFilename();
        this.fromCreateDate = filter.getFromCreateDate();
        this.toCreateDate = filter.getToCreateDate();
        this.id = filter.getId();
        this.mode = filter.getMode();
        this.fileType = filter.getFileType();
        this.processingDate = filter.getProcessingDate();
        this.billingFileFormat = filter.getBillingFileFormat();
        this.countLines = filter.getCountLines();
        this.checkAvailability = filter.isCheckAvailability();
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        if (this.filename == null && filename != null || this.filename != null && !this.filename.equals(filename)) {
            this.filename = filename;

        }
    }

    public Date getFromCreateDate() {
        return fromCreateDate;
    }

    public void setFromCreateDate(Date fromBusinessDate) {
        if (this.fromCreateDate == null && fromBusinessDate != null || this.fromCreateDate != null && !this.fromCreateDate.equals(fromBusinessDate)) {
            this.fromCreateDate = fromBusinessDate;

        }
    }

    public Date getToCreateDate() {
        return toCreateDate;
    }

    public void setToCreateDate(Date toBusinessDate) {
        if (this.toCreateDate == null && toBusinessDate != null || this.toCreateDate != null && !this.toCreateDate.equals(toBusinessDate)) {
            this.toCreateDate = toBusinessDate;

        }
    }

    public SearchMode getMode() {
        return mode;
    }

    public void setMode(SearchMode mode) {
        if (this.mode != mode) {
            this.mode = mode;
        }
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        if (this.fileType != fileType) {
            this.fileType = fileType;
        }
    }

    public void resetFilter() {
        setFilename(null);
        initFromDate();
        initToDate();
        setMode(SearchMode.BY_BILLING);
        setFileType(null);
    }

    public void setFilterModified() {

    }

    public Date getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(Date processingDate) {
        this.processingDate = processingDate;
    }

    public BillingFileFormat getBillingFileFormat() {
        return billingFileFormat;
    }

    public void setBillingFileFormat(BillingFileFormat billingFileFormat) {
        this.billingFileFormat = billingFileFormat;
    }

    public Integer getCountLines() {
        return countLines;
    }

    public void setCountLines(Integer countLines) {
        this.countLines = countLines;
    }

    private void initFromDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -9);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        fromCreateDate = cal.getTime();
    }

    private void initToDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        toCreateDate = cal.getTime();
    }

    public boolean isCheckAvailability() {
        return checkAvailability;
    }

    public void setCheckAvailability(boolean checkAvailability) {
        this.checkAvailability = checkAvailability;
    }

    @Override
    public Predicate toPredicate(Root<BillingFile> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if ( null != id ) {
            return criteriaBuilder.equal(root.<Long>get("id"),id);
        }
        List<Predicate> predicates = new ArrayList<>();

        if ( mode.equals(SearchMode.BY_BILLING) ) {
            predicates.add(criteriaBuilder.equal(root.<FileType>get("fileType"),FileType.BILLING));
        }
        else if ( mode.equals(SearchMode.BY_LINKED) ) {
            predicates.add(criteriaBuilder.notEqual(root.<FileType>get("fileType"), FileType.BILLING));
        }
        if ( !StringUtils.isBlank(filename) ) {
            predicates.add(criteriaBuilder.like(root.<String>get("name"), filename, '\\'));
        }
        if ( null != getFromCreateDate() && null != getToCreateDate() ) {
            predicates.add(criteriaBuilder.between(root.<Date>get("createdDate"), getFromCreateDate(), getToCreateDate()));
        }

        if ( null != getProcessingDate() || null != getBillingFileFormat() || null != getCountLines() ) {
            Root<BillingFile> billingFileRoot = criteriaQuery.from(BillingFile.class);

            if (null != getProcessingDate()) {
                predicates.add(criteriaBuilder.equal(billingFileRoot.<Date>get("processingDate"), getProcessingDate()));
            }
            if (null != getBillingFileFormat()) {
                predicates.add(criteriaBuilder.equal(billingFileRoot.<BillingFileFormat>get("format"), getBillingFileFormat()));
            }
            if (null != getCountLines()) {
                predicates.add(criteriaBuilder.equal(billingFileRoot.<Integer>get("countLines"), getCountLines()));
            }
        }



        return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
    }
}
