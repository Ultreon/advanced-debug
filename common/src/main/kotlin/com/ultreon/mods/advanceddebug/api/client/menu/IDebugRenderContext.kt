package com.ultreon.mods.advanceddebug.api.client.menu

import net.minecraft.network.chat.Component

interface IDebugRenderContext {
    fun left(text: Component, `object`: Any?, vararg objects: Any?)
    fun left(text: String, `object`: Any?, vararg objects: Any?)
    fun left(text: Component)
    fun left(text: String)
    fun left()
    fun right(text: Component, `object`: Any?, vararg objects: Any?)
    fun right(text: String, `object`: Any?, vararg objects: Any?)
    fun right(text: Component)
    fun right(text: String)
    fun right()
    fun top(text: Component)
    fun top(text: String)
    fun top()
}