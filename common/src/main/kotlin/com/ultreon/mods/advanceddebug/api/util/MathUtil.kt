package com.ultreon.mods.advanceddebug.api.util

import java.math.RoundingMode

/**
 * Public math utilities.
 *
 * @author Qboi123
 */
object MathUtil {
    /**
     * Rounds to a given amount of decimal places.
     *
     * @param `val` the value to round.
     * @param places decimal places to round to. (This is inclusive)
     * @return the rounded value.
     */
    fun roundTo(value: Double, places: Int): Double {
        return value.toBigDecimal().setScale(places, RoundingMode.HALF_UP).toDouble()
    }

    /**
     * Rounds to a given amount of decimal places.
     *
     * @param `val` the value to round.
     * @param places decimal places to round to. (This is inclusive)
     * @return the rounded value.
     */
    fun roundTo(value: Float, places: Int): Float {
        return value.toBigDecimal().setScale(places, RoundingMode.HALF_UP).toFloat()
    }
}