package ru.bpc.billing.domain.billing.arc;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;

/**
 * TRANSACTION FILE HEADER (TFH) RECORD
 */
@FlrDataType(defaultPrefix = "TFH")
public class TFH {
    public static final String PRDA_PATTERN = "yyMMdd";

    @FlrField(pos = 1, length = 6)
    private String FILLER_2; // (field 2)

    @FlrField(pos = 2, length = 6)
    private String PRDA; // PROCESSING DATE (field 3)

    @FlrField(pos = 3, length = 4)
    private String TIME; // PROCESSING TIME (field 4)


    public String getPRDA() {
        return PRDA;
    }

    public String getTIME() {
        return TIME;
    }

    public String getProcessingDate() {
        return PRDA + TIME;
    }
}
