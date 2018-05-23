package ru.bpc.billing.domain;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.bpc.billing.AbstractTest;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.domain.posting.PostingRecord;
import ru.bpc.billing.domain.posting.PostingRecordType;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;
import ru.bpc.billing.repository.ProcessingFileRecordRepository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * User: Krainov
 * Date: 08.09.2014
 * Time: 10:26
 */
public class ProcessingTest extends AbstractTest {

    @Resource
    private ProcessingFileRecordRepository processingFileRecordRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testCreate() {
        BillingFile billingFile = new BillingFile();
        billingFile.setName("test_billing_file");
        billingFile.setBusinessDate(new Date());
        billingFile.setCountLines(11);
        billingFile.setFormat(BillingFileFormat.ARC);
        billingFile.setProcessingDate(new Date());
        billingFile.setOriginalFileName("original_file_name");

        BillingFile billingFile2 = new BillingFile();
        billingFile2.setName("test_billing_file2");
        billingFile2.setBusinessDate(new Date());
        billingFile2.setCountLines(11);
        billingFile2.setFormat(BillingFileFormat.ARC);
        billingFile2.setProcessingDate(new Date());
        billingFile2.setOriginalFileName("original_file_name2");

        SvPostingRecord postingRecord = new SvPostingRecord();
        postingRecord.setSvfeSystemDate("1111111");
        postingRecord.setNetworkRefNumber("3333333");
        postingRecord.setActionCode("DR");
        postingRecord.setInvoiceNumber("2222222");
        postingRecord.setInvoiceDate("4444444");
        postingRecord.setTransactionAmount("555");
        postingRecord.setTransactionCurrency("840");
        postingRecord.setCountryCode("US");
        postingRecord.setRbsId(UUID.randomUUID().toString());
        postingRecord.setType(PostingRecordType.SUCCESS);
        postingRecord.setErrorMessage(null);
        postingRecord.setExpirationDate("201412");
        postingRecord.setPan("4012233456678965");
        postingRecord.setApprovalCode("123456");
        postingRecord.setRefNum("777777");
        postingRecord.setPostingFileName("posting_filename");

        ProcessingRecord processingRecord1 = createPostingRecord(postingRecord);


//        processingFileRepository.save(billingFile);
//        processingFileRepository.save(billingFile2);

//        processingRecordRepository.save(processingRecord1);
//        processingFileRepository.save(billingFile);
//        processingFileRepository.save(billingFile2);

        entityManager.persist(processingRecord1);
        entityManager.persist(billingFile);
        entityManager.persist(billingFile2);

        System.out.println(processingRecord1.getId());
        System.out.println(billingFile.getId());
        System.out.println(billingFile2.getId());

        ProcessingFileRecord processingFileRecord = new ProcessingFileRecord(new ProcessingFileRecordPk(billingFile,processingRecord1));
        ProcessingFileRecord processingFileRecord1 = new ProcessingFileRecord(new ProcessingFileRecordPk(billingFile2,processingRecord1));

        billingFile.getProcessingFileRecords().add(processingFileRecord);
        billingFile.getProcessingFileRecords().add(processingFileRecord1);


//        ProcessingFileRecord processingFileRecord = billingFile.addProcessingRecord(processingRecord1);
//        ProcessingFileRecord processingFileRecord1 = billingFile2.addProcessingRecord(processingRecord1);

//        billingFile.getProcessingFileRecords().add(processingFileRecord);
//        billingFile.getProcessingFileRecords().add(processingFileRecord1);

//        processingFileRecordRepository.save(processingFileRecord);
//        processingFileRecordRepository.save(processingFileRecord1);
        entityManager.persist(processingFileRecord);
        entityManager.persist(processingFileRecord1);

//        entityManager.persist(billingFile.addProcessingRecord(processingRecord1));
//        entityManager.persist(billingFile2.addProcessingRecord(processingRecord1));



    }

    @Test
    @Transactional
    public void testGet() {
        ProcessingRecord processingRecord = processingRecordRepository.findOne(51L);
        assertNotNull(processingRecord);
        for (ProcessingFile processingFile : processingRecord.getFiles()) {
            System.out.println(processingFile.getId());
        }
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testCreate1() {

        ProcessingRecord item1 = new ProcessingRecord();
        item1.setRbsId("item1");

        ProcessingRecord item2 = new ProcessingRecord();
        item2.setRbsId("item2");

        ProcessingRecord item3 = new ProcessingRecord();
        item3.setRbsId("item3");


        ProcessingFile product1 = new ProcessingFile();
        product1.setName("product1");

        ProcessingFile product2 = new ProcessingFile();
        product2.setName("product2");

        ProcessingFile product3 = new ProcessingFile();
        product3.setName("product3");





        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.persist(item1);

        System.out.println(product1.getId());
        System.out.println(product2.getId());
        System.out.println(product3.getId());
        System.out.println(item1.getId());

        ProcessingFileRecord productItem1 = new ProcessingFileRecord(new ProcessingFileRecordPk(item1,product1));
        item1.getProcessingFileRecords().add(productItem1);
        ProcessingFileRecord productItem2 = new ProcessingFileRecord(new ProcessingFileRecordPk(item1,product2));
        item1.getProcessingFileRecords().add(productItem2);
        ProcessingFileRecord productItem3 = new ProcessingFileRecord(new ProcessingFileRecordPk(item1,product3));
        item1.getProcessingFileRecords().add(productItem3);

        entityManager.persist(productItem1);
        entityManager.persist(productItem2);
        entityManager.persist(productItem3);
    }

    private ProcessingRecord createPostingRecord(PostingRecord postingRecord) {
        ProcessingRecord processingRecord = new ProcessingRecord();
        processingRecord.setDocumentDate(postingRecord.getDocumentDate());
        processingRecord.setDocumentNumber(postingRecord.getDocumentNumber());
        processingRecord.setTransactionType(postingRecord.getTransactionType());
        processingRecord.setCreatedAt(new Date());
        processingRecord.setInvoiceNumber(postingRecord.getInvoiceNumber());
        processingRecord.setInvoiceDate(postingRecord.getInvoiceDate());
        processingRecord.setAmount(Integer.parseInt(postingRecord.getTransactionAmount()));
        processingRecord.setCurrency(postingRecord.getTransactionCurrency());
        processingRecord.setCountryCode(postingRecord.getCountryCode());
        processingRecord.setRbsId(postingRecord.getRbsId());
        processingRecord.setStatus(postingRecord.getType().equals(PostingRecordType.SUCCESS) ? ProcessingStatus.SUCCESS : ProcessingStatus.REJECT_BILLING);
        processingRecord.setErrorMessage(postingRecord.getErrorMessage());
        processingRecord.setExpiry(postingRecord.getExpirationDate());
        processingRecord.setPan(postingRecord.getPan());
        processingRecord.setApprovalCode(postingRecord.getApprovalCode());
        processingRecord.setRefNum(postingRecord.getRefNum());

        return processingRecord;
    }
}
