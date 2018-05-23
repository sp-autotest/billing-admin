package ru.bpc.billing.service;

import org.jsefa.Deserializer;
import org.jsefa.flr.FlrIOFactory;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.bpc.billing.AbstractTest;
import ru.bpc.billing.domain.ProcessingFileRecord;
import ru.bpc.billing.domain.ProcessingFileRecordPk;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.ProcessingStatus;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.posting.PostingRecord;
import ru.bpc.billing.domain.posting.PostingRecordType;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;
import ru.bpc.billing.repository.ProcessingRecordFilter;
import ru.bpc.billing.service.billing.ProcessingRecordBuilderResult;

import java.io.File;
import java.io.FileReader;
import java.util.Date;

/**
 * User: Krainov
 * Date: 17.09.2014
 * Time: 12:40
 */
public class PostingTest extends AbstractTest {

    @Test
    public void testCreateRecordsByPostingFile() throws Exception {
        BillingFile billingFile = (BillingFile)processingFileRepository.findOne(174L);
        assertNotNull(billingFile);
        File postingFile = new File("D:\\Users\\krainov\\Documents\\work\\bpc\\rbs-web\\bsp\\bugs\\3539\\postingARC20140717200321891");
        String countryCode = "US";

        Deserializer deserializerPosting = FlrIOFactory.createFactory(SvPostingRecord.class).createDeserializer();
        FileReader fileReaderPosting = new FileReader(postingFile);
        deserializerPosting.open(fileReaderPosting);

        int i =0;
        while (deserializerPosting.hasNext()) {
            SvPostingRecord postingRecord = deserializerPosting.next();

            if ( null == postingRecord.getBerTlvData() ) continue;
            postingRecord.setRbsId(getRbsId(postingRecord.getBerTlvData()));
            postingRecord.setInvoiceNumber(getInvoiceNumber(postingRecord.getBerTlvData()));
            if ( null == postingRecord.getDocumentNumber() ) continue;

            postingRecord.setCountryCode(countryCode);//только для ARC, так как для них тока америка, если БСП, то надо брать из биллинга данные, см. код в след тесте
            ProcessingRecordBuilderResult result = addRecordIfNotExists(postingRecord,billingFile);
            i++;
            System.out.println(i + " = " + result.getStatus());
        }
    }

    private String getRbsId(String s) {
        int i = s.indexOf("DF8556") + "DF8556".length() + 2;
        return s.substring(i,i+36);
    }

    private String getInvoiceNumber(String s) {
        return s.substring(s.indexOf("DF8559")+ "DF8559".length()+2,s.indexOf("DF8556"));
    }

    protected ProcessingRecordBuilderResult addRecordIfNotExists(PostingRecord postingRecord, BillingFile billingFile) {
        ProcessingRecordBuilderResult result = new ProcessingRecordBuilderResult();
        try {
            ProcessingRecordFilter filter = new ProcessingRecordFilter(postingRecord.getDocumentDate(), postingRecord.getDocumentNumber(), postingRecord.getTransactionType());
            ProcessingRecord processingRecord = processingRecordRepository.findOne(filter);
            if ( null == processingRecord) {// record not exists
                result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.NEW);
                processingRecord = createBillingFileRecordFromPostingData(postingRecord);
                result.setProcessingRecord(processingRecord);
                processingRecordRepository.save(processingRecord);
                processingFileRecordRepository.save(new ProcessingFileRecord(new ProcessingFileRecordPk(billingFile,processingRecord)));
                if ( null != postingRecord.getPostingFile() ) processingFileRecordRepository.save(new ProcessingFileRecord(new ProcessingFileRecordPk(postingRecord.getPostingFile(),processingRecord)));
            }
            else {
                result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.EXIST);
                result.setProcessingRecord(processingRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus(ProcessingRecordBuilderResult.ProcessingRecordStatus.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    protected ProcessingRecord createBillingFileRecordFromPostingData(PostingRecord postingRecord) {
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
