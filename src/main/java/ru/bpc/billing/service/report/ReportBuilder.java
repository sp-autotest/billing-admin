package ru.bpc.billing.service.report;

import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.report.ReportRecord;
import ru.bpc.billing.service.ISystem;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: Krainov
 * Date: 15.09.2014
 * Time: 15:16
 */
public interface ReportBuilder extends ISystem {

    public File build(LoadAndGroupTickets loadAndGroupTickets, AtomicBoolean stopped) throws ReportBuildException;
    public FileType getFileType();
    public boolean linkFileToRecord(ProcessingFile processingFile, ReportRecord reportRecord);
}
