package com.ultreon.mods.advanceddebug.client.fabric;

import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import com.ultreon.mods.advanceddebug.client.Config;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigImpl {
    public static void register(Object context) {
        ForgeConfigRegistry.INSTANCE.register(IAdvancedDebug.MOD_ID, ModConfig.Type.CLIENT, Config.build());
    }
}
