package ru.bpc.billing.repository;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.bpc.billing.domain.*;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.domain.posting.PostingFile;
import ru.bpc.billing.domain.posting.PostingFileFormat;
import ru.bpc.billing.domain.posting.PostingRecord;
import ru.bpc.billing.domain.posting.PostingRecordType;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;
import ru.bpc.billing.AbstractTest;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User: Krainov
 * Date: 13.08.14
 * Time: 17:09
 */
public class ProcessingRecordRepositoryTest extends AbstractTest {



    @Test
    public void testExist() {
        assertNotNull(billingFileRepository);
        assertNotNull(processingRecordRepository);
    }

    @Test
    public void testManyToMany() {
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


        PostingFile postingFile = new PostingFile();
        postingFile.setName("test_posting_file");
        postingFile.setCreatedDate(new Date());
        postingFile.setFileType(FileType.POSTING);
        postingFile.setFormat(PostingFileFormat.SUCCESS);
        postingFile.setOriginalFileName("test_original_posting_file.txt");
        postingFile.setParentFile(billingFile);



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


        postingRecord.setNetworkRefNumber("4444444");
        postingRecord.setRbsId(UUID.randomUUID().toString());
        ProcessingRecord processingRecord2 = createPostingRecord(postingRecord);

//        billingFile.addProcessingRecord(processingRecord1);
//        billingFile.addProcessingRecord(processingRecord2);
//        billingFile2.addProcessingRecord(processingRecord1);
//        billingFile2.addProcessingRecord(processingRecord2);

//        billingFile.getRecords().add(processingRecord1);
//        billingFile.getRecords().add(processingRecord2);
//        billingFile2.getRecords().add(processingRecord1);
//        billingFile2.getRecords().add(processingRecord2);

        processingFileRepository.save(billingFile);
        processingFileRepository.save(billingFile2);
        processingFileRepository.save(postingFile);

    }

    @Test
    @Transactional
    public void testGetRecordsByFile() {
        ProcessingFile processingFile = processingFileRepository.findOne(47L);
        assertNotNull(processingFile);
        for (ProcessingRecord processingRecord : processingFile.getRecords()) {
            System.out.println(processingRecord.getId());
        }
    }

    @Test
    @Transactional
    public void testGetFilesByRecord() {
        ProcessingRecord processingRecord = processingRecordRepository.findOne(23L);
        assertNotNull(processingRecord);
        for (ProcessingFile processingFile : processingRecord.getFiles()) {
            System.out.println(processingFile.getId());
        }
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testRemoveFile() {
        ProcessingFile processingFile = processingFileRepository.findOne(46L);
        assertNotNull(processingFile);
        for (ProcessingFile file : processingFile.getFiles()) {
            processingFileRepository.delete(file);
        }
        processingFileRepository.delete(processingFile);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testAddRecordToExitFile() {
        SvPostingRecord postingRecord = new SvPostingRecord();
        postingRecord.setSvfeSystemDate("1111111");
        postingRecord.setNetworkRefNumber("6666663333");
        postingRecord.setActionCode("DR");
        postingRecord.setInvoiceNumber("2222222");
        postingRecord.setInvoiceDate("555555");
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

        ProcessingFile billingFile = processingFileRepository.findOne(47L);

        processingRecord1.getFiles().add(billingFile);
        processingRecordRepository.save(processingRecord1);
        //billingFile.addProcessingRecord(processingRecord1);
        billingFile.getRecords().add(processingRecord1);


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

        ProcessingRecord processingRecord = addBillingFileRecordToDatabase(postingRecord,"posting_file_name",billingFile);
        assertNotNull(processingRecord);
        assertNotNull(processingRecord.getId());
    }

    @Test
    public void testFindByDocumentParams() {
        ProcessingRecord processingRecord = create();

        ProcessingRecord processingRecord1 = processingRecordRepository.findByDocumentParams(processingRecord.getDocumentDate(), processingRecord.getDocumentNumber(), processingRecord.getTransactionType());
        assertNotNull(processingRecord1);
        assertTrue(processingRecord.getId().equals(processingRecord1.getId()));
    }

    @Test
    public void testFindByRbsId() {
        ProcessingRecord processingRecord = create();

        ProcessingRecord processingRecord1 = processingRecordRepository.findByRbsId(processingRecord.getRbsId());
        assertNotNull(processingRecord1);
        assertTrue(processingRecord.getId().equals(processingRecord1.getId()));
    }

    private ProcessingRecord create() {
        BillingFile billingFile = new BillingFile();
        billingFile.setName("test_billing_file");
        billingFile.setBusinessDate(new Date());
        billingFile.setCountLines(11);
        billingFile.setFormat(BillingFileFormat.ARC);
        billingFile.setProcessingDate(new Date());
        billingFile.setOriginalFileName("original_file_name");

        billingFileRepository.save(billingFile);

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

        ProcessingRecord processingRecord = addBillingFileRecordToDatabase(postingRecord,"posting_file_name",billingFile);
        assertNotNull(processingRecord);
        assertNotNull(processingRecord.getId());

        return processingRecord;
    }

    private ProcessingRecord addBillingFileRecordToDatabase(PostingRecord postingRecord, String postingFilename, BillingFile billingFile) {
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
//        processingRecord.setPostingFileName(postingFilename);
//        processingRecord.setBillingFile(billingFile);

        return processingRecordRepository.save(processingRecord);
    }

    @Test
    public void testFindOriginalProcessingRecordBy() {
        ProcessingRecord processingRecord = new ProcessingRecord();
        processingRecord.setDocumentNumber("1");
        processingRecord.setAmount(1);
        processingRecord.setCurrency("643");
        processingRecord.setTransactionType(TransactionType.DR);
        processingRecordRepository.save(processingRecord);

        ProcessingRecord processingRecord2 = new ProcessingRecord();
        processingRecord2.setDocumentNumber("1");
        processingRecord2.setAmount(1);
        processingRecord2.setCurrency("643");
        processingRecord2.setTransactionType(TransactionType.CR);
        processingRecordRepository.save(processingRecord2);

        List<ProcessingRecord> processingRecord3 = processingRecordRepository.findOriginalProcessingRecordBy("1",1,"643");
        System.out.println(processingRecord3);
    }
}
