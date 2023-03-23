package com.ultreon.mods.advanceddebug.api.client.menu

import com.ultreon.mods.advanceddebug.api.IAdvancedDebug
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import com.ultreon.mods.advanceddebug.api.common.IFormatter
import net.minecraft.resources.ResourceLocation
import kotlin.reflect.KClass

abstract class Formatter<T : Any>(private val clazz: KClass<T>, private val name: ResourceLocation) : IFormatter<T> {
    abstract override fun format(obj: T, context: IFormatterContext)
    fun formatOther(obj: Any?, context: IFormatterContext) {
        IAdvancedDebug.get().gui.format(obj, context)
    }

    fun javaClass(): Class<T> {
        return clazz.java
    }

    fun kotlinClass(): KClass<T> {
        return clazz
    }

    fun registryName(): ResourceLocation {
        return name
    }
}

fun <T : Any>formatter(clazz: KClass<T>, name: ResourceLocation, formatter: (T, IFormatterContext) -> Unit): Formatter<T> {
    return object : Formatter<T>(clazz, name) {
        override fun format(obj: T, context: IFormatterContext) {
            formatter(obj, context)
        }
    }
}
