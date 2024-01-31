package com.ultreon.mods.advanceddebug.client.fabric;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.client.Config;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.fml.config.ModConfig;

public class ConfigImpl {
    public static void register(Object context) {
        NeoForgeConfigRegistry.INSTANCE.register(AdvancedDebug.MOD_ID, ModConfig.Type.CLIENT, Config.build());
    }
}
