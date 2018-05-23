package ru.bpc.billing.domain.billing.bsp;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;

/**
 * File Header Record – IFH
 */
@FlrDataType(defaultPrefix = "IFH")
public class IFH {
    public static final String PRDA_PATTERN = "yyyyMMdd";
    public static final String TIME_PATTERN = "HHmm";

    @FlrField(pos = 1, length = 53)
    private String FILLER_2_5; // (fields 2 - 5)

    @FlrField(pos = 2, length = 8)
    private String PRDA; // Current processing date. Format: YYYYMMDD (field 6)

    @FlrField(pos = 3, length = 4)
    private String TIME; // Processing time. It is always 0000 (field 7)


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
