package ru.bpc.billing.util;

import org.jsefa.common.lowlevel.io.LineSegment;
import org.jsefa.flr.FlrDeserializer;
import org.jsefa.flr.FlrDeserializerImpl;
import org.jsefa.flr.lowlevel.FlrLowLevelDeserializer;
import org.jsefa.rbf.lowlevel.RbfLowLevelDeserializerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import ru.bpc.billing.domain.Carrier;
import ru.bpc.billing.domain.billing.BillingFile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public class BillingFileUtils {

    private final static Logger logger = LoggerFactory.getLogger(BillingFileUtils.class);

    /**
     * Список биллинговых файлов обрабатывается в рамках одного перевозчика
     * Метод возвращает перевозчика или null, если перевозчик не задан.
     */
    public static Carrier getCarrier(Collection<BillingFile> billingFiles) {
        if (CollectionUtils.isEmpty(billingFiles)) return null;
        return billingFiles.stream()
                .filter(bf -> bf.getCarrier() != null)
                .findAny()
                .map(BillingFile::getCarrier)
                .orElse(null);
    }

    public static String getCurrentLine(FlrDeserializer deserializer) {
        if (deserializer == null || !(deserializer instanceof FlrDeserializerImpl)) return null;
        try {
            Method method = FlrDeserializerImpl.class.getDeclaredMethod("getLowLevelDeserializer");
            method.setAccessible(true);
            FlrLowLevelDeserializer lowLevelDeserializer = (FlrLowLevelDeserializer) method.invoke(deserializer);
            if (lowLevelDeserializer == null) return null;
            Field field = RbfLowLevelDeserializerImpl.class.getDeclaredField("currentSegment");
            field.setAccessible(true);
            return ((LineSegment) field.get(lowLevelDeserializer)).getContent();
        } catch (Exception e) {
            logger.error("Reflection error", e);
            return null;
        }
    }

    public static String getRbsIdFromBerTlvData(String berTlvData) {
        int i = berTlvData.indexOf("DF8556") + "DF8556".length() + 2;
        return berTlvData.substring(i,i+36);
    }
}
