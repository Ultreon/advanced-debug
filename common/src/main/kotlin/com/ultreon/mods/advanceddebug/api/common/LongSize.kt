package com.ultreon.mods.advanceddebug.api.common

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import java.util.*

class LongSize(override var width: Long, override var height: Long) : AbstractSize(), IFormattable {

    override fun format(ctx: IFormatterContext) {
        ctx.number(width).operator(" × ").number(height)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val longSize = other as LongSize
        return longSize.width.compareTo(width) == 0 && longSize.height.compareTo(height) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(width, height)
    }
}

val Long.squared get() = LongSize(this, this)
fun size(width: Long, height: Long) = LongSize(width, height)

