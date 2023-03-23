package com.ultreon.mods.advanceddebug

import com.ultreon.mods.advanceddebug.api.IAdvancedDebug
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugGui
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry
import com.ultreon.mods.advanceddebug.api.init.ModDebugFormatters.Companion.initClass
import com.ultreon.mods.advanceddebug.client.Config
import com.ultreon.mods.advanceddebug.client.input.KeyBindingList.register
import com.ultreon.mods.advanceddebug.client.menu.DebugGui
import com.ultreon.mods.advanceddebug.client.registry.FormatterRegistry
import com.ultreon.mods.advanceddebug.extension.ExtensionLoader
import com.ultreon.mods.advanceddebug.init.ModDebugPages
import com.ultreon.mods.advanceddebug.init.ModOverlays
import dev.architectury.event.EventResult
import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.client.ClientRawInputEvent
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AdvancedDebug : IAdvancedDebug {
    const val modId = "advanced_debug"

    // Directly reference a log4j logger.
    @JvmField
    val logger: Logger = LoggerFactory.getLogger("AdvancedDebug")
    private var instance: AdvancedDebug = this
    private val loader = ExtensionLoader.get()

    fun init() {
        ClientLifecycleEvent.CLIENT_SETUP.register(ClientLifecycleEvent.ClientState { client: Minecraft -> setup(client) })
        ClientRawInputEvent.KEY_PRESSED.register(ClientRawInputEvent.KeyPressed { client: Minecraft, keyCode: Int, scanCode: Int, action: Int, modifiers: Int ->
            keyPressed(
                client,
                keyCode,
                scanCode,
                action,
                modifiers
            )
        })
        register()
        if (System.getenv("RT_DEBUG_FORMATTER_DUMP") != null) {
            FormatterRegistry.get().dump()
        }
        loader.scan()
        loader.construct()
    }

    override val gui: IDebugGui
        get() = DebugGui.get()
    override val formatterRegistry: IFormatterRegistry
        get() = FormatterRegistry.get()

    private fun setup(client: Minecraft?) {
        if (client == null) {
            return
        }
        logger.debug("Doing client side setup rn.")
        logger.debug("Registering modded overlays...")
        ModOverlays.registerAll()
        initClass()
        logger.debug("Setting up extensions...")
        loader.makeSetup()
        logger.debug("Client side setup done!")
        ModDebugPages.init()
    }

    override val isSpacedNamespace: Boolean
        get() = Config.spacedNamespaces.get()
    override val isSpacedEnumConstants: Boolean
        get() = Config.spacedEnumConstants.get()

    override fun enableBubbleBlasterID(): Boolean {
        return Config.enableBubbleBlasterId.get()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun keyPressed(
        client: Minecraft,
        keyCode: Int,
        scanCode: Int,
        action: Int,
        modifiers: Int
    ): EventResult {
        return if (DebugGui.get().onKeyReleased(keyCode, scanCode, action, modifiers)) {
            EventResult.interruptTrue()
        } else EventResult.pass()
    }

    fun res(path: String): ResourceLocation {
        return ResourceLocation(modId, path)
    }

    @Deprecated("Is now an 'object'", ReplaceWith("AdvancedDebug"))
    @JvmStatic
    fun get(): AdvancedDebug {
        return AdvancedDebug
    }
}