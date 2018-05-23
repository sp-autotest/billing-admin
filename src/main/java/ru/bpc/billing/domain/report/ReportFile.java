package ru.bpc.billing.domain.report;

import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * User: Krainov
 * Date: 15.09.2014
 * Time: 14:59
 */
@Entity
@Table(name = "report_file")
public class ReportFile extends ProcessingFile {

    public ReportFile() {
        super();
    }

    public ReportFile(FileType fileType) {
        super(fileType);
    }

    @Transient
    public int successDepositRecordsCount;
    @Transient
    public int successCreditRecordsCount;
    @Transient
    public int rejectDepositRecordsCount;
    @Transient
    public int rejectCreditRecordsCount;

}
