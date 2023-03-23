package com.ultreon.mods.advanceddebug.api.common

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import java.util.*
import kotlin.math.roundToLong

/**
 * Percentage class, used for get percentage value or normalized value.<br></br>
 * Use [getPercentage][.getPercentage] or [setPercentage][.setPercentage] for getting / setting the percentage value.<br></br>
 * Use [getValue][.getValue] or [setValue][.setValue] for getting / settings the normalized value.<br></br>
 * <br></br>
 *
 * @author Qboi123
 */
@Suppress("MemberVisibilityCanBePrivate")
class Percentage : IFormattable {
    var percentage: Double

    constructor(percentage: Double) {
        this.percentage = percentage
    }

    constructor(percentage: Int) {
        this.percentage = percentage.toDouble()
    }

    override fun format(ctx: IFormatterContext) {
        ctx.number(percentage.roundToLong()).operator("%")
    }

    var value: Double
        get() = percentage / 100
        set(value) {
            percentage = value * 100
        }

    val asMultiplier: Multiplier get() = (percentage / 100).multiplier

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Percentage
        return that.percentage.compareTo(percentage) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(percentage)
    }

    override fun toString(): String {
        return "Percentage{" +
                "percentage=" + percentage +
                '}'
    }
}

val Double.asPercentage get() = (this * 100).percent
val Number.asPercentage get() = (this.toDouble() * 100).percent
val Double.percent get() = Percentage(this)
val Number.percent get() = this.toDouble().percent
