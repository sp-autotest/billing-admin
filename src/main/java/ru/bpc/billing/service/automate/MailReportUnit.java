package ru.bpc.billing.service.automate;

import ru.bpc.billing.controller.dto.BillingConverterResultDto;
import ru.bpc.billing.controller.dto.BillingFileDto;
import ru.bpc.billing.controller.dto.PostingDto;
import ru.bpc.billing.domain.BillingSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MailReportUnit {

    private BillingSystem bs;
    private List<String> fileNames = new ArrayList<>();
    private List<String> filteredFileNames;
    private List<File> downloadedFiles;
    private List<File> decryptedFiles;
    private String uploaded;
    private String billingConverterResultDto;
    private String postingsUploaded;

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

    public void addDecryptedFiles(List<File> decryptedFiles) {
        this.decryptedFiles = decryptedFiles;
    }

    public void addUploaded(List<BillingFileDto> uploaded) {
        StringBuilder sb = new StringBuilder();
        for (BillingFileDto each : uploaded) {
            sb.append(String.format("\n\tИмя: %s, тип: %s", each.getName(), each.getFileType()));
        }
        this.uploaded = sb.toString();
    }

    public void addConverted(List<BillingConverterResultDto> dtos) {
        StringBuilder sb = new StringBuilder();
        for (BillingConverterResultDto each : dtos) {
            for (PostingDto eachPosting : each.getPostings()) {
                sb.append(String.format("\n\tПостинг: %s", eachPosting.getName()));
            }
        }
        this.billingConverterResultDto = sb.toString();
    }

    public void addPostingUploaded(List<BillingConverterResultDto> dtos) {
        StringBuilder sb = new StringBuilder();
        for (BillingConverterResultDto each : dtos) {
            for (PostingDto eachPosting : each.getPostings()) {
                sb.append(String.format("\n\tПостинг: %s", eachPosting.getName()));
            }
        }
        this.postingsUploaded = sb.toString();
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
        sb.append("\nРасшифрованы файлы: ").append(printList(decryptedFiles));
        if (decryptedFiles.isEmpty())
            return sb.toString();
        sb.append("\nЗагружены файлы (на наш сервер): ").append(uploaded);
        if (billingConverterResultDto != null)
            sb.append("\nКонвертированы файлы: ").append(billingConverterResultDto);
        sb.append("\nПостинги загружены на scp: ").append(postingsUploaded);
        return sb.toString();
    }
}
