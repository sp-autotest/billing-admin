package ru.bpc.billing.repository;

import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.AbstractTest;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: Krainov
 * Date: 13.08.14
 * Time: 16:55
 */
public class BillingFileRepositoryTest extends AbstractTest {


    @Test
    public void testExists() throws Exception {
        assertNotNull(billingFileRepository);
    }

    @Test
    public void testCreate() {
        BillingFile billingFile = new BillingFile();
        billingFile.setName("test_billing_file");
        billingFile.setBusinessDate(new Date());
        billingFile.setCountLines(11);
        billingFile.setFormat(BillingFileFormat.ARC);
        billingFile.setProcessingDate(new Date());
        billingFile.setOriginalFileName("original_file_name");

        billingFileRepository.save(billingFile);

        assertNotNull(billingFile.getId());
    }

    @Test
    public void testFindByNameAndDate() {
        BillingFile billingFile = create(new File("D:\\3\\real\\alfabk20140530204606168.20140530"));
        assertNotNull(billingFile.getId());

        ProcessingFileFilter filter = new ProcessingFileFilter();
        filter.setMode(SearchMode.BY_BILLING);
        Calendar from = Calendar.getInstance();
        from.add(Calendar.DAY_OF_MONTH,-10);
        filter.setFromCreateDate(from.getTime());
        filter.setToCreateDate(new Date());
        filter.setFilename(billingFile.getName());

        List<ProcessingFile> billingFiles = processingFileRepository.findAll(filter);
        assertNotNull(billingFiles);

        for (ProcessingFile file : billingFiles) {
            System.out.println(file);
        }
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void testFindByLinkedFile() {
        ProcessingFileFilter filter = new ProcessingFileFilter();
        filter.setMode(SearchMode.BY_LINKED);
        Calendar from = Calendar.getInstance();
        from.add(Calendar.DAY_OF_MONTH,-10);
        filter.setFromCreateDate(from.getTime());
        filter.setToCreateDate(new Date());
        filter.setFilename("alfabk20140530204606168.20140530_simple");
        filter.setFileType(FileType.BO_REVENUE_LOG);

        List<ProcessingFile> billingFiles = processingFileRepository.findAll(filter);
        assertNotNull(billingFiles);
        for (ProcessingFile billingFile : billingFiles) {
            System.out.println(billingFile);
        }
    }

    @Test
    public void testFindByProcessingDate() {
        ProcessingFileFilter filter = new ProcessingFileFilter();
        filter.setMode(SearchMode.BY_LINKED);
        Calendar from = Calendar.getInstance();
        from.add(Calendar.DAY_OF_MONTH,-10);
        filter.setFromCreateDate(from.getTime());
        filter.setToCreateDate(new Date());
        filter.setFilename("alfabk20140530204606168.20140530_simple");
        filter.setFileType(FileType.BO);
        filter.setCountLines(11);

        Sort sort = new Sort(Sort.Direction.ASC,"id");
        List<ProcessingFile> billingFiles = processingFileRepository.findAll(filter, sort);
        assertNotNull(billingFiles);
        for (ProcessingFile billingFile : billingFiles) {
            System.out.println(billingFile);
        }
    }

    @Test
    @Transactional
    public void testSimpleFiles() {
        BillingFile billingFile1 = billingFileRepository.findOne(25L);
        assertNotNull(billingFile1.getFiles());
        System.out.println("size = " + billingFile1.getFiles().size());

    }
}
