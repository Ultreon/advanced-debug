package com.ultreon.mods.advanceddebug.client.registry

import com.ultreon.mods.advanceddebug.api.client.menu.Formatter
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry
import kotlin.reflect.KClass

class FormatterRegistry private constructor() : IFormatterRegistry {
    override fun <T : Any> register(formatter: Formatter<T>): Formatter<T> {
        val clazz: Class<*> = formatter.javaClass()
        FORMATTERS[clazz.name] = formatter
        return formatter
    }

    fun dump() {
        println("-=====- DEBUG FORMATTER REGISTRY DUMP -=====-")
        for ((key, value) in FORMATTERS) {
            println(key)
            println(value!!.registryName())
        }
        println("_______ DEBUG FORMATTER REGISTRY DUMP _______")
    }

    override fun identify(`class`: Class<*>): Formatter<*>? {
        var clazz: Class<*>? = `class`
        while (true) {
            if (FORMATTERS.containsKey(clazz!!.name)) {
                return FORMATTERS[clazz.name]
            }
            for (inter in clazz.interfaces) {
                val identify = identify(inter)
                if (identify != null) return identify
            }
            if (clazz.superclass != null) {
                clazz = clazz.superclass
            } else {
                return null
            }
        }
    }

    override fun identify(kClass: KClass<*>): Formatter<*>? {
        return identify(kClass.java)
    }

    companion object {
        private val FORMATTERS: MutableMap<String, Formatter<*>?> = HashMap()
        private val INSTANCE = FormatterRegistry()
        fun get(): FormatterRegistry {
            return INSTANCE
        }
    }
}