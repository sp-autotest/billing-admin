package ru.bpc.billing.repository;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.TransactionType;

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
 * Date: 05.09.2014
 * Time: 13:41
 */
public class ProcessingRecordFilter implements Specification<ProcessingRecord> {

    private Date fromCreateDate;
    private Date toCreateDate;
    private Long id;
    private String rbsId;
    private String documentDate;
    private String documentNumber;
    private TransactionType transactionType;
    private String approvalCode;
    private String pan;
    private Integer page;
    private Integer size;

    public ProcessingRecordFilter(){
        initToDate();
        initFromDate();
    }
    public ProcessingRecordFilter(String documentDate, String documentNumber, TransactionType transactionType) {
        this();
        this.documentDate = documentDate;
        this.documentNumber = documentNumber;
        this.transactionType = transactionType;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public Date getFromCreateDate() {
        return fromCreateDate;
    }

    public void setFromCreateDate(Date fromCreateDate) {
        this.fromCreateDate = fromCreateDate;
    }

    public Date getToCreateDate() {
        return toCreateDate;
    }

    public void setToCreateDate(Date toCreateDate) {
        this.toCreateDate = toCreateDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRbsId() {
        return rbsId;
    }

    public void setRbsId(String rbsId) {
        this.rbsId = rbsId;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
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

    @Override
    public Predicate toPredicate(Root<ProcessingRecord> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if ( null != id ) {
            return criteriaBuilder.equal(root.<Long>get("id"),id);
        }
        if ( StringUtils.isNotBlank(rbsId) ) {
            return criteriaBuilder.equal(root.<String>get("rbsId"),rbsId);
        }
        List<Predicate> predicates = new ArrayList<>();

        if ( null != getFromCreateDate() && null != getToCreateDate() ) {
            predicates.add(criteriaBuilder.between(root.<Date>get("createdAt"), getFromCreateDate(), getToCreateDate()));
        }
        if ( StringUtils.isNotBlank(approvalCode) ) {
            predicates.add(criteriaBuilder.equal(root.<String>get("approvalCode"),approvalCode));
        }
        if ( StringUtils.isNotBlank(pan) ) {
            predicates.add(criteriaBuilder.equal(root.<String>get("pan"),pan));
        }
        if (StringUtils.isNotBlank(documentDate) ) {
            predicates.add(criteriaBuilder.equal(root.<String>get("documentDate"), documentDate));
        }
        if (  StringUtils.isNotBlank(documentNumber) ) {
            predicates.add(criteriaBuilder.equal(root.<String>get("documentNumber"),documentNumber));
        }
        if ( null != transactionType ) {
            predicates.add(criteriaBuilder.equal(root.<String>get("transactionType"),transactionType));
        }

//        CriteriaQuery<Long> cc = criteriaBuilder.createQuery(Long.class);
//        cc.select(criteriaBuilder.count(cc.from(ProcessingRecord.class)));

        return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
    }
}
