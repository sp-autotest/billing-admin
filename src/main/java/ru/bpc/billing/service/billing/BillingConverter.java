package ru.bpc.billing.service.billing;

import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.service.ISystem;

import java.io.File;
import java.io.IOException;

public interface BillingConverter extends ISystem {

    public BillingConverterResult convert(BillingFile... billingFiles) throws IOException;
    public BillingValidateResult validate(File file) throws IOException;
}
