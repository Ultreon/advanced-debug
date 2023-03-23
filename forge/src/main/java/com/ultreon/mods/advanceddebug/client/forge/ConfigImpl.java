package com.ultreon.mods.advanceddebug.client.forge;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigImpl {
    public static void register(Object context){
        if (context instanceof ModLoadingContext ctx) {
            ctx.registerConfig(ModConfig.Type.CLIENT, com.ultreon.mods.advanceddebug.client.Config.build());
        }
    }
}
