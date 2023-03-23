package com.ultreon.mods.advanceddebug.forge

import com.ultreon.mods.advanceddebug.AdvancedDebug
import com.ultreon.mods.advanceddebug.client.Config
import dev.architectury.platform.forge.EventBuses
import net.minecraftforge.fml.IExtensionPoint
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.KotlinModLoadingContext

@Mod(AdvancedDebug.modId)
object AdvancedDebugForge {
    init {
        AdvancedDebug.init()
        val ctx = ModLoadingContext.get()
        EventBuses.registerModEventBus(AdvancedDebug.modId, KotlinModLoadingContext.get().getKEventBus())

        Config.register(ctx)
        ctx.registerExtensionPoint(IExtensionPoint.DisplayTest::class.java) {
            IExtensionPoint.DisplayTest({ "anything. i don't care" }) { _: String?, clientSide: Boolean -> clientSide }
        }
    }
}