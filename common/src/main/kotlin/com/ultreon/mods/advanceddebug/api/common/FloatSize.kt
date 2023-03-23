package com.ultreon.mods.advanceddebug.api.common

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import com.ultreon.mods.advanceddebug.api.util.MathUtil
import java.util.*

class FloatSize(var width: Float, var height: Float) : IFormattable {

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
        val size = other as FloatSize
        return size.width.compareTo(width) == 0 && size.height.compareTo(height) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(width, height)
    }
}

val Float.squared get() = FloatSize(this, this)
fun size(width: Float, height: Float) = FloatSize(width, height)
