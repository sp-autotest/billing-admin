package ru.bpc.billing.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bpc.billing.controller.dto.PostingDto;
import ru.bpc.billing.controller.dto.RevertPostingFileResultDto;
import ru.bpc.billing.domain.ProcessingFile;
import ru.bpc.billing.domain.ProcessingRecord;
import ru.bpc.billing.domain.billing.BillingFile;
import ru.bpc.billing.domain.posting.PostingFile;
import ru.bpc.billing.repository.PostingFileRepository;
import ru.bpc.billing.repository.ProcessingFileRecordRepository;
import ru.bpc.billing.repository.ProcessingRecordRepository;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * User: Krainov
 * Date: 12.04.2016
 * Time: 12:33
 */
@Service
public class PostingService {

    @Resource
    private PostingFileRepository postingFileRepository;
    @Resource
    private ProcessingFileRecordRepository processingFileRecordRepository;
    @Resource
    private ProcessingRecordRepository processingRecordRepository;

    @Transactional
    public RevertPostingFileResultDto revert(Long postingFileId) {
        RevertPostingFileResultDto revertPostingFileResultDto = new RevertPostingFileResultDto();
        if ( null == postingFileId ) {
            revertPostingFileResultDto.setSuccess(false);
            revertPostingFileResultDto.setText("Invalid param: " + postingFileId);
            return revertPostingFileResultDto;
        }
        PostingFile postingFile = postingFileRepository.findOne(postingFileId);
        if ( null == postingFile ) {
            revertPostingFileResultDto.setSuccess(false);
            revertPostingFileResultDto.setText("Unknown posting file for id: " + postingFileId);
            return revertPostingFileResultDto;
        }
        if ( canRevert(postingFile) ) {
            for (ProcessingRecord processingRecord : postingFile.getRecords()) {
                processingRecordRepository.deleteById(processingRecord.getId());
                processingFileRecordRepository.deleteRecordsById(processingRecord.getId());
            }
            postingFileRepository.delete(postingFile);
            revertPostingFileResultDto.setSuccess(true);
        }
        else {
            revertPostingFileResultDto.setSuccess(false);
            revertPostingFileResultDto.setText("We can not revert posting file: " + postingFile);
        }
        return revertPostingFileResultDto;
    }

    /**
     * Если по данному постингу уже был сформирован отчёт, то мы уже не можем откатить изенения
     * @param postingFile
     * @return
     */
    @Transactional(readOnly = true)
    public boolean canRevert(PostingFile postingFile) {
        ProcessingFile billingFile = postingFile.getParentFile();
        if ( null == billingFile ) throw new IllegalArgumentException("Posting file: " + postingFile + " has not parent billing file");

        ProcessingFile reportFile = billingFile.getFiles().stream().filter(file -> file.getFileType().isReport()).findFirst().orElse(null);
        if ( null != reportFile ) {
            return false;
        }

        return true;
    }

    @Transactional(readOnly = true)
    public boolean canRevert(Long id) {
        ProcessingFile processingFile = postingFileRepository.findOne(id);
        if ( null == processingFile || !(processingFile instanceof PostingFile)) return false;
        return canRevert((PostingFile)processingFile);

    }
}
