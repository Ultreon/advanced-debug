package com.ultreon.mods.advanceddebug.api.client.registry

import com.ultreon.mods.advanceddebug.api.client.menu.Formatter
import kotlin.reflect.KClass

interface IFormatterRegistry {
    fun <T : Any> register(formatter: Formatter<T>): Formatter<T>
    fun identify(`class`: Class<*>): Formatter<*>?
    fun identify(kClass: KClass<*>): Formatter<*>?
}