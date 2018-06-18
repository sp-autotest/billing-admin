package ru.bpc.billing.domain.bo.sv;

import ru.bpc.billing.domain.bo.OperationType;

/**
 * User: Krainov
 * Date: 11.08.14
 * Time: 15:37
 */
public enum SvOperationType implements OperationType {
    US_ON_US_BSP("79","US"),
    VISA_ON_US_BSP("80","VI"),
    MC_ON_US_BSP("81","MC"),
    UNKNOWN("-1","UNKNOWN"),
    //fake 2 values, which won't find in bo-file
    US_ON_US_BSP_VISA("7980","US VI"),
    US_ON_US_BSP_MC("7981","US MC"),
    //new 2 values
    VI_US("18", "VI_Us"),//добавлены два новых типа операций 18 и 19,
    MC_US("19", "MC_Us"),//согласно пунктов ТЗ 5.5 и 5.6
    //real nspc values , we can see in bo-file
    NSPC_ON_US_VISA("92","VI_NSPK"),//изменены два типа операций с кодами 92 и 93,
    NSPC_ON_US_MC("93","MC_NSPK");//согласно пунктов ТЗ 5.5 и 5.6
//    NSPC_ON_US_VISA("92","NSPC-on-Us VISA"),
//    NSPC_ON_US_MC("93","NSPC-on-Us MC");

    private final String code;
    private final String type;
    private SvOperationType(String code, String type) {
        this.code = code;
        this.type = type;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getType() {
        return type;
    }

    public static OperationType valueOfCode(String code) {
        for (OperationType type : values()) {
            if ( type.getCode().equals(code) ) return type;
        }
        return UNKNOWN;
    };



}
