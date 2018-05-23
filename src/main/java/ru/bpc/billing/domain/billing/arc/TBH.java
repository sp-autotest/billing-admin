package ru.bpc.billing.domain.billing.arc;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;

@FlrDataType(defaultPrefix = "TBH")
public class TBH {
    @FlrField(pos = 1, length = 6)
    private String SEQUENCE_NUMBER; // (fields 2)

    @FlrField(pos = 2, length = 15)
    private String AGRN;

    @FlrField(pos = 3, length = 6)
    private String SIDT;

    @FlrField(pos = 4, length = 14)
    private String INVN; // INVOICE NUMBER (field 5)

    public String getINVN() {
        return INVN;
    }

    public String getAGRN() {
        return AGRN;
    }

}