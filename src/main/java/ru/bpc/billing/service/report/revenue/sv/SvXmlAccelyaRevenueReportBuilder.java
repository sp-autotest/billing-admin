package ru.bpc.billing.service.report.revenue.sv;

import com.google.common.collect.Multimap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.report.ReportRecord;
import ru.bpc.billing.service.ApplicationService;
import ru.bpc.billing.service.ISystem;
import ru.bpc.billing.service.report.*;
import ru.bpc.billing.domain.report.accelya.ItemError;
import ru.bpc.billing.domain.report.accelya.ItemsError;
import ru.bpc.billing.domain.report.accelya.Receipt;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: Krainov
 * Date: 15.09.2014
 * Time: 16:00
 */
public class SvXmlAccelyaRevenueReportBuilder implements ReportBuilder {

    public static final Logger logger = LoggerFactory.getLogger(SvXmlAccelyaRevenueReportBuilder.class);
    public static final String RECEIPT_PREFIX = "Receipt";
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    public static final String NO_DATA_IN_FILE = "NO_DATA_IN_FILE";

    @Resource
    private Jaxb2Marshaller jaxb2Marshaller;
    @Resource
    private ApplicationService applicationService;
    @Resource
    private MessageSource messageSource;

    protected String generateSourceFileName(LoadAndGroupTickets loadAndGroupTickets) {
        StringBuilder fileNames = new StringBuilder();
        fileNames.append(loadAndGroupTickets.getBillingFiles().get(0).getName());
//        for (BillingFile billingFile : loadAndGroupTickets.getBillingFiles()) {
//            fileNames.append(billingFile.getName()).append("_");
//        }
        return fileNames.toString();
    }

    protected String generateFileName(boolean success, LoadAndGroupTickets loadAndGroupTickets) {
        return RECEIPT_PREFIX + "_" + (success ? OK + "_" : "") + generateSourceFileName(loadAndGroupTickets);
    }

    protected File successFile(LoadAndGroupTickets loadAndGroupTickets) throws ReportBuildException {
        String sourceFileName = generateSourceFileName(loadAndGroupTickets);
        Receipt receipt = new Receipt();
        receipt.setSourceFileName(sourceFileName);
        receipt.setStatusProcess(OK);
        return writeToFile(generateFileName(true, loadAndGroupTickets),receipt);
    }

    protected File rejectFile(LoadAndGroupTickets loadAndGroupTickets, AtomicBoolean stopped) throws ReportBuildException, InterruptedException {
        String sourceFileName = generateSourceFileName(loadAndGroupTickets);
        Receipt receipt = new Receipt();
        receipt.setSourceFileName(sourceFileName);
        receipt.setStatusProcess(ERROR);
        ItemsError itemsError = new ItemsError();
        int count = 0;
        for (Map.Entry<String, Multimap<RejectReportGroup, ReportRecord>> entry : loadAndGroupTickets.groupedRejectTickets.entrySet()) {
            String country = entry.getKey();
            logger.trace("Build items error for country: {}", country);
            Multimap<RejectReportGroup, ReportRecord> recordMultimap = entry.getValue();
            for (RejectReportGroup group : recordMultimap.keySet()) {
                Collection<ReportRecord> revenueRecords = recordMultimap.get(group);
                int i = 1;
                for (ReportRecord revenueRecord : revenueRecords) {
                    if (i % 100 == 0 && stopped.get()) {
                        throw new InterruptedException("Interrupted");
                    }
                    itemsError.getItemError().add(buildItemError(revenueRecord));
                    count++;
                    i++;
                }
            }
        }
        logger.debug("Build reject report for accelya file for {} records.", count);
        receipt.setItemsError(itemsError);
        return writeToFile(generateFileName(false,loadAndGroupTickets), receipt);
    }

    protected String checkExistsSourceFile(final String fileName) {
        return fileName;
        /*
        IOFileFilter filter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().startsWith(fileName + "_");
            }

            @Override
            public boolean accept(File dir, String name) {
                return false;
            }
        };
        Iterator<File> it = FileUtils.iterateFiles(new File(applicationService.getHomeDir(getFileType())), filter, null);
        boolean hasFiles = it.hasNext();
        int min = 1;
        while (it.hasNext()) {
            Integer num = Integer.parseInt(StringUtils.substringBetween(it.next().getName(), fileName + "_", "." + "xml"));
            if ( num > min ) min = num;
        }
        return fileName + "_" + (min + (hasFiles ? 1: 0));
        */
    }

    protected File writeToFile(String fileName, Receipt receipt) throws ReportBuildException {
        try {
            File file = new File(applicationService.getHomeDir(getFileType()) + checkExistsSourceFile(fileName) + "." + "xml");
            StreamResult streamResult = new StreamResult(file);
            jaxb2Marshaller.marshal(receipt, streamResult);
            return file;
        } catch (Exception e) {
            throw new ReportBuildException("Error during build accelya revenue report.", e);
        }
    }

    protected ItemError buildItemError(ReportRecord revenueRecord) {
        ItemError itemError = new ItemError();
        itemError.setTicketNumber(fillItemErrorField(revenueRecord.getDocumentNumber()));
        itemError.setTicketAmount(fillItemErrorField(revenueRecord.getGrossOperation().toString()));
        itemError.setTicketCurrency(fillItemErrorField(revenueRecord.getCurrencyOperation()));
        itemError.setExpiryDate(fillItemErrorField(revenueRecord.getExpirationDate()));
        itemError.setApprovalCode(fillItemErrorField(revenueRecord.getApprovalCode()));
        itemError.setTypeDocument(fillItemErrorField(revenueRecord.getOperSign()));
        itemError.setReason(fillItemErrorField(revenueRecord.getErrorMessage()));

        return itemError;
    }

    protected String fillItemErrorField(String value) {
        return StringUtils.isBlank(value) ? NO_DATA_IN_FILE : value;
    }

    @Override
    public File build(LoadAndGroupTickets loadAndGroupTickets, AtomicBoolean stopped) throws ReportBuildException {
        try {
            return 0 < loadAndGroupTickets.groupedRejectTickets.size() ? rejectFile(loadAndGroupTickets,stopped) : successFile(loadAndGroupTickets);
        } catch (InterruptedException e) {
            throw new ReportBuildException("Error build " + getFileType() + " type file",e);
        }
    }

    @Override
    public FileType getFileType() {
        return FileType.REVENUE_REPORT_XML_ACCELYA;
    }

    @Override
    public boolean linkFileToRecord(ProcessingFile processingFile, ReportRecord reportRecord) {
        return true;
    }

    @Override
    public String getSystemName() {
        return messageSource.getMessage(ISystem.SYSTEM_PREFIX + "report.xmlRevenueAccelyaSv", new Object[]{},
                "XML revenue accelya report for Smart Vista", LocaleContextHolder.getLocale());
    }
}
