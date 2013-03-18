/*
 */
package com.nnstockpredict.Utility;

import java.util.Arrays;

/**
 */
public class ArrayUtil {

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static Double[] convertArray(double[] original) {
        Double[] converted = new Double[original.length];

        for (int i = 0; i < converted.length; i++) {
            converted[i] = new Double(original[i]);
        }

        return converted;
    }

    public static double[] convertArray(Double[] original) {
        double[] converted = new double[original.length];

        for (int i = 0; i < converted.length; i++) {
            converted[i] = original[i].doubleValue();
        }

        return converted;
    }
}
