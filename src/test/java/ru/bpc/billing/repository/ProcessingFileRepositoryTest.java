package ru.bpc.billing.repository;

import org.junit.Test;
import ru.bpc.billing.AbstractTest;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * User: Krainov
 * Date: 04.09.2014
 * Time: 13:53
 */
public class ProcessingFileRepositoryTest extends AbstractTest {

    @Test
    public void testFindAll() {

        ProcessingFileFilter filter = new ProcessingFileFilter(null,new Date(),new Date());

        List<ProcessingFile> processingFiles = processingFileRepository.findAll(filter);
        assertNotNull(processingFiles);
    }

    @Test
    public void testFindById() {
        ProcessingFileFilter filter = new ProcessingFileFilter();
        filter.setId(372L);
        List<ProcessingFile> processingFiles = processingFileRepository.findAll(filter);
        assertNotNull(processingFiles);
        for (ProcessingFile processingFile : processingFiles) {
            System.out.println(processingFile.getClass() + ":" + processingFile.getName());
            if ( processingFile instanceof BillingFile ) {
                System.out.println("this is billing");
            }
        }
    }

    @Test
    public void testByName() {
        ProcessingFileFilter filter = new ProcessingFileFilter();
        filter.setFilename("alfabk.20140717_20140925170036333");
        System.out.println(filter.getFileType());
        List<ProcessingFile> processingFiles = processingFileRepository.findAll(filter);
        assertNotNull(processingFiles);
        for (ProcessingFile processingFile : processingFiles) {
            System.out.println(processingFile.getClass() + ":" + processingFile.getId());
        }
    }

    @Test
    public void testByLikeName() {
        ProcessingFileFilter filter = new ProcessingFileFilter();
        filter.setFilename("report%");
        System.out.println(filter.getFileType());
        List<ProcessingFile> processingFiles = processingFileRepository.findAll(filter);
        assertNotNull(processingFiles);
        for (ProcessingFile processingFile : processingFiles) {
            System.out.println(processingFile.getClass() + ":" + processingFile.getId());
        }
    }
}
