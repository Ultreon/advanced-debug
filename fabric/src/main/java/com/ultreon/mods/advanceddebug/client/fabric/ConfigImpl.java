package com.ultreon.mods.advanceddebug.client.fabric;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.client.Config;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

@SuppressWarnings("removal")
public class ConfigImpl {
    public static void register(Object context) {
        ModLoadingContext.registerConfig(AdvancedDebug.modId, ModConfig.Type.CLIENT, Config.build());
    }
}
