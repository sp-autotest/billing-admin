package ru.bpc.billing.service.automate.bo;

import ru.bpc.billing.controller.dto.*;
import ru.bpc.billing.domain.BillingSystem;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.report.ReportFile;
import ru.bpc.billing.service.bo.BOProcessingResult;
import ru.bpc.billing.service.report.ReportProcessingResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MailReportUnitBo {

    //private BillingSystem bs;
    private List<String> fileNames = new ArrayList<>();
    private List<String> filteredFileNames;
    private List<File> downloadedFiles;

    private List<ReportFile> attachmentFiles = new ArrayList<>();
    private String uploaded;
    private String reportResultDto;

    private List<ReportFile> reportFiles;
    private List<String> iataList;

    public List<ReportFile> getReportFiles() {
        return reportFiles;
    }

    public List<String> getIataList() {
        return iataList;
    }


    public List<ReportFile> getAttachmentFiles() {
        return attachmentFiles;
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

    public void addUploaded(List<BoDto> uploaded) {
        StringBuilder sb = new StringBuilder();
        for (BoDto each : uploaded) {
            sb.append(String.format("\n<p>Имя: %s, тип: %s</p>", each.getFileName(), "BO"));
        }
        this.uploaded = sb.toString();
    }

    public void addConverted(ReportProcessingResultDto dto) {
        StringBuilder sb = new StringBuilder();
        for (BillingFileDto eachBilling : dto.getBillingFiles()) {
            sb.append(String.format("\n<p>Биллинговый файл: %s</p>", eachBilling.getName()));
        }

        for (BOFileDto eachBo : dto.getBoFiles()) {
            sb.append(String.format("\n<p>БO файл: %s", eachBo.getName())).append("</p>");
        }

//        for(ReportFileDto rdto : dto.getReportFiles()) {
//            sb.append(String.format("\n\tКредит успешно: %s", rdto.successCreditRecordsCount));
//            sb.append(String.format("\n\tКредит отклонено: %s", rdto.rejectCreditRecordsCount));
//
//            sb.append(String.format("\n\tДепозит успешно: %s", rdto.successDepositRecordsCount));
//            sb.append(String.format("\n\tДепозит отклонено: %s", rdto.successCreditRecordsCount));
//
//        }
        //sb.append(String.format("\n\tОбработка БО:"));
        ReportProcessingResult reportProcessingResult = dto.reportProcessingResult;

        for(BOProcessingResult boRes : reportProcessingResult.getBoProcessingResults()) {
            sb.append("<table>");
//            sb.append(String.format("\n\tФайл: %s", boRes.getOriginalFile().getName()));
            sb.append("<tr><td>").append("Файл").append("</td><td>").append(boRes.getOriginalFile().getName()).append("</td></tr>");
//            sb.append(String.format("\n\tВсего записей: %d", boRes.getTotalRecords()));
            sb.append("<tr><td>").append("Всего записей").append("</td><td>").append(boRes.getTotalRecords()).append("</td></tr>");
//            sb.append(String.format("\n\tУспешных: %d", boRes.getSuccessRecords()));
            sb.append("<tr><td>").append("Успешных").append("</td><td>").append(boRes.getSuccessRecords()).append("</td></tr>");
//            sb.append(String.format("\n\tОшибочных: %d", boRes.getErrorRecords()));
            sb.append("<tr><td>").append("Ошибочных").append("</td><td>").append(boRes.getErrorRecords()).append("</td></tr>");
//            sb.append(String.format("\n\tФрод: %d", boRes.getFraudRecords()));
            sb.append("<tr><td>").append("Фрод").append("</td><td>").append(boRes.getFraudRecords()).append("</td></tr>");
//            sb.append(String.format("\n\tДепозит: %d", boRes.getDepositRecords()));
            sb.append("<tr><td>").append("Депозит").append("</td><td>").append(boRes.getDepositRecords()).append("</td></tr>");
//            sb.append(String.format("\n\tРефанд: %d", boRes.getRefundRecords()));
            sb.append("<tr><td>").append("Рефанд").append("</td><td>").append(boRes.getRefundRecords()).append("</td></tr>");
            sb.append("</table><br>");
        }

        this.reportResultDto = sb.toString();
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
        sb.append("<html><head></head><body>");
        sb.append("<h3>Результат выгрузки BO файлов</h3>");
        if (fileNames.isEmpty()) {
            sb.append("</body></html>");
            return sb.toString();
        }

        //sb.append("\nОбнаружены файлы: ").append(printList(fileNames));

//        sb.append("\nОтфильтрованы файлы: ").append(printList(filteredFileNames));
//        if (filteredFileNames.isEmpty())
//            return sb.toString();
        sb.append("\n<p>Скачаны файлы: ").append(printList(downloadedFiles)).append("</p>");
        if (downloadedFiles.isEmpty()) {
            sb.append("</body></html>");
            return sb.toString();
        }
//        sb.append("\nРасшифрованы файлы: ").append(printList(decryptedFiles));
//        if (decryptedFiles.isEmpty())
//            return sb.toString();
        sb.append("\n<p>Загружены файлы (на наш сервер): ").append(uploaded).append("</p>");
        if (reportResultDto != null)
            sb.append("\n<p>Созданы отчеты:<br>").append(reportResultDto).append("</p>");
        //sb.append("\nПостинги загружены на scp: ").append(postingsUploaded);
        sb.append("</body></html>");
        return sb.toString();
    }

    public void addReportFiles(List<ReportFile> reportFiles) {
        this.attachmentFiles = reportFiles;
    }

    public void addIataList(List<String> bsList) {
        this.iataList = bsList;
    }

}
