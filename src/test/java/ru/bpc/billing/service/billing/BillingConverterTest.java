package ru.bpc.billing.service.billing;

import org.junit.Test;
import ru.bpc.billing.AbstractTest;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.TransactionType;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.posting.PostingFile;
import ru.bpc.billing.domain.posting.PostingRecord;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * User: Krainov
 * Date: 13.08.14
 * Time: 15:35
 */
public class BillingConverterTest extends AbstractTest{

    @Resource
    private BillingConverter arcBillingConverter;
    @Resource
    private BillingConverter bspBillingConverter;

    @Test
    public void testExists() {
        assertNotNull(arcBillingConverter);
        assertNotNull(bspBillingConverter);
    }

    @Test
    public void testConvert() throws Exception {
        BillingFile billingFile = create(new File("D:\\3\\real\\alfabk20140530204606168.20140530"));
        BillingConverterResult billingConverterResult = arcBillingConverter.convert(billingFile);
        assertNotNull(billingConverterResult);
        System.out.println(billingFile.depositCount + ":" + billingFile.refundCount + ":" + billingFile.reverseCount + ":" + billingFile.allRecordWithoutNotFinancialOperationCount);

    }


    @Test
    public void testSort() {
        List<ProcessingFile> processingFiles = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            PostingFile processingFile = new PostingFile();
            for (int j = 0; j < 100; j++) {
                SvPostingRecord svPostingRecord = new SvPostingRecord();
                if (j % 2 == 0) svPostingRecord.setActionCode("DR");
                else if (j % 3 == 0) svPostingRecord.setActionCode("CR");
                else if (j%4==0) svPostingRecord.setActionCode("CR_REFUND");
                else if (j%5==0) svPostingRecord.setActionCode("UN");
                else svPostingRecord.setActionCode(TransactionType.CR_REVERSE.name());

                processingFile.addPostingRecord(svPostingRecord);
            }
            processingFiles.add(processingFile);
        }

        processingFiles.stream()
                .filter(f -> f instanceof PostingFile)
                .flatMap(p -> ((PostingFile) p).getPostingRecords().stream())
                .sorted(Comparator.comparing(p -> p.getTransactionType(),(o1,o2) -> o2.equals(TransactionType.CR_REVERSE) ? -1 : 1))
                .forEach(postingRecord ->
                                System.out.println(postingRecord.getTransactionType())
                );


        }
}
