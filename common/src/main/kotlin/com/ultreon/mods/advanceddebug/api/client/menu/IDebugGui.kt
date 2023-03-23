package com.ultreon.mods.advanceddebug.api.client.menu

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import com.ultreon.mods.advanceddebug.api.common.IFormatter

interface IDebugGui : IFormatter<Any?> {
    fun <T : DebugPage> registerPage(page: T): T
    val debugPage: DebugPage?
    var page: Int
    fun setPage(page: DebugPage)
    operator fun next()
    fun prev()
    override fun format(obj: Any?, context: IFormatterContext)
    val default: Formatter<Any>
}