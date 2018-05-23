package ru.bpc.billing.domain.bo;

import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.FileType;
import ru.bpc.billing.domain.ProcessingFile;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Petrov_M
 * Date: 26.08.13
 * Time: 9:54
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "bo_file")
public class BOFile extends ProcessingFile {

    public BOFile(FileType fileType) {
        super(fileType);
    }

    public BOFile() {
        super();
    }

    @Column(name = "format", nullable = false)
    @Enumerated(EnumType.STRING)
    private BOFileFormat format;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_carrier_id", nullable = true)
    private Carrier carrier;

    public BOFileFormat getFormat() {
        return format;
    }

    public void setFormat(BOFileFormat format) {
        this.format = format;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }
}