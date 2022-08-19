package com.ultreon.mods.advanceddebug.api.common;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public class MathHelper {
    private MathHelper() {
        throw new UnsupportedOperationException("Cannot instantiate a utility class");
    }

    static int getDecimalPlaces(Float d) {
        String s = d.toString();
        if (s.endsWith(".0")) {
            return 0;
        }
        String[] split = s.split("\\.");
        if (split.length == 1) {
            return 0;
        }

        return split[1].length();
    }

    static int getDecimalPlaces(Double d) {
        String s = d.toString();
        if (s.endsWith(".0")) {
            return 0;
        }
        String[] split = s.split("\\.");
        if (split.length == 1) {
            return 0;
        }

        return split[1].length();
    }

    static int getDecimalPlaces(BigDecimal d) {
        String s = d.toString();
        if (s.endsWith(".0")) {
            return 0;
        }
        String[] split = s.split("\\.");
        if (split.length == 1) {
            return 0;
        }

        return split[1].length();
    }
}
