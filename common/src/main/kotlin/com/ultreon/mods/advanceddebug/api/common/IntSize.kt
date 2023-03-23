package com.ultreon.mods.advanceddebug.api.common

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import java.util.*

class IntSize(var width: Int, var height: Int) : IFormattable {

    override fun format(ctx: IFormatterContext) {
        ctx.number(width).operator(" × ").number(height)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val size = other as IntSize
        return width == size.width && height == size.height
    }

    override fun hashCode(): Int {
        return Objects.hash(width, height)
    }
}

val Int.squared get() = IntSize(this, this)
fun size(width: Int, height: Int) = IntSize(width, height)
