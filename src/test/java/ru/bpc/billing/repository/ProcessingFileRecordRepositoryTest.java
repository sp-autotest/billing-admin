package ru.bpc.billing.repository;

import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.csv.CsvDeserializer;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.flr.FlrDeserializer;
import org.jsefa.flr.FlrIOFactory;
import org.junit.Test;
import ru.bpc.billing.AbstractTest;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.ProcessingStatus;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.domain.posting.PostingFile;
import ru.bpc.billing.domain.posting.PostingFileFormat;
import ru.bpc.billing.domain.posting.PostingRecord;
import ru.bpc.billing.domain.posting.PostingRecordType;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;
import ru.bpc.billing.util.BillingFileUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * User: Krainov
 * Date: 12.04.2016
 * Time: 14:24
 */
public class ProcessingFileRecordRepositoryTest extends AbstractTest {

    @Resource
    private ProcessingFileRecordRepository processingFileRecordRepository;
    @Resource
    private ProcessingRecordRepository processingRecordRepository;
    @Resource
    private ProcessingFileRepository processingFileRepository;

    @Test
    public void testDeleteRecordsByFile() {
        BillingFile billingFile = new BillingFile();
        billingFile.setName("test_billing_file");
        billingFile.setBusinessDate(new Date());
        billingFile.setCountLines(11);
        billingFile.setFormat(BillingFileFormat.ARC);
        billingFile.setProcessingDate(new Date());
        billingFile.setOriginalFileName("original_file_name");

        processingFileRepository.save(billingFile);

        PostingFile postingFile = new PostingFile();
        postingFile.setName("test_posting_file");
        postingFile.setCreatedDate(new Date());
        postingFile.setFileType(FileType.POSTING);
        postingFile.setFormat(PostingFileFormat.SUCCESS);
        postingFile.setOriginalFileName("test_original_posting_file.txt");
        postingFile.setParentFile(billingFile);

        processingFileRepository.save(postingFile);

        List<ProcessingRecord> processingRecords = new ArrayList<>();
        IntStream.range(0,10).forEach(idx -> {
            SvPostingRecord postingRecord = new SvPostingRecord();
            postingRecord.setSvfeSystemDate("1111111");
            postingRecord.setNetworkRefNumber(String.valueOf(3333333 + idx));
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

            ProcessingRecord processingRecord = createPostingRecord(postingRecord);
            processingRecords.add(processingRecord);
        });

        for (ProcessingRecord processingRecord : processingRecords) {
            processingRecord.getFiles().add(billingFile);
            processingRecord.getFiles().add(postingFile);



            processingRecordRepository.save(processingRecord);
        }

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
    public void testCreatePostingRecordsFromFile() throws FileNotFoundException {
        CsvConfiguration csvConfiguration = new CsvConfiguration();
        csvConfiguration.setFieldDelimiter(';');
        csvConfiguration.setLineFilter(new HeaderAndFooterFilter(1,false,false));//skip first header line
        CsvDeserializer deserializer = CsvIOFactory.createFactory(csvConfiguration, CSVProcessingRecord.class).createDeserializer();
//        Reader reader = new FileReader(new File("F:\\Users\\krainov\\Documents\\work\\bpc\\rbs-web\\bsp\\bugs\\7100\\processing_record.csv"));
        Reader reader = new FileReader(new File("F:\\Users\\krainov\\Documents\\work\\bpc\\rbs-web\\bsp\\bugs\\7100\\09_11.csv"));
        deserializer.open(reader);
        int count = 0;
        while (deserializer.hasNext()) {
            CSVProcessingRecord csvProcessingRecord = deserializer.next();
            ProcessingRecord processingRecord = processingRecordRepository.findByDocumentNumber(csvProcessingRecord.DOCUMENT_NUMBER).stream().findFirst().orElse(null);
            if ( null == processingRecord ) {
                System.out.println("skip: " + csvProcessingRecord);
                continue;
            }
            processingRecord.setRbsId(csvProcessingRecord.RBS_ID);
            processingRecordRepository.save(processingRecord);
            System.out.println(csvProcessingRecord.RBS_ID + " ok");
            count++;
        }
        System.out.println("OK files: " + count);
        deserializer.close(true);
    }

    @Test
    public void testReadPostingFile() throws Exception {
        FlrDeserializer deserializer = FlrIOFactory.createFactory(SvPostingRecord.class).createDeserializer();
        Reader reader = new FileReader(new File("F:\\Users\\krainov\\Documents\\work\\bpc\\rbs-web\\bsp\\bugs\\7040\\files\\postingBSP20161024192328062"));
        deserializer.open(reader);
        int count = 0;
        while (deserializer.hasNext()) {
            String record = BillingFileUtils.getCurrentLine(deserializer);
            if ( record.startsWith("1") || record.startsWith("@") ) {
                System.out.println("not record");
                deserializer.next();
                continue;
            }
            SvPostingRecord svPostingRecord = deserializer.next();
            ProcessingRecord processingRecord = processingRecordRepository.findByDocumentNumber(svPostingRecord.getDocumentNumber()).stream().findFirst().orElse(null);
            if (null == processingRecord) {
                System.out.println("skip: " + svPostingRecord);
                continue;
            }
            processingRecord.setRbsId(BillingFileUtils.getRbsIdFromBerTlvData(svPostingRecord.getBerTlvData()));
            processingRecordRepository.save(processingRecord);
            System.out.println(processingRecord.getRbsId() + " ok");
            count++;
        }
        System.out.println("OK files: " + count);
        deserializer.close(true);


    }
}
