package com.ultreon.mods.advanceddebug.neoforge;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.client.Config;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.IExtensionPoint.DisplayTest;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(AdvancedDebug.MOD_ID)
public class AdvancedDebugNeoForge {
    public AdvancedDebugNeoForge() {
        if (FMLEnvironment.dist != Dist.CLIENT) return;

        AdvancedDebug advancedDebug = new AdvancedDebug();
        advancedDebug.init();

        ModLoadingContext ctx = ModLoadingContext.get();
        Config.register(ctx);

        ctx.registerExtensionPoint(DisplayTest.class, () -> new DisplayTest(() -> "anything. i don't care", (version, clientSide) -> clientSide));
    }
}
