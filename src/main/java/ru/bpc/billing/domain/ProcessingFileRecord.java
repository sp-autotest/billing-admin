package ru.bpc.billing.domain;

import javax.persistence.*;

/**
 * User: Krainov
 * Date: 08.09.2014
 * Time: 10:08
 * http://boris.kirzner.info/blog/archives/2008/07/19/hibernate-annotations-the-many-to-many-association-with-composite-key/
 */
@Entity
@Table(name = "file_record")
@AssociationOverrides({
        @AssociationOverride(name = "pk.record", joinColumns = @JoinColumn(name = "record_id")),
        @AssociationOverride(name = "pk.file", joinColumns = @JoinColumn(name = "file_id"))
})
public class ProcessingFileRecord {

    @EmbeddedId
    private ProcessingFileRecordPk pk = new ProcessingFileRecordPk();

    public ProcessingFileRecord(){}
    public ProcessingFileRecord(ProcessingFileRecordPk pk) {
        this.pk = pk;
    }

    public ProcessingFileRecordPk getPk() {
        return pk;
    }

    public void setPk(ProcessingFileRecordPk pk) {
        this.pk = pk;
    }

    @Transient
    public ProcessingFile getFile() {
        return getPk().getFile();
    }

    public void setFile(ProcessingFile processingFile) {
        getPk().setFile(processingFile);
    }

    @Transient
    public ProcessingRecord getRecord() {
        return getPk().getRecord();
    }

    public void setRecord(ProcessingRecord record) {
        getPk().setRecord(record);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessingFileRecord that = (ProcessingFileRecord) o;

        if (getPk() != null ? !getPk().equals(that.getPk()) : that.getPk() != null) return false;

        return true;
    }

    public int hashCode() {
        return (getPk() != null ? getPk().hashCode() : 0);
    }
}
