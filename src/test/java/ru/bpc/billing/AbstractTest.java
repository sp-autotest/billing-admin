package ru.bpc.billing;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.bpc.billing.config.ApplicationConfig;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.domain.bo.BOFile;
import ru.bpc.billing.repository.*;
import ru.bpc.billing.service.ApplicationService;
import ru.bpc.billing.service.SequenceService;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.File;
import java.util.Date;
import java.util.Locale;

/**
 * User: Krainov
 * Date: 13.08.14
 * Time: 15:35
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,classes = {
        ApplicationConfig.class
})
public class AbstractTest extends TestCase {

    @Resource
    protected ApplicationContext applicationContext;
    @Resource
    protected DataSource dataSource;
    @PersistenceContext
    protected EntityManager entityManager;
    @Value("${database.driverClassName}")
    private String driverClassName;
    @Resource
    protected Environment environment;
    @Resource
    protected BillingFileRepository billingFileRepository;
    @Resource
    protected BOFileRepository boFileRepository;
    @Resource
    protected MessageSource messageSource;
    @Resource
    protected ProcessingFileRepository processingFileRepository;
    @Resource
    protected ProcessingRecordRepository processingRecordRepository;
    @Resource
    protected ProcessingFileRecordRepository processingFileRecordRepository;
    @Resource
    protected SequenceService sequenceService;
    @Resource
    protected ApplicationService applicationService;

    @Test
    public void testMessageSource() {
        String s1 = messageSource.getMessage("file.upload.alreadyUploaded", new Object[]{"test", "ARC", "2:asd"}, Locale.getDefault());
        assertNotNull(s1);
        System.out.println(s1);


        String s = messageSource.getMessage("billing.converter.APLC.empty", new Object[]{}, Locale.getDefault());
        assertNotNull(s);
        System.out.println(s);
    }

    @Test
    public void testExist() {
        assertNotNull(applicationContext);
        assertNotNull(dataSource);
        assertNotNull(entityManager);
        System.out.println("driverClassName = " + driverClassName);
        assertTrue(driverClassName.equals(environment.getRequiredProperty("database.driverClassName")));
    }

    protected BillingFile create(File file) {

        BillingFile billingFile = new BillingFile();
        billingFile.setName(file.getName());
        billingFile.setBusinessDate(new Date());
        billingFile.setCountLines(11);
        billingFile.setFormat(BillingFileFormat.ARC);
        billingFile.setProcessingDate(new Date());
        billingFile.setOriginalFileName(file.getName());
        billingFile.setOriginalFile(file);

        return billingFileRepository.save(billingFile);
    }

    protected BOFile createBoFile(File file) {
        BOFile bf = new BOFile();
        bf.setName(file.getName());
        bf.setBusinessDate(new Date());

        bf.setOriginalFile(file);

        return boFileRepository.save(bf);
    }

    protected ProcessingFile createProcessingFile(BillingFile billingFile, FileType billingSimpleFileType) {
        ProcessingFile file = new ProcessingFile();
        file.setParentFile(billingFile);
        file.setCreatedDate(new Date());
        file.setName(billingFile.getName() + "_simple");
        file.setFileType(billingSimpleFileType);
        file.setParentFile(null);

        return processingFileRepository.save(file);
    }
}
