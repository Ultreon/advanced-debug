package com.ultreon.mods.advanceddebug.util

import java.math.BigDecimal

@Suppress("unused")
class MathHelper private constructor() {
    init {
        throw UnsupportedOperationException("Cannot instantiate a utility class")
    }

    companion object {
        fun getDecimalPlaces(d: Float): Int {
            val s = d.toString()
            if (s.endsWith(".0")) {
                return 0
            }
            val split = s.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return if (split.size == 1) {
                0
            } else split[1].length
        }

        fun getDecimalPlaces(d: Double): Int {
            val s = d.toString()
            if (s.endsWith(".0")) {
                return 0
            }
            val split = s.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return if (split.size == 1) {
                0
            } else split[1].length
        }

        fun getDecimalPlaces(d: BigDecimal): Int {
            val s = d.toString()
            if (s.endsWith(".0")) {
                return 0
            }
            val split = s.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return if (split.size == 1) {
                0
            } else split[1].length
        }
    }
}