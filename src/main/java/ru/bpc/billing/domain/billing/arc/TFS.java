package ru.bpc.billing.domain.billing.arc;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;

/**
 * File summary records
 */
@FlrDataType(defaultPrefix = "TFS")
public class TFS {

    @FlrField(pos = 1, length = 3)
    private String FILLER;
}
