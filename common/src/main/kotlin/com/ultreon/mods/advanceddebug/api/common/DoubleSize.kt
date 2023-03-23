package com.ultreon.mods.advanceddebug.api.common

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import com.ultreon.mods.advanceddebug.api.util.MathUtil
import java.util.*

class DoubleSize(var width: Double, var height: Double) : IFormattable {

    override fun format(ctx: IFormatterContext) {
        ctx.number(MathUtil.roundTo(width, 5)).operator(" × ").number(
            MathUtil.roundTo(
                height, 5
            )
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as DoubleSize
        return that.width.compareTo(width) == 0 && that.height.compareTo(height) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(width, height)
    }
}

val Double.squared get() = DoubleSize(this, this)
fun size(width: Double, height: Double) = DoubleSize(width, height)
