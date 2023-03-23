package com.ultreon.mods.advanceddebug.api.common

import java.math.BigDecimal
import java.math.BigInteger

abstract class AbstractSize {
    abstract val width: Number
    abstract val height: Number
    val intWidth: Int
        get() = width as Int
    val intHeight: Int
        get() = height as Int
    val longWidth: Long
        get() = width as Long
    val longHeight: Long
        get() = height as Long
    val floatWidth: Float
        get() = width as Float
    val floatHeight: Float
        get() = height as Float
    val doubleWidth: Double
        get() = width as Double
    val doubleHeight: Double
        get() = height as Double
    val bigIntegerWidth: BigInteger
        get() = BigInteger(width.toString())
    val bigIntegerHeight: BigInteger
        get() = BigInteger(height.toString())
    val bigDecimalWidth: BigDecimal
        get() = BigDecimal(width.toString())
    val bigDecimalHeight: BigDecimal
        get() = BigDecimal(height.toString())
}