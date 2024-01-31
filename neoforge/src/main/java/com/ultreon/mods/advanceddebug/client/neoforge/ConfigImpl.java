package com.ultreon.mods.advanceddebug.client.neoforge;

import com.ultreon.mods.advanceddebug.client.Config;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;

public class ConfigImpl {
    public static void register(Object context) {
        if (context instanceof ModLoadingContext ctx) {
            ctx.registerConfig(ModConfig.Type.CLIENT, Config.build());
        }
    }
}
