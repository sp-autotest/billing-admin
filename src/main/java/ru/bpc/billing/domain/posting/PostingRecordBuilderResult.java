package ru.bpc.billing.domain.posting;

import ru.bpc.billing.service.billing.BillingConverterException;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Krainov
 * Date: 21.11.13
 * Time: 11:27
 */
public class PostingRecordBuilderResult {

    private PostingRecord postingRecord;
    private List<BillingConverterException> billingConverterExceptions = new ArrayList<BillingConverterException>();
    private boolean isNotFinancial;

    public PostingRecordBuilderResult(PostingRecord postingRecord) {
        this.postingRecord = postingRecord;
    }

    public PostingRecordBuilderResult addConverterException(BillingConverterException e) {
        billingConverterExceptions.add(e);
        return this;
    }

    public boolean isSuccess() {
        return billingConverterExceptions.isEmpty();
    }

    public void throwIfNotSuccess() throws BillingConverterException {
        if (!isSuccess()) throw billingConverterExceptions.iterator().next();
    }

    public String getFirstErrorIfExists() {
        if ( 0 < billingConverterExceptions.size() ) return billingConverterExceptions.iterator().next().getErrorMessageCode();
        return null;
    }

    public PostingRecord getPostingRecord() {
        return postingRecord;
    }

    public boolean isNotFinancial() {
        return isNotFinancial;
    }

    public void setNotFinancial(boolean notFinancial) {
        isNotFinancial = notFinancial;
    }
}
