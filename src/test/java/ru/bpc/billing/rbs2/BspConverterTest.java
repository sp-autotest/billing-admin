package ru.bpc.billing.rbs2;

import org.jsefa.flr.FlrDeserializer;
import org.jsefa.flr.FlrIOFactory;
import org.jsefa.flr.FlrSerializer;
import org.junit.Test;
import ru.bpc.billing.domain.billing.BillingFileFormat;
import ru.bpc.billing.domain.billing.bsp.IBR;
import ru.bpc.billing.domain.posting.sv.SvPostingRecord;
import ru.bpc.billing.service.billing.AbstractBillingConverter;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * User: Krainov
 * Date: 02.09.14
 * Time: 11:55
 */
public class BspConverterTest {

    @Test
    public void testConvert() throws Exception {
        FlrDeserializer deserializer = FlrIOFactory.createFactory(BillingFileFormat.BSP.getClasses()).createDeserializer();
        deserializer.open(new FileReader("D:\\Users\\krainov\\Documents\\work\\bpc\\rbs-web\\bsp\\bugs\\AFBRBS-3073\\alphabank_20140519_0001_Panama"));

        FlrSerializer serializer = FlrIOFactory.createFactory(SvPostingRecord.class).createSerializer();
        serializer.open(new BufferedWriter(new FileWriter("D:\\Users\\krainov\\Documents\\work\\bpc\\rbs-web\\bsp\\bugs\\AFBRBS-3073\\my\\test")));

        SvPostingRecord record = new SvPostingRecord();
        StringBuilder sb = new StringBuilder("DF855203BSP"); // Тип платёжного агрегатора

        String test = "123456789asdfsdfgsdfsdfsdfsdfsdfя";
        System.out.println(test.length() + ":" + test.getBytes().length);

        System.out.println(UUID.randomUUID().toString());


        while (deserializer.hasNext()) {
            Object o = deserializer.next();
            if ( o instanceof IBR ) {
                IBR ibr = (IBR)o;
                String posn = null != ibr.getPOSN() ? ibr.getPOSN() : "aeroflot " + ibr.getTDNR();
                for (char c : posn.toCharArray()) {
                    System.out.println(c + ":" + String.valueOf(c).getBytes().length);
                }
                System.out.println("1 = " + posn + ":" + posn.length() + ":" + posn.getBytes().length + ":" + toHexString(posn.length()) + ":" + toHexString(posn.getBytes().length));
                ByteBuffer bb = Charset.forName("UTF-8").encode(posn);
                String ss = new String(bb.array());
                System.out.println("2 = " + ss + ":" + ss.length() + ":" + ss.getBytes().length + ":" + toHexString(ss.length()) + ":" + toHexString(ss.getBytes().length));
                sb.append("DF8555").append(toHexString(posn.length())).append(posn); // Наименование точки продажи билета
                record.setBerTlvData(sb.toString());
            }
        }
//        serializer.write(record);
//        serializer.flush();
        serializer.close(false);

        AbstractBillingConverter.safeCloseDeserializer(deserializer);
        AbstractBillingConverter.safeCloseSerializer(serializer);
    }

    public static String toHexString(Integer i) {
        if (i == null) return null;
        String res = Integer.toHexString(i).toUpperCase();
        return res.length() % 2 == 0 ? res : "0" + res;
    }

}
