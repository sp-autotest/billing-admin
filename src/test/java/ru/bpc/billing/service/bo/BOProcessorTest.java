package ru.bpc.billing.service.bo;

import org.junit.Test;
import ru.bpc.billing.AbstractTest;
import ru.bpc.billing.domain.ProcessingFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 12:31
 */
public class BOProcessorTest extends AbstractTest {

    @Resource
    private BOProcessor boProcessor;

    @Test
    public void testExist() {
        assertNotNull(boProcessor);
    }

    @Test
    public void testProcess() throws IOException {

        //File boFile = new File("D:\\tmp\\revenue\\bo\\BSP_20140802_182829_corr");
        File boFile = new File("D:\\tmp\\bsp-admin\\bo\\ARC_20140719_193006_corr20140917125913270");
        BOProcessingResult result = boProcessor.process(boFile);
        assertNotNull(result);
        System.out.println(result);


    }
}
