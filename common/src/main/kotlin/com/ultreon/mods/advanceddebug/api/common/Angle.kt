package com.ultreon.mods.advanceddebug.api.common

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import com.ultreon.mods.advanceddebug.api.util.MathUtil
import java.util.*

@JvmRecord
data class Angle(val degrees: Double) : IFormattable {
    override fun format(ctx: IFormatterContext) {
        ctx.number(MathUtil.roundTo(degrees, 5)).operator("°")
    }

    val radians: Double get() = Math.toRadians(degrees)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val (degrees1) = other as Angle
        return degrees1.compareTo(degrees) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(degrees)
    }
}

val Number.degrees get() = Angle(this.toDouble())
val Number.radians get() = Angle(Math.toDegrees(this.toDouble()))
