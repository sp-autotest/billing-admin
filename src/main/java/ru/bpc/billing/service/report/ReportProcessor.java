package ru.bpc.billing.service.report;

import ru.bpc.billing.domain.billing.BillingFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: Krainov
 * Date: 03.09.2014
 * Time: 16:40
 */
public interface ReportProcessor {

    public ReportProcessingResult process(List<BillingFile> billingFiles, List<File> boFiles) throws IOException, InterruptedException, ReportBuildException;
}
