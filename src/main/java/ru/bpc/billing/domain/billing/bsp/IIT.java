package ru.bpc.billing.domain.billing.bsp;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;

/**
 * Invoice Trailer Record
 */
@FlrDataType(defaultPrefix = "IIT")
public class IIT {

    @FlrField(pos = 1, length = 3)
    private String FILLER;
}
