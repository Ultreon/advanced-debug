package com.ultreon.mods.advanceddebug.api.common

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext

interface IFormattable {
    @Deprecated("")
    fun toFormattedString(): String {
        throw AssertionError("toFormattedString(IFormatterContext) is not implemented")
    }

    @Suppress("DEPRECATION")
    fun format(ctx: IFormatterContext) {
        ctx.direct(toFormattedString())
    }
}