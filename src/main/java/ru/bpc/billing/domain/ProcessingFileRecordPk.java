package ru.bpc.billing.domain;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * User: Krainov
 * Date: 08.09.2014
 * Time: 10:09
 */
@Embeddable
public class ProcessingFileRecordPk implements Serializable{

    @ManyToOne
    private ProcessingFile file;
    @ManyToOne
    private ProcessingRecord record;

    public ProcessingFileRecordPk(){}
    public ProcessingFileRecordPk(ProcessingFile file, ProcessingRecord record) {
        this.file = file;
        this.record = record;
    }
    public ProcessingFileRecordPk(ProcessingRecord record, ProcessingFile file) {
        this.file = file;
        this.record = record;
    }

    public ProcessingFile getFile() {
        return file;
    }

    public void setFile(ProcessingFile file) {
        this.file = file;
    }

    public ProcessingRecord getRecord() {
        return record;
    }

    public void setRecord(ProcessingRecord record) {
        this.record = record;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessingFileRecordPk that = (ProcessingFileRecordPk) o;

        if (record != null ? !record.equals(that.record) : that.record != null) return false;
        if (file != null ? !file.equals(that.file) : that.file != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (record != null ? record.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }
}
