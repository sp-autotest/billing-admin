package ru.bpc.billing.service.report;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.bpc.billing.AbstractTest;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;

/**
 * User: Krainov
 * Date: 15.09.2014
 * Time: 17:52
 */
public class ReportProcessorTest extends AbstractTest {

    @Resource
    private ReportProcessor reportProcessor;

    @Test
    @Transactional
    @Rollback(false)
    public void testProcess() throws Exception {
        BillingFile billingFile = (BillingFile) processingFileRepository.findOne(1L);
        assertNotNull(billingFile);
        File boFile = new File("D:\\tmp\\bsp-admin\\bo\\test1400502502528");
        ReportProcessingResult result = reportProcessor.process(Arrays.asList(billingFile),Arrays.asList(boFile));
        assertNotNull(result);
    }
}
