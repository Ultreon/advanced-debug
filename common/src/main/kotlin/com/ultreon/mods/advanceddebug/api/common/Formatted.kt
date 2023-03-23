package com.ultreon.mods.advanceddebug.api.common

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext

@Suppress("unused")
class Formatted : IFormattable {
    val string: String

    constructor(string: String) {
        this.string = string
    }

    constructor(`object`: Any?) {
        string = `object`.toString()
    }

    constructor(c: Char) {
        string = c.toString()
    }

    constructor(b: Byte) {
        string = b.toString()
    }

    constructor(s: Short) {
        string = s.toString()
    }

    constructor(i: Int) {
        string = i.toString()
    }

    constructor(l: Long) {
        string = l.toString()
    }

    constructor(b: Boolean) {
        string = java.lang.Boolean.toString(b)
    }

    @Suppress("DEPRECATION")
    override fun format(ctx: IFormatterContext) {
        ctx.direct(string)
    }
}

val Any?.formatted get() = Formatted(this)
val String.formatted get() = Formatted(this)
val Char.formatted get() = Formatted(this)
val Byte.formatted get() = Formatted(this)
val Short.formatted get() = Formatted(this)
val Int.formatted get() = Formatted(this)
val Long.formatted get() = Formatted(this)
val Float.formatted get() = Formatted(this)
val Double.formatted get() = Formatted(this)
val Boolean.formatted get() = Formatted(this)