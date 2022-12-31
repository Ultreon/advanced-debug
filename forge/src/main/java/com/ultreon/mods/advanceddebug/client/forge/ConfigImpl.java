package com.ultreon.mods.advanceddebug.client.forge;

import com.ultreon.mods.advanceddebug.client.Config;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigImpl {
    public static void register(Object context) {
        if (context instanceof ModLoadingContext ctx) {
            ctx.registerConfig(ModConfig.Type.CLIENT, Config.build());
        }
    }
}
