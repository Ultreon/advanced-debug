package com.ultreon.mods.advanceddebug.api.common

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext

fun interface IFormatter<T> {
    fun format(obj: T, context: IFormatterContext)

    companion object {
        fun <T : IFormattable?> create(): IFormatter<T> {
            return IFormatter { obj: T, ctx: IFormatterContext ->
                obj?.format(
                    ctx
                ) ?: ctx.keyword("null")
            }
        }
    }
}