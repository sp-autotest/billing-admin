package ru.bpc.billing.controller.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.bo.BOFile;
import ru.bpc.billing.domain.report.ReportFile;
import ru.bpc.billing.service.bo.BOProcessingResult;
import ru.bpc.billing.service.report.ReportProcessingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Krainov
 * Date: 17.09.2014
 * Time: 14:37
 */
public class ReportProcessingResultDto {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Boolean success;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String text;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<BillingFileDto> billingFiles = new ArrayList<>();
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<BOFileDto> boFiles = new ArrayList<>();
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private List<ReportFileDto> reportFiles = new ArrayList<>();
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private ReportFileDto reportData;

    public List<BillingFileDto> getBillingFiles() {
        return billingFiles;
    }

    public void setBillingFiles(List<BillingFileDto> billingFiles) {
        this.billingFiles = billingFiles;
    }

    public List<BOFileDto> getBoFiles() {
        return boFiles;
    }

    public void setBoFiles(List<BOFileDto> boFiles) {
        this.boFiles = boFiles;
    }

    public List<ReportFileDto> getReportFiles() {
        return reportFiles;
    }

    public void setReportFiles(List<ReportFileDto> reportFiles) {
        this.reportFiles = reportFiles;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setReportProcessingResult(ReportProcessingResult result) {
        if ( !result.getBillingFiles().isEmpty() ) {
            List<BillingFileDto> billingFileDtos = new ArrayList<>();
            for (BillingFile billingFile : result.getBillingFiles()) {
                BillingFileDto billingDto = new BillingFileDto(billingFile);
                billingFileDtos.add(billingDto);
            }
            setBillingFiles(billingFileDtos);
        }

        if ( !result.getBoProcessingResults().isEmpty() ) {
            List<BOFileDto> boFiles = new ArrayList<>();
            for (BOProcessingResult boProcessingResult : result.getBoProcessingResults()) {
                BOFile boFile = boProcessingResult.getBoFile();
                if ( null != boFile ) boFiles.add(new BOFileDto().setBOFile(boFile).setBOProcessingResult(boProcessingResult));
            }
            setBoFiles(boFiles);
        }

        if ( !result.getReportFiles().isEmpty() ) {
            reportData = new ReportFileDto();
            reportData.setReportFile(result.getReportFiles().get(0));
            /*
            List<ReportFileDto> reportDtos = new ArrayList<>();
            for (ReportFile reportFile : result.getReportFiles()) {
                ReportFileDto reportDto = new ReportFileDto();
                reportDto.setReportFile(reportFile);
                reportDtos.add(reportDto);
            }
            setReportFiles(reportDtos);
            */
        }

    }
}
