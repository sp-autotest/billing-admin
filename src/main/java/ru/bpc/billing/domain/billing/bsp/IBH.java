package ru.bpc.billing.domain.billing.bsp;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;

/**
 * Batch Header Record – IBH
 */
@FlrDataType(defaultPrefix = "IBH")
public class IBH {

    @FlrField(pos = 1, length = 3)
    private String FILLER;
}
