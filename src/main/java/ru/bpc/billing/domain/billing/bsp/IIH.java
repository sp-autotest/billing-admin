package ru.bpc.billing.domain.billing.bsp;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;

@FlrDataType(defaultPrefix = "IIH")
public class IIH {

    @FlrField(pos = 1, length = 36)
    private String FILLER_2_7;

    @FlrField(pos = 2, length = 16)
    private String AGRN;

    public String getAGRN() {
        return AGRN;
    }
}
