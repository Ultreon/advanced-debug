package com.ultreon.mods.advanceddebug.forge;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import com.ultreon.mods.lib.client.gui.config.ConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(IAdvancedDebug.MOD_ID)
public class AdvancedDebugForge {
    public AdvancedDebugForge() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            AdvancedDebug advancedDebug = new AdvancedDebug();
            advancedDebug.init();

            ModLoadingContext ctx = ModLoadingContext.get();

            ctx.registerExtensionPoint(DisplayTest.class, () -> new DisplayTest(() -> "anything. i don't care", (version, clientSide) -> clientSide));
            ctx.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, back) -> {
                return new ConfigScreen(advancedDebug.getConfig(), back);
            }));
        });
    }
}
