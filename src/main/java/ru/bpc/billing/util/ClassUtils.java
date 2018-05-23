package ru.bpc.billing.util;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Petrov_M
 * Date: 24.10.12
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */
public class ClassUtils {
    public static String getShortClassName(String fullClassName) {
        if (StringUtils.isBlank(fullClassName)) return null;
        return fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
    }

    public static String getShortClassName(Class clazz) {
        if (clazz == null) return null;
        return getShortClassName(clazz.getName());
    }

    public static String getShortClassName(Object object) {
        if (object == null) return null;
        return getShortClassName(object.getClass());
    }

    private static void removeLineSeparators(File file) throws IOException {
        Properties props = new Properties();
        try {
            FileReader fileReader = new FileReader(file);
            props.load(fileReader);
            fileReader.close();

            FileWriter writer = new FileWriter(file);
            props.store(writer, null);
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void sortAndGroup(File file, int groupingFactor) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            List<String> lines = new LinkedList<String>();
            while ((line = br.readLine()) != null) {
                if (!StringUtils.isBlank(line) && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
            br.close();

            Collections.sort(lines);

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            StringBuilder curPrefix = new StringBuilder();
            String prevPrefix = null;
            for (String s : lines) {
                String[] parts = s.split("\\.");
                for (int i = 0; i < groupingFactor; i++) {
                    if (i < parts.length) {
                        curPrefix.append(".").append(parts[i]);
                    }
                }
                curPrefix.delete(0, 1);
                if (prevPrefix != null && !prevPrefix.equals(curPrefix.toString())) bw.newLine();
                prevPrefix = curPrefix.toString();
                curPrefix.delete(0, curPrefix.length());

                bw.write(s);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}