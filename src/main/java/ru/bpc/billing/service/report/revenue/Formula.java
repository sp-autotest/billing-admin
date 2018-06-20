package ru.bpc.billing.service.report.revenue;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * User: Krainov
 * Date: 16.09.2014
 * Time: 10:29
 */
public abstract class Formula {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String FEE_IN_CURRENCY_OPERATION = "G%d*S%d*(-1)";
    private static final String NET_IN_CURRENCY_OPERATION = "G%d + H%d";
    private static final String GROSS_IN_CURRENCY_MPS = "K%d*G%d";
    private static final String FEE_IN_CURRENCY_MPS = "L%d*S%d*(-1)";
    private static final String NET_IN_CURRENCY_MPS = "L%d+M%d";
    private static final String GROSS_IN_CURRENCY_BANK = "L%d*O%d";
    private static final String FEE_IN_CURRENCY_BANK = "P%d*S%d*(-1)";
    private static final String NET_ON_CURRENCY_BANK = "P%d+Q%d";
    private static final String REJECT_TOTAL_GROSS_IN_CURRENCY_MPS = "'Rejects, Сhargebacks'!L%d";
    private static final String FROM_REJECT_LIST = "'Rejects, Сhargebacks'!%s%d";
    private static final String SUM_FROM_START_TO_FINISH = "SUM(%s%d:%s%d)";
    private static final String NET_IN_RUB_TO_TICKET_INFO = "J%d+K%d";

    public final static String feeInCurrencyOperation(int rowNum) {
        return String.format(FEE_IN_CURRENCY_OPERATION, rowNum, rowNum);
    }

    public final static String netInCurrencyOperation(int rowNum) {
        return String.format(NET_IN_CURRENCY_OPERATION, rowNum, rowNum);
    }

    public final static String feeInCurrencyMps(int rowNum) {
        return String.format(FEE_IN_CURRENCY_MPS, rowNum, rowNum);
    }
    public final static String netInCurrencyMps(int rowNum) {
        return String.format(NET_IN_CURRENCY_MPS, rowNum, rowNum);
    }

    public final static String feeInCurrencyBank(int rowNum) {
        return String.format(FEE_IN_CURRENCY_BANK, rowNum, rowNum);
    }

    public final static String netInCurrencyBank(int rowNum) {
        return String.format(NET_ON_CURRENCY_BANK, rowNum, rowNum);
    }

    public final static String grossInMps(int rowNum) {
        return String.format(GROSS_IN_CURRENCY_MPS, rowNum, rowNum);
    }

    public final static String grossInBank(int rowNum) {
        return String.format(GROSS_IN_CURRENCY_BANK, rowNum, rowNum);
    }

    public final static String fromRejectList(String cellName, int rowNum) {
        return String.format(FROM_REJECT_LIST, cellName, rowNum);
    }

    public final static String rejectTotalGrossInCurrencyMps(int rowNumInRejectList) {
        return String.format(REJECT_TOTAL_GROSS_IN_CURRENCY_MPS, rowNumInRejectList);
    }

    public static void main(String[] args) {
        String s = Formula.subtotalsMinusRejects("A",false, Arrays.asList(1, 2, 3, 4, 5, 6));
        System.out.println("s = " + s);
    }

    //if will be situations when count of elements in formula are more then support excel version then we have to split formula to several formulas, like sum(1,2) + sum(3,4)
    public final static String subtotalsMinusRejects(String cellName, boolean sumFromToStartToFinish, Collection<Integer> rows, Integer... rejectRowNums) {
        if (null == cellName || null == rows ) return "";
        Integer[] ra = rows.toArray(new Integer[]{});

        String subtotals = "";
        if (sumFromToStartToFinish) {
            if ( ra.length > 0 ) {
                int start = ra[0];
                int finish = ra[ra.length - 1];
                subtotals = String.format(SUM_FROM_START_TO_FINISH, cellName, start, cellName, finish);
            }
        }
        else {
            StringBuilder builder = new StringBuilder("SUM(");
            int i = 0;
            int length = rows.size();
            for (Integer row : rows) {
                builder.append(cellName).append(row).append(i == length-1 ? "" : ",");
                i++;
            }
            builder.append(")");
            subtotals = builder.toString();
        }
        StringBuilder withRejects = new StringBuilder(subtotals);
        for (Integer rejectRowNum : rejectRowNums) {
            withRejects.append("-").append(cellName).append(rejectRowNum);
        }
        return withRejects.toString();
    }

    private final static String subtotalsMinusRejects(String cellName, int start, int finish, Integer... rejectRowNums) {
        String subtotals = String.format(SUM_FROM_START_TO_FINISH, cellName, start, cellName, finish);
        StringBuilder withRejects = new StringBuilder(subtotals);
        for (Integer rejectRowNum : rejectRowNums) {
            withRejects.append("-").append(cellName).append(rejectRowNum);
        }
        return withRejects.toString();
    }

    public final static String subtotalsCountryGrossInCurrencyMps(int start, int finish, Integer... rejectRowNums) {
        return subtotalsMinusRejects("P", start, finish, rejectRowNums);
    }

    public final static String subtotalsCountryFeeInCurrencyMps(int start, int finish, Integer... rejectRowNums) {
        return subtotalsMinusRejects("Q", start, finish, rejectRowNums);
    }

    public final static String subtotalsCountryNetInCurrencyMps(int start, int finish, Integer... rejectRowNums) {
        return subtotalsMinusRejects("R", start, finish, rejectRowNums);
    }

    public final static String rejectTotalGrossInCurrencyMpsByCurrencyMps(Integer... rejectRowNumsByCurrencyMps) {
        StringBuilder builder = new StringBuilder("SUM(");
        for (Integer mp : rejectRowNumsByCurrencyMps) {
            builder.append("L").append(mp).append(";");
        }
        builder.append(")");
        return builder.toString();
    }

    public final static String totalFromStartToEnd(String cellName, int start, int end) {
        StringBuilder builder = new StringBuilder("SUM(");
        builder.append(cellName).append(start).append(":").append(cellName).append(end);
        return builder.toString();
    }

    public final static String totalGrossByCurrencyMps(Integer... totalsByCountry) {
        StringBuilder builder = new StringBuilder("SUM(");
        for (Integer mp : totalsByCountry) {
            builder.append("L").append(mp).append(",");
        }
        builder.append(")");
        return builder.toString();
    }

    public final static String totalFeeByCurrencyMps(Integer... totalsByCountry) {
        StringBuilder builder = new StringBuilder("SUM(");
        for (Integer mp : totalsByCountry) {
            builder.append("M").append(mp).append(",");
        }
        builder.append(")");
        return builder.toString();
    }

    public final static String totalNetByCurrencyMps(Integer... totalsByCountry) {
        StringBuilder builder = new StringBuilder("SUM(");
        for (Integer mp : totalsByCountry) {
            builder.append("N").append(mp).append(",");
        }
        builder.append(")");
        return builder.toString();
    }

    public final static String totalGrossByCurrencyBank(Integer... totalsByCountry) {
        StringBuilder builder = new StringBuilder("SUM(");
        for (Integer mp : totalsByCountry) {
            builder.append("P").append(mp).append(",");
        }
        builder.append(")");
        return builder.toString();
    }

    public final static String totalFeeByCurrencyBank(Integer... totalsByCountry) {
        StringBuilder builder = new StringBuilder("SUM(");
        for (Integer mp : totalsByCountry) {
            builder.append("Q").append(mp).append(",");
        }
        builder.append(")");
        return builder.toString();
    }

    public final static String totalNetByCurrencyBank(Integer... totalsByCountry) {
        StringBuilder builder = new StringBuilder("SUM(");
        for (Integer mp : totalsByCountry) {
            builder.append("R").append(mp).append(",");
        }
        builder.append(")");
        return builder.toString();
    }

    public final static String rejectTotalGrossInCurrencyMps(int start, int finish) {
        return subtotalsMinusRejects("L", start, finish);
    }

    public final static String rejectTotalFeeInCurrencyMps(int start, int finish) {
        return subtotalsMinusRejects("M", start, finish);
    }

    public final static String rejectTotalNetInCurrencyMps(int start, int finish) {
        return subtotalsMinusRejects("N", start, finish);
    }

    private final static String theSameValue(int rowNum, char column) {
        return column + String.valueOf(rowNum);
    }

    public final static String feeRateFormula(int rowNum, int feeColumn) {
        rowNum++;
        String divisorColumn = "" + ALPHABET.charAt(feeColumn - 1) + rowNum;
        return "IF(" + divisorColumn + "=0,0,ABS(" + ALPHABET.charAt(feeColumn) + rowNum + "/" + divisorColumn + "))";
    }
    public final static String feeRateFormula(int rowNum, int feeColumn, int grossColumn) {
        rowNum++;
        String divisorColumn = "" + ALPHABET.charAt(grossColumn) + rowNum;
        return "IF(" + divisorColumn + "=0,0,ABS(" + ALPHABET.charAt(feeColumn) + rowNum + "/" + divisorColumn + "))";
    }
    public final static String feeAmountInRub(int rowNum, String feeAmountInRubColumn, double amountInRub) {
        rowNum++;
        feeAmountInRubColumn += rowNum;
        return "IF(" + feeAmountInRubColumn + ">0," + amountInRub + "*(-1)," + amountInRub + ")";
    }
    public final static String feeAmountInRub(int rowNum, int iFeAmountInRubColumn, double amountInRub) {
        rowNum++;
        String feeAmountInRubColumn = "" + ALPHABET.charAt(iFeAmountInRubColumn) + rowNum;
        return "IF(" + feeAmountInRubColumn + ">0," + amountInRub + "*(-1)," + amountInRub + ")";
    }
    public final static String sign(int rowNum, int signColumn, double operationValue) {
        rowNum++;
        String sign = "" + ALPHABET.charAt(signColumn) + rowNum;
        return operationValue + "* IF(" + sign + "=\"CR\",-1,1)";
    }

    public final static String signFeeOperation(int rowNum, int signOperation, int grossOperation, int feeRate) {
        rowNum++;
        String divisorColumn = "" + ALPHABET.charAt(feeRate) + rowNum;
        String signColumn = "" + ALPHABET.charAt(signOperation) + rowNum;
        return "IF(" + divisorColumn + "=0,0,ABS(" + ALPHABET.charAt(grossOperation) + rowNum + "*" + divisorColumn + ")) " +
                "* IF(" + signColumn + "=\"CR\",-1,1)";
    }

    public final static String sumIf(String range, String criteria, String rangeSum) {
        return "SUMIF(" + range + ",\"" + criteria + "\"," + rangeSum + ")";
    }

    public final static String sumByCurrencyOperationGross(int startRow, int endRow, String currencyOperation) {
        return sumIf("F" + startRow + ":F" + endRow, currencyOperation, "G" + startRow + ":" + "G" + endRow);
    }
    public final static String sumByCurrencyOperationFee(int startRow, int endRow, String currencyOperation) {
        return sumIf("F" + startRow + ":F" + endRow, currencyOperation, "H" + startRow + ":" + "H" + endRow);
    }
    public final static String sumByCurrencyOperationNet(int startRow, int endRow, String currencyOperation) {
        return sumIf("F" + startRow + ":F" + endRow, currencyOperation, "I" + startRow + ":" + "I" + endRow);
    }
    public final static String sumByCurrencyOperationCount(int startRow, int endRow, String currencyOperation) {
        return sumIf("F" + startRow + ":F" + endRow, currencyOperation, "E" + startRow + ":" + "E" + endRow);
    }
    public final static String mul(int rowNum, String firstArg, String secondArg) {
        rowNum++;
        return firstArg + rowNum + "*" + secondArg + rowNum;
    }
    public final static String amountInRubUseAllRates(int rowNum, String amountInCurrencyClientColumn, double rateCb, double rateMps) {
        rowNum ++;
        String formula = amountInCurrencyClientColumn + rowNum + "*" + rateCb + "*" + rateMps;
        return formula;
    }
    public final static String amountInRubUseAllRates(int rowNum, String amountInCurrencyClientColumn, String rateCbColumn, String rateMpsColumn) {
        rowNum ++;
        String formula = (amountInCurrencyClientColumn + rowNum) + "*" + (rateCbColumn + rowNum) + "*" + (rateMpsColumn + rowNum);
        return formula;
    }
    public final static String amountInRubUseAllRates(int rowNum, String amountInCurrencyClientInMinor, int iRateCbColumn, int iRateMpsColumn) {
        rowNum ++;
        String rateCbColumn = "" + ALPHABET.charAt(iRateCbColumn) + rowNum;
        String rateMpsColumn = "" + ALPHABET.charAt(iRateMpsColumn) + rowNum;
        String formula = (amountInCurrencyClientInMinor) + "*" + (rateCbColumn) + "*" + (rateMpsColumn) + "/100";
        return formula;
    }
    public final static String feeAmountInRubTicketInfo(int rowNum, int iFeAmountInRubColumn, double amountInRub) {
        rowNum++;
        String feeAmountInRubColumn = "" + ALPHABET.charAt(iFeAmountInRubColumn) + rowNum;
        return "IF(" + feeAmountInRubColumn + ">0," + amountInRub + "*(-1), 0)";
    }

    public final static String netInRubTicketInfo(int rowNum) {
        rowNum++;
        return String.format(NET_IN_RUB_TO_TICKET_INFO, rowNum, rowNum);
    }

    public static String formatString(int minorUnit) {
        if (minorUnit == 0) {
            return "0";
        }
        return "0." + StringUtils.repeat("0", minorUnit);
    }

}