package ru.bpc.billing.service.bo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.bpc.billing.controller.dto.BoDto;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.bo.BOFileUploadRequest;
import ru.bpc.billing.exception.FileUploadException;
import ru.bpc.billing.repository.CarrierRepository;
import ru.bpc.billing.repository.ProcessingFileFilter;
import ru.bpc.billing.service.ApplicationService;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BOService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private ApplicationService applicationService;
    @Resource
    private CarrierRepository carrierRepository;
    @Value("${carrier.default.iataCode:SU}")
    private String defaultCarrierIataCode;

    public File upload(BOFileUploadRequest boFileUploadRequest) throws FileUploadException {
        return applicationService.uploadFile(applicationService.getHomeDir(FileType.BO), boFileUploadRequest.getFile(),
                multipartFile -> {
                    String filename = multipartFile.getOriginalFilename();
                    String name = FilenameUtils.getName(filename);
                    if ( StringUtils.countMatches(name,"_") > 1 ) {
                        throw new RuntimeException("Некорректное имя файла (символ '_' встречается больше 1 раза)");
                    }
                    String iataCode = StringUtils.substringAfterLast(name, "_");
                    String nameBeforeIataCode = StringUtils.substringBeforeLast(name,"_");
                    String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    return nameBeforeIataCode + "-" + timestamp + (StringUtils.isNotBlank(iataCode) ? "_" + iataCode : "");
                });
    }

    public BoDto prepareBos(final ProcessingFileFilter filter) {

        String requiredIataCode = null;
        if (filter.getCarrierId() != null){
            Carrier carrier = carrierRepository.findOne(filter.getCarrierId());
            if (carrier != null){
                requiredIataCode = carrier.getIataCode();
            }
        }
        final String finalRequiredIataCode = requiredIataCode;

        IOFileFilter fileFilter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                boolean accept = true;
                if (StringUtils.isNotEmpty(filter.getFilename()) ) {
                    if ( filter.getFilename().startsWith("%") ) {
                        accept = file.getName().toLowerCase().endsWith(filter.getFilename().toLowerCase().substring(1));
                    }
                    else if ( filter.getFilename().endsWith("%") ) {
                        accept = file.getName().toLowerCase().startsWith(filter.getFilename().toLowerCase().substring(0,filter.getFilename().length()-1));
                    }
                    else {
                        accept = file.getName().toLowerCase().equals(filter.getFilename().toLowerCase());
                    }
                }
                if ( null != filter.getFromCreateDate() ) {
                    accept = accept & FileUtils.isFileNewer(file,filter.getFromCreateDate());
                }
                if ( null != filter.getToCreateDate() ) {
                    accept = accept & FileUtils.isFileOlder(file,filter.getToCreateDate());
                }
                if ( null != finalRequiredIataCode) {     //Имя файла должно формироваться следующим образом <BSP><YYYYMMDD><HH24MISS>_<IATA>
                    accept = accept & file.getName().toLowerCase().endsWith("_" + finalRequiredIataCode);
                }
                return accept;
            }

            @Override
            public boolean accept(File dir, String name) {
                return false;
            }
        };
        List<BoDto> boDtos = new ArrayList<>();
        Collection<File> files = FileUtils.listFiles(new File(applicationService.getHomeDir(FileType.BO)),fileFilter,null);
        for (File file : files) {
            if ( !file.exists() || !file.canRead() ) continue;
            BoDto boDto = new BoDto();
            fillDto(boDto,file);
            boDtos.add(boDto);
        }
        BoDto dto = new BoDto();
        dto.setChildren(boDtos);
        dto.setSuccess(true);
        return dto;
    }

    public void fillDto(BoDto boDto, File file) {
        boDto.setFileName(file.getName());
        Path path = Paths.get(file.toURI());
        try {
            FileTime createdDate = (FileTime) Files.getAttribute(path, "creationTime");
            Long size = (Long)Files.getAttribute(path, "size");
            boDto.setCreatedDate(new Date(createdDate.toMillis()));
            boDto.setSize(size);
            if (StringUtils.countMatches(file.getName(), "_") >= 1){
                boDto.setIataCode(StringUtils.substringAfterLast(file.getName(), "_"));
            }
            else {
                boDto.setIataCode(defaultCarrierIataCode);
            }
        } catch (IOException e) {
            logger.error("Error get 'creationTime' or 'size' attributes for file: " + file,e);
        }
    }

    public static void main(String[] args) {
        BOService boService = new BOService();
        boService.applicationService = new ApplicationService();
        //boService.applicationService.setApplicationHomeDir("d:/tmp/bsp-admin/");
        boService.applicationService.init();

        Calendar from = Calendar.getInstance();
        from.set(Calendar.MONTH,Calendar.SEPTEMBER);
        from.set(Calendar.DAY_OF_MONTH,4);
        from.set(Calendar.HOUR_OF_DAY,0);
        Calendar to = Calendar.getInstance();
        to.set(Calendar.MONTH, Calendar.SEPTEMBER);
        to.set(Calendar.DAY_OF_MONTH, 4);
        ProcessingFileFilter filter = new ProcessingFileFilter("%7",null,null);
        BoDto dto = boService.prepareBos(filter);
        for (BoDto boDto : dto.getChildren()) {
            System.out.println(boDto.getFileName());
        }
    }
}
