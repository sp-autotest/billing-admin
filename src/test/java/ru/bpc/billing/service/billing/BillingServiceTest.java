package ru.bpc.billing.service.billing;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.jsefa.flr.FlrDeserializer;
import org.jsefa.flr.FlrIOFactory;
import org.jsefa.flr.FlrSerializer;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import ru.bpc.billing.AbstractTest;
import ru.bpc.billing.controller.dto.PostingDto;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.domain.billing.BillingFileUploadRequest;
import ru.bpc.billing.domain.billing.bsp.IBR;
import ru.bpc.billing.domain.posting.PostingFile;
import ru.bpc.billing.domain.posting.PostingRecord;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;
import ru.bpc.billing.exception.FileUploadException;
import ru.bpc.billing.repository.CarrierRepository;
import ru.bpc.billing.service.PostingService;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 10:49
 */
public class BillingServiceTest extends AbstractTest {

    @Resource
    private BillingService billingService;
    @Resource
    private Map<BillingFileFormat,BillingConverter> billingConverters;
    @Resource
    private CarrierRepository carrierRepository;
    @Resource
    private PostingService postingService;

    @Test
    public void testExists() {
        assertNotNull(billingService);
        assertNotNull(billingConverters);
        for (Map.Entry<BillingFileFormat, BillingConverter> entry : billingConverters.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    @Test
    public void testConvert() throws IOException {
        BillingFile billingFile = create(new File("D:\\3\\real\\alfabk20140530204606168.20140530"));
        BillingConverterResult billingConverterResult = billingService.convert(new HashSet<>(Arrays.asList(billingFile)));
        assertNotNull(billingConverterResult);
        System.out.println(billingFile.depositCount + ":" + billingFile.refundCount + ":" + billingFile.reverseCount + ":" + billingFile.allRecordWithoutNotFinancialOperationCount);

    }

    @Test
    public void testConvert2() throws Exception {
        BillingFile billingFile = billingFileRepository.findOne(48L);
        assertNotNull(billingFile);
        BillingConverterResult result = billingService.convert(new HashSet<>(Arrays.asList(billingFile)));
        assertNotNull(result);
    }

    @Test
    public void testConvertString() throws Exception {
        String s = "IBR00000004    41407144392014071400000000021540AUD2DBP4201407140000000010000000000000002301563AU0000001MC5163104000403289   0915R40285  5555222731903   20140713    41407144392014071400000000021540AUD2STELLA TRAVEL SERVICES                  SIEMER/CHRISTIAN MR                                                         27JULI     S0712MCWISQAKA  -00-    -                                                             00000000000000                   FO";
        FlrSerializer rejectRecordSerializer = FlrIOFactory.createFactory(SvPostingRecord.class).createSerializer();

        FlrDeserializer flrDeserializer = FlrIOFactory.createFactory(IBR.class).createDeserializer();
        flrDeserializer.open(new StringReader(s));
        IBR ibr = null;
        while (flrDeserializer.hasNext() ) {
            ibr = (IBR)flrDeserializer.next();
            System.out.println(ibr);
        }
        rejectRecordSerializer.write(ibr);
        rejectRecordSerializer.flush();

    }

    @Test
    public void testCheck() throws Exception {
        BillingFile billingFile = create(new File("D:\\3\\real\\alfabk20140530204606168.20140530"));
        BillingFileUploadRequest billingFileUploadRequest = new BillingFileUploadRequest();
        billingFile = billingService.upload(billingFileUploadRequest);
        assertNotNull(billingFile);
        System.out.println(billingFile);
    }

    @Test
    public void testBspUploadAndConvert() throws Exception {
        File file = new File("F:\\3\\real\\alphabank_20140530_000120140530202044956");
        BillingFileUploadRequest billingFileUploadRequest = new BillingFileUploadRequest();
        BillingFileFormat billingFileFormat = billingService.determineFileFormat(file);
        assertNotNull(billingFileFormat);

        BillingConverter billingConverter = billingConverters.get(billingFileFormat);
        assertNotNull(billingConverter);

        BillingValidateResult billingValidateResult = null;
        try {
            billingValidateResult = billingConverter.validate(file);
        } catch (IOException e) {
            throw new FileUploadException("file.upload.error",new Object[]{file.getName()},file);
        }
        assertNotNull(billingValidateResult);

        File newFile = new File(applicationService.getHomeDir(FileType.BILLING) + file.getName());
        FileUtils.copyFile(file, newFile);

        if ( !billingValidateResult.isSuccess() ) throw new IllegalArgumentException("Error validate");
        Carrier carrier = billingValidateResult.getCarrier();
        if ( null == carrier ) {
            carrier = new Carrier();

            carrier.setIataCode("111111");
            carrier.setCreatedAt(new Date());
            carrier.setName("aeroflot " + new Date().getTime() );
            carrierRepository.save(carrier);
            assertNotNull(carrier.getId());
        }

        BillingFile billingFile = new BillingFile();
        billingFile.setOriginalFile(file);
        billingFile.setFormat(billingFileFormat);
        billingFile.setOriginalFileName(file.getName());
        billingFile.setName(newFile.getName());
        billingFile.setProcessingDate(billingValidateResult.getProcessingDate());
        billingFile.setBusinessDate(billingFileUploadRequest.getBusinessDate());
        billingFile.setCountLines(billingValidateResult.getCountLines());
        billingFile.setCarrier(carrier);
        billingFileRepository.save(billingFile);

        assertNotNull(billingFile.getId());

        BillingConverterResult billingConverterResult = billingService.convert(new HashSet<>(Arrays.asList(billingFile)));
        for (ProcessingFile processingFile : billingConverterResult.getProcessingFiles()) {
            System.out.println(processingFile.getFileType() + ":" + processingFile.getName());
        }
    }

    @Test
    public void testUploadAndConvert() throws Exception {
        File file = new File("F:\\3\\real\\alphabank_20140602_000120140602185532676");

        BillingFileUploadRequest billingFileUploadRequest = new BillingFileUploadRequest();
//        billingFileUploadRequest.setBusinessDate(new Date());
//        DiskFileItem diskFileItem = new DiskFileItem("file","",false,file.getName(),1000,file);
//        billingFileUploadRequest.setFile(new CommonsMultipartFile(diskFileItem));

        //BillingFile billingFile = billingService.upload(billingFileUploadRequest);

        BillingFileFormat billingFileFormat = billingService.determineFileFormat(file);
        assertNotNull(billingFileFormat);

        BillingConverter billingConverter = billingConverters.get(billingFileFormat);
        assertNotNull(billingConverter);

        BillingValidateResult billingValidateResult = null;
        try {
            billingValidateResult = billingConverter.validate(file);
        } catch (IOException e) {
            throw new FileUploadException("file.upload.error",new Object[]{file.getName()},file);
        }
        assertNotNull(billingValidateResult);

        File newFile = new File(applicationService.getHomeDir(FileType.BILLING) + file.getName());
        FileUtils.copyFile(file, newFile);

        if ( !billingValidateResult.isSuccess() ) throw new IllegalArgumentException("Error validate");
        Carrier carrier = billingValidateResult.getCarrier();
        if ( null == carrier ) {
            carrier = new Carrier();

            carrier.setIataCode("111111");
            carrier.setCreatedAt(new Date());
            carrier.setName("aeroflot " + new Date().getTime() );
            carrierRepository.save(carrier);
            assertNotNull(carrier.getId());
        }

        BillingFile billingFile = new BillingFile();
        billingFile.setOriginalFile(file);
        billingFile.setFormat(billingFileFormat);
        billingFile.setOriginalFileName(file.getName());
        billingFile.setName(newFile.getName());
        billingFile.setProcessingDate(billingValidateResult.getProcessingDate());
        billingFile.setBusinessDate(billingFileUploadRequest.getBusinessDate());
        billingFile.setCountLines(billingValidateResult.getCountLines());
        billingFile.setCarrier(carrier);
        billingFileRepository.save(billingFile);

        assertNotNull(billingFile.getId());

        File file1 = new File("F:\\3\\real\\alfabank_2.txt");
        File newFile1 = new File(applicationService.getHomeDir(FileType.BILLING) + file1.getName());
        FileUtils.copyFile(file1, newFile1);

        BillingFile billingFile1 = new BillingFile();
        billingFile1.setOriginalFile(file1);
        billingFile1.setFormat(billingFileFormat);
        billingFile1.setOriginalFileName(file1.getName());
        billingFile1.setName(newFile1.getName());
        billingFile1.setProcessingDate(billingValidateResult.getProcessingDate());
        billingFile1.setBusinessDate(billingFileUploadRequest.getBusinessDate());
        billingFile1.setCountLines(billingValidateResult.getCountLines());
        billingFile1.setCarrier(carrier);
        billingFileRepository.save(billingFile1);

        assertNotNull(billingFile1.getId());

        File file2 = new File("F:\\3\\real\\arc_new.txt");
        File newFile2 = new File(applicationService.getHomeDir(FileType.BILLING) + file2.getName());
        FileUtils.copyFile(file2, newFile2);

        BillingFile billingFile2 = new BillingFile();
        billingFile2.setOriginalFile(file2);
        billingFile2.setFormat(billingFileFormat);
        billingFile2.setOriginalFileName(file2.getName());
        billingFile2.setName(newFile2.getName());
        billingFile2.setProcessingDate(billingValidateResult.getProcessingDate());
        billingFile2.setBusinessDate(billingFileUploadRequest.getBusinessDate());
        billingFile2.setCountLines(billingValidateResult.getCountLines());
        billingFile2.setCarrier(carrier);
        billingFileRepository.save(billingFile2);

        assertNotNull(billingFile2.getId());


        BillingConverterResult billingConverterResult = billingService.convert(new HashSet<>(Arrays.asList(billingFile, billingFile1, billingFile2)));
        /*
        assertNotNull(billingConverterResult);
        for (PostingRecord postingRecord : billingConverterResult.getPostingRecords()) {
            System.out.println(postingRecord.getIataCode() + ":" + postingRecord);
        }
        for (BillingConverterResult converterResult : billingConverterResult.getBillingConverterResults()) {
            System.out.println(converterResult);
            System.out.println(converterResult.getPostingRecords().size());
            //System.out.println(converterResult.getLogStrings());
            for (String str : converterResult.getLogStrings()) {
                System.out.println(str);
            }
            System.out.println(converterResult.getProcessingFiles());
        }
        System.out.println(billingConverterResult.getProcessingFiles());
        */
        for (ProcessingFile processingFile : billingConverterResult.getProcessingFiles()) {
            System.out.println(processingFile.getFileType() + ":" + processingFile.getName());
        }
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testRevertPostingFile() {
        processingFileRepository.findAll().forEach(processingFile -> {
            if ( processingFile instanceof PostingFile ) {
                PostingFile postingFile = (PostingFile)processingFile;
                PostingDto postingDto = new PostingDto(postingFile);
                postingService.revert(postingDto.getId());
            }
        });
    }
}
