package com.ultreon.mods.advanceddebug.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class MathUtils {
    public static double average(long[] array) {
        long sum = 0;
        for (long l : array) {
            sum += l;
        }
        return (double) sum / array.length;
    }
    
    public static float average(float[] array) {
        float sum = 0;
        for (float f : array) {
            sum += f;
        }
        return sum / array.length;
    }

    public static int average(int[] array) {
        int sum = 0;
        for (int i : array) {
            sum += i;
        }
        return sum / array.length;
    }

    public static short average(short[] array) {
        short sum = 0;
        for (short s : array) {
            sum += s;
        }
        return (short) (sum / array.length);
    }

    public static byte average(byte[] array) {
        byte sum = 0;
        for (byte b : array) {
            sum += b;
        }
        return (byte) (sum / array.length);
    }

    public static double average(double[] array) {
        double sum = 0;
        for (double d : array) {
            sum += d;
        }
        return sum / array.length;
    }
    
    public static BigInteger average(BigInteger[] array) {
        BigInteger sum = BigInteger.ZERO;
        for (BigInteger b : array) {
            sum = sum.add(b);
        }
        return sum.divide(BigInteger.valueOf(array.length));
    }

    public static BigDecimal average(BigDecimal[] array) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal b : array) {
            sum = sum.add(b);
        }
        return sum.divide(BigDecimal.valueOf(array.length), RoundingMode.HALF_UP);
    }
}
