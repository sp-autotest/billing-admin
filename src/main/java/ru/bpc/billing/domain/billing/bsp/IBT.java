package ru.bpc.billing.domain.billing.bsp;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;

/**
 * Batch Trailer Record
 */
@FlrDataType(defaultPrefix = "IBT")
public class IBT {

    @FlrField(pos = 1, length = 3)
    private String FILLER;
}
