package com.ultreon.mods.advanceddebug.extension

import com.ultreon.mods.advanceddebug.AdvancedDebug
import com.ultreon.mods.advanceddebug.AdvancedDebug.logger
import com.ultreon.mods.advanceddebug.api.extension.Extension
import com.ultreon.mods.advanceddebug.api.extension.ExtensionInfo
import com.ultreon.mods.advanceddebug.client.menu.DebugGui
import org.slf4j.MarkerFactory
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class ExtensionLoader private constructor() {
    private var providers: List<ServiceLoader.Provider<Extension>>? = null
    fun scan() {
        val load = ServiceLoader.load(
            Extension::class.java
        )
        providers = load.stream().toList()
    }

    fun construct() {
        for (provider in providers!!) {
            val type = provider.type()
            val annotation = type.getAnnotation(
                ExtensionInfo::class.java
            )
            if (annotation == null) {
                logger.warn(MARKER, "Advanced Debug extension doesn't have info: " + type.name)
                continue
            }
            val value = annotation.value
            EXTENSIONS[value] = provider.get()
        }
    }

    fun makeSetup() {
        for ((key, ext) in EXTENSIONS) {
            logger.debug(MARKER, "Initiating extension of mod '" + key + "' for class: " + ext.javaClass.name)
            ext.initPages(DebugGui.get().createInitEvent())
            ext.initFormatters(AdvancedDebug.formatterRegistry)
        }
    }

    companion object {
        private val instance = ExtensionLoader()
        private val MARKER = MarkerFactory.getMarker("ExtensionLoader")
        private val EXTENSIONS: MutableMap<String, Extension> = HashMap()
        fun get(): ExtensionLoader {
            return instance
        }
    }
}