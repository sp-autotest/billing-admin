package ru.bpc.billing.service.automate.bo;

import ru.bpc.billing.controller.dto.*;
import ru.bpc.billing.domain.BillingSystem;
import ru.bpc.billing.service.bo.BOProcessingResult;
import ru.bpc.billing.service.report.ReportProcessingResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MailReportUnitBo {

    private BillingSystem bs;
    private List<String> fileNames = new ArrayList<>();
    private List<String> filteredFileNames;
    private List<File> downloadedFiles;
    //private List<File> decryptedFiles;
    private String uploaded;
    private String reportResultDto;
    List<ReportFileDto> reportFiles;
    //private String postingsUploaded;

    public void addConnected(BillingSystem bs) {
        this.bs = bs;
    }

    public void addFileNames(List<String> fileNames) {
        this.fileNames.addAll(fileNames);
    }

    public void addFilteredFileNames(List<String> filteredFileNames) {
        this.filteredFileNames = filteredFileNames;
    }

    public void addDownloaded(List<File> downloadedFiles) {
        this.downloadedFiles = downloadedFiles;
    }

//    public void addDecryptedFiles(List<File> decryptedFiles) {
//        this.decryptedFiles = decryptedFiles;
//    }

    public void addUploaded(List<BoDto> uploaded) {
        StringBuilder sb = new StringBuilder();
        for (BoDto each : uploaded) {
            sb.append(String.format("\n\tИмя: %s, тип: %s", each.getFileName(), "BO"));
        }
        this.uploaded = sb.toString();
    }

    public void addConverted(ReportProcessingResultDto dto) {
        StringBuilder sb = new StringBuilder();
        for (BillingFileDto eachBilling : dto.getBillingFiles()) {
            sb.append(String.format("\n\tБиллинговые файлы: %s", eachBilling.getName()));
        }

        for (BOFileDto eachBo : dto.getBoFiles()) {
            sb.append(String.format("\n\tБO файлы: %s", eachBo.getName()));
        }

        for(ReportFileDto rdto : dto.getReportFiles()) {
            sb.append(String.format("\n\tКредит успешно: %s", rdto.successCreditRecordsCount));
            sb.append(String.format("\n\tКредит отклонено: %s", rdto.rejectCreditRecordsCount));

            sb.append(String.format("\n\tДепозит успешно: %s", rdto.successDepositRecordsCount));
            sb.append(String.format("\n\tДепозит отклонено: %s", rdto.successCreditRecordsCount));

        }
        sb.append(String.format("\n\tОбработка БО:"));
        ReportProcessingResult reportProcessingResult = dto.reportProcessingResult;
        for(BOProcessingResult boRes : reportProcessingResult.getBoProcessingResults()) {
            sb.append(String.format("\n\tФайл: %s", boRes.getOriginalFile().getName()));
            sb.append(String.format("\n\tВсего записей: %d", boRes.getTotalRecords()));
            sb.append(String.format("\n\tУспешных: %d", boRes.getSuccessRecords()));
            sb.append(String.format("\n\tОшибочных: %d", boRes.getErrorRecords()));
            sb.append(String.format("\n\tФрод: %d", boRes.getFraudRecords()));
            sb.append(String.format("\n\tДепозит: %d", boRes.getDepositRecords()));
            sb.append(String.format("\n\tРефанд: %d", boRes.getRefundRecords()));
        }
        this.reportResultDto = sb.toString();
    }

    public void addReportFiles(List<ReportFileDto> dtos) {
        this.reportFiles = reportFiles;
        StringBuilder sb = new StringBuilder();
        for(ReportFileDto dto : dtos) {
            sb.append(String.format("\n\tКредит успешно: %s", dto.successCreditRecordsCount));
            sb.append(String.format("\n\tКредит отклонено: %s", dto.rejectCreditRecordsCount));

            sb.append(String.format("\n\tДепозит успешно: %s", dto.successDepositRecordsCount));
            sb.append(String.format("\n\tДепозит отклонено: %s", dto.successCreditRecordsCount));

        }
    }

    private String printList(List<?> list) {
        StringBuilder result = new StringBuilder();
        for (Object s : list) {
            result.append("\n\t").append(s.toString());
        }
        return result.toString();
    }

    public String getReport() {
        StringBuilder sb = new StringBuilder();
        if (bs == null) {
            sb.append("Не удалось подключиться к sftp");
            return sb.toString();
        }
        sb.append("\nПодключились к серверу sftp: ").append(bs.toString());
        if (fileNames.isEmpty())
            return sb.toString();

        sb.append("\nОбнаружены файлы: ").append(printList(fileNames));

        sb.append("\nОтфильтрованы файлы: ").append(printList(filteredFileNames));
        if (filteredFileNames.isEmpty())
            return sb.toString();
        sb.append("\nСкачаны файлы: ").append(printList(downloadedFiles));
        if (downloadedFiles.isEmpty())
            return sb.toString();
//        sb.append("\nРасшифрованы файлы: ").append(printList(decryptedFiles));
//        if (decryptedFiles.isEmpty())
//            return sb.toString();
        sb.append("\nЗагружены файлы (на наш сервер): ").append(uploaded);
        if (reportResultDto != null)
            sb.append("\nКонвертированы файлы: ").append(reportResultDto);
        //sb.append("\nПостинги загружены на scp: ").append(postingsUploaded);
        return sb.toString();
    }
}
