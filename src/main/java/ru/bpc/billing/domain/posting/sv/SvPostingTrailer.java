package ru.bpc.billing.domain.posting.sv;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.lowlevel.Align;

/**
 * Created with IntelliJ IDEA.
 * User: Petrov_M
 * Date: 27.08.13
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
@FlrDataType
public class SvPostingTrailer {
    @FlrField(pos = 1, length = 1)
    private String FILLER1 = "@";

    @FlrField(pos = 2, length = 6, padCharacter = ' ')
    private String FILLER2;

    @FlrField(pos = 3, length = 9, align = Align.RIGHT, padCharacter = '0')
    private String numberOfRecordsInBody;

    @FlrField(pos = 4, length = 16, padCharacter = '0', align = Align.RIGHT)
    private String TOTAL_DEBIT_AMOUNT;

    @FlrField(pos = 5, length = 16, padCharacter = '0', align = Align.RIGHT)
    private String TOTAL_CREDIT_AMOUNT;

    @FlrField(pos = 6, length = 8, padCharacter = ' ')
    private String CHECK_SUM;

    @FlrField(pos = 7, length = 3922, padCharacter = ' ')
    private String FILLER3;


    public void setNumberOfRecordsInBody(String numberOfRecordsInBody) {
        this.numberOfRecordsInBody = numberOfRecordsInBody;
    }
}
