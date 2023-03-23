package com.ultreon.mods.advanceddebug.fabric

import com.ultreon.mods.advanceddebug.AdvancedDebug
import com.ultreon.mods.advanceddebug.client.Config
import dev.architectury.utils.EnvExecutor
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer

object AdvancedDebugFabric : ModInitializer {
    override fun onInitialize() {
        EnvExecutor.runInEnv(EnvType.CLIENT) {
            Runnable {
                AdvancedDebug.init()
                Config.register(null)
            }
        }
    }
}