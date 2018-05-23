package ru.bpc.billing.domain.posting.sv;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.lowlevel.Align;

/**
 * Created with IntelliJ IDEA.
 * User: Petrov_M
 * Date: 27.08.13
 * Time: 13:34
 * To change this template use File | Settings | File Templates.
 */
@FlrDataType
public class SvPostingHeader {
    @FlrField(pos = 1, length = 1)
    private String RECORD_TYPE = "1";

    @FlrField(pos = 2, length = 2)
    private String FILLER_2 = "01";

    @FlrField(pos = 3, length = 1, padCharacter = ' ')
    private String FILLER_3;

    @FlrField(pos = 4, length = 9, align = Align.LEFT, padCharacter = ' ')
    private String RECEIVING_INSTITUTION_NUMBER = "0010";

    @FlrField(pos = 5, length = 4, align = Align.LEFT, padCharacter = ' ')
    private String INDICATOR = "999";

    @FlrField(pos = 6, length = 1, padCharacter = ' ')
    private String FILLER_6;

    @FlrField(pos = 7, length = 8, align = Align.LEFT, padCharacter = ' ')
    private String settlementDate;

    @FlrField(pos = 8, length = 1, padCharacter = ' ')
    private String FILLER_8;

    @FlrField(pos = 9, length = 6, align = Align.LEFT, padCharacter = ' ')
    private String settlementTime;

    @FlrField(pos = 10, length = 1, padCharacter = ' ')
    private String FILLER_10;

    @FlrField(pos = 11, length = 8, align = Align.LEFT, padCharacter = ' ')
    private String fileCreationDate;

    @FlrField(pos = 12, length = 1, padCharacter = ' ')
    private String FILLER_12;

    @FlrField(pos = 13, length = 6, align = Align.LEFT, padCharacter = ' ')
    private String fileCreationTime;

    @FlrField(pos = 14, length = 1, padCharacter = ' ')
    private String FILLER_14;

    @FlrField(pos = 15, length = 4, padCharacter = '0')
    private String FILLER_15;

    @FlrField(pos = 16, length = 1)
    private String FILLER_16 = "A";

    @FlrField(pos = 17, length = 1, padCharacter = ' ')
    private String FILLER_17;

    @FlrField(pos = 18, length = 4)
    private String RECORD_SIZE = "3978";

    @FlrField(pos = 19, length = 2, padCharacter = ' ')
    private String FILLER_19;

    @FlrField(pos = 20, length = 1)
    private String FILLER_20 = "1";

    @FlrField(pos = 21, length = 1, padCharacter = ' ')
    private String FILLER_21;

    @FlrField(pos = 22, length = 4, align = Align.LEFT, padCharacter = ' ')
    private String INSTITUTUIN_ID = "0010";

    @FlrField(pos = 23, length = 42, padCharacter = ' ')
    private String FILLER_23;

    @FlrField(pos = 24, length = 3, align = Align.LEFT, padCharacter = ' ')
    private String POSTING_VERSION = "BSP";

    @FlrField(pos = 25, length = 1, padCharacter = ' ')
    private String FILLER_25;

    @FlrField(pos = 26, length = 8, align = Align.LEFT, padCharacter = ' ')
    private String NEXT_SETTLEMENT_DATE;

    @FlrField(pos = 27, length = 1, padCharacter = ' ')
    private String FILLER_27;

    @FlrField(pos = 28, length = 6, align = Align.LEFT, padCharacter = ' ')
    private String NEXT_SETTLEMENT_TIME;


    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }

    public void setFileCreationDate(String fileCreationDate) {
        this.fileCreationDate = fileCreationDate;
    }

    public void setFileCreationTime(String fileCreationTime) {
        this.fileCreationTime = fileCreationTime;
    }
}