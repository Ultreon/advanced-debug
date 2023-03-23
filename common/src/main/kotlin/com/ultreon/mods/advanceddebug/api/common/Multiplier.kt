package com.ultreon.mods.advanceddebug.api.common

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import com.ultreon.mods.advanceddebug.api.util.MathUtil
import java.util.*
import kotlin.math.roundToLong

class Multiplier(value: Double) : IFormattable {
    val value: Double
    val percentage: Percentage get() = (value * 100).percent

    override fun format(ctx: IFormatterContext) {
        if (MathHelper.getDecimalPlaces(value) == 0) {
            ctx.number(value.roundToLong()).operator("×")
        } else {
            ctx.number(MathUtil.roundTo(value, 5)).operator("×")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Multiplier
        return that.value == value
    }

    override fun hashCode(): Int {
        return Objects.hash(value)
    }

    init {
        this.value = value
    }
}

val Number.multiplier
    get() = Multiplier(this.toDouble())
