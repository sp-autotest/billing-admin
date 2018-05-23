package ru.bpc.billing.domain.billing;

import ru.bpc.billing.domain.billing.arc.*;
import ru.bpc.billing.domain.billing.bsp.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Petrov_M
 * Date: 26.08.13
 * Time: 11:16
 */
public enum BillingFileFormat {
    ARC("TFH", TAB.class, TAA.class, TFH.class, TBH.class, TFS.class), BSP("IFH", IFH.class, IBR.class, IIH.class, IBH.class, IBT.class, IFT.class, IIT.class);

    private String filePrefix;
    private Class[] classes;


    private BillingFileFormat(String filePrefix, Class... classes) {
        this.filePrefix = filePrefix;
        this.classes = classes;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public Class[] getClasses() {
        return classes;
    }

    public static Class[] getAllClasses() {
        List<Class> classes = new LinkedList<Class>();
        for (BillingFileFormat ff : values()) {
            classes.addAll(Arrays.asList(ff.getClasses()));
        }
        return classes.toArray(new Class[classes.size()]);
    }
}