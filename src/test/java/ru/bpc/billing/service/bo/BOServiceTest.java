package ru.bpc.billing.service.bo;

import junit.framework.TestCase;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import ru.bpc.billing.controller.dto.BoDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BOServiceTest extends TestCase {

    @Test
    public void testFillDtoOld() throws IOException {
        BOService boService = new BOService();
        ReflectionTestUtils.setField(boService, "defaultCarrierIataCode", "SU");
        BoDto boDto = new BoDto();
        Resource resource = new ClassPathResource("bo/BSPOLD-20170330");
        assertTrue(resource.exists());
        File file = resource.getFile();
        Path path = Paths.get(file.toURI());
        FileTime createdDate = (FileTime) Files.getAttribute(path, "creationTime");
        Long size = (Long)Files.getAttribute(path, "size");
        boService.fillDto(boDto,file);

        assertEquals(file.getName(),boDto.getFileName());
        assertEquals(new Date(createdDate.toMillis()), boDto.getCreatedDate());
        assertEquals(size, boDto.getSize());
        assertEquals("SU",boDto.getIataCode());
    }


    @Test
    public void testFillDtoNew() throws IOException {
        BOService boService = new BOService();
        BoDto boDto = new BoDto();
        Resource resource = new ClassPathResource("bo/BSP_NEW-20161111180902_SU");
        assertTrue(resource.exists());
        File file = resource.getFile();
        Path path = Paths.get(file.toURI());
        FileTime createdDate = (FileTime) Files.getAttribute(path, "creationTime");
        Long size = (Long)Files.getAttribute(path, "size");
        boService.fillDto(boDto,file);

        assertEquals(file.getName(),boDto.getFileName());
        assertEquals(new Date(createdDate.toMillis()), boDto.getCreatedDate());
        assertEquals(size, boDto.getSize());
        assertEquals("SU",boDto.getIataCode());
    }

    @Test
    public void testBuildUploadFileName() {
        Date date = new Date();
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);

        String filename = "BSP20150921173535_SU";
        String fullname = getFullName(filename, date);
        assertEquals("BSP20150921173535-" + timestamp + "_SU", fullname);

        String filename2 = "BSP20150921173535";
        String fullname2 = getFullName(filename2, date);
        assertEquals("BSP20150921173535-" + timestamp, fullname2);
    }

    private String getFullName(String filename, Date date) {
        String name = FilenameUtils.getName(filename);
        String iataCode = StringUtils.substringAfterLast(name, "_");
        String nameBeforeIataCode = StringUtils.substringBeforeLast(name, "_");
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
        String fullname = nameBeforeIataCode + "-" + timestamp + (StringUtils.isNotBlank(iataCode) ? "_" + iataCode : "");
        return fullname;
    }

}
