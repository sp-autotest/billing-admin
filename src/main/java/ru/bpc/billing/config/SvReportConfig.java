package ru.bpc.billing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.service.billing.BillingConverter;
import ru.bpc.billing.service.billing.sv.SvNewArcBillingConverter;
import ru.bpc.billing.service.billing.sv.SvNewBSPBillingConverter;
import ru.bpc.billing.service.bo.BOProcessor;
import ru.bpc.billing.service.bo.sv.SvBOProcessor;
import ru.bpc.billing.service.report.ReportBuilder;
import ru.bpc.billing.service.report.ReportProcessor;
import ru.bpc.billing.service.report.ReportType;
import ru.bpc.billing.service.report.revenue.sv.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Krainov
 * Date: 15.09.2014
 * Time: 16:06
 */
@Configuration
public class SvReportConfig {

    @Bean
    public Map<BillingFileFormat,BillingConverter> billingConverters() {
        Map<BillingFileFormat,BillingConverter> map = new HashMap<>();
        map.put(BillingFileFormat.ARC,arcBillingConverter());
        map.put(BillingFileFormat.BSP,bspBillingConverter());
        return map;
    }

    @Bean
    public BillingConverter arcBillingConverter() {
        //return new SvARCBillingConverter();
        return new SvNewArcBillingConverter();
    }

    @Bean
    public BillingConverter bspBillingConverter() {
        //return new SvBSPBillingConverter();
        return new SvNewBSPBillingConverter();
    }

    @Bean
    public BOProcessor boProcessor() {
        return new SvBOProcessor();
    }

    @Bean
    public ReportBuilder excelRevenueReportBuilder() {
        return new SvExcelRevenueReportBuilder();
    }
    @Bean
    public ReportBuilder nspcExcelRevenueReportBuilder() {
        return new NspcSvExcelRevenueReportBuilder();
    }
    @Bean
    public ReportBuilder excelRevenueOperationRegisterReportBuilder() {
        return new SvExcelRevenueOperationRegisterReportBuilder();
    }
    @Bean
    public ReportBuilder nspcExcelRevenueOperationRegisterReportBuilder() {
        return new NspcSvExcelRevenueOperationRegisterReportBuilder();
    }
    @Bean
    public ReportBuilder xmlAccelyaRevenueReportBuilder() {
        return new SvXmlAccelyaRevenueReportBuilder();
    }
    @Bean
    public ReportBuilder excelTicketInfoReportBuilder() {
        return new SvExcelTicketInfoReportBuilder();
    }


    @Bean
    public ReportProcessor standardReportProcessor() {
        SvRevenueReportProcessor reportProcessor = new SvRevenueReportProcessor();
        //DummyReportProcessor reportProcessor = new DummyReportProcessor();
        List<ReportBuilder> reportBuilders = new ArrayList<>();
        reportBuilders.add(excelRevenueReportBuilder());
        reportBuilders.add(xmlAccelyaRevenueReportBuilder());
        reportBuilders.add(excelRevenueOperationRegisterReportBuilder());
        reportBuilders.add(excelTicketInfoReportBuilder());
        reportProcessor.setReportBuilders(reportBuilders);
        return reportProcessor;
    }

    @Bean
    public ReportProcessor nspcReportProcessor() {
        NspcSvRevenueReportProcessor reportProcessor = new NspcSvRevenueReportProcessor();
        List<ReportBuilder> reportBuilders = new ArrayList<>();
        reportBuilders.add(nspcExcelRevenueReportBuilder());
        reportBuilders.add(xmlAccelyaRevenueReportBuilder());
        reportBuilders.add(nspcExcelRevenueOperationRegisterReportBuilder());
        reportProcessor.setReportBuilders(reportBuilders);
        return reportProcessor;
    }

    @Bean
    public Map<ReportType,ReportProcessor> reportProcessors() {
        Map<ReportType,ReportProcessor> reportProcessors = new HashMap<>();
        reportProcessors.put(ReportType.STANDARD, standardReportProcessor());
        reportProcessors.put(ReportType.NSPC, nspcReportProcessor());
        return reportProcessors;
    }
}
