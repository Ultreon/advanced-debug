package com.ultreon.mods.advanceddebug.api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Public math utilities.
 *
 * @author Qboi123
 */
public class MathUtil {
    /**
     * Rounds to a given amount of decimal places.
     *
     * @param val the value to round.
     * @param places decimal places to round to. (This is inclusive)
     * @return the rounded value.
     */
    public static double roundTo(double val, int places) {
        return BigDecimal.valueOf(val).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Rounds to a given amount of decimal places.
     *
     * @param val the value to round.
     * @param places decimal places to round to. (This is inclusive)
     * @return the rounded value.
     */
    public static float roundTo(float val, int places) {
        return BigDecimal.valueOf(val).setScale(places, RoundingMode.HALF_UP).floatValue();
    }
}
