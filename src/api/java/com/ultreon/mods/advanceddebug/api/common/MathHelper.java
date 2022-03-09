package com.ultreon.mods.advanceddebug.api.common;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@SuppressWarnings("unused")
@UtilityClass
class MathHelper {
    int getDecimalPlaces(Float d) {
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

    int getDecimalPlaces(Double d) {
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

    int getDecimalPlaces(BigDecimal d) {
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
