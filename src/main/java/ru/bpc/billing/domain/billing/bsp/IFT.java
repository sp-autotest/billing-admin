package ru.bpc.billing.domain.billing.bsp;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;

/**
 * File Trailer Record
 */
@FlrDataType(defaultPrefix = "IFT")
public class IFT {

    @FlrField(pos = 1, length = 3)
    private String FILLER;
}
