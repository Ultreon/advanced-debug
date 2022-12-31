package com.ultreon.mods.advanceddebug.forge;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.client.Config;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(AdvancedDebug.MOD_ID)
public class AdvancedDebugForge {
    public AdvancedDebugForge() {
        AdvancedDebug advancedDebug = new AdvancedDebug();
        advancedDebug.init();

        ModLoadingContext ctx = ModLoadingContext.get();
        Config.register(ctx);

        ctx.registerExtensionPoint(DisplayTest.class, () -> new DisplayTest(() -> "anything. i don't care", (version, clientSide) -> clientSide));
    }
}
