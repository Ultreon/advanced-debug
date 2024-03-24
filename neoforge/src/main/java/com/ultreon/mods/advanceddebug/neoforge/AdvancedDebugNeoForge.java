package com.ultreon.mods.advanceddebug.neoforge;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import com.ultreon.mods.lib.client.gui.config.ConfigScreen;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;

@Mod(IAdvancedDebug.MOD_ID)
public class AdvancedDebugNeoForge {
    public AdvancedDebugNeoForge() {
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            AdvancedDebug advancedDebug = new AdvancedDebug();
            advancedDebug.init();

            ModLoadingContext ctx = ModLoadingContext.get();

            ctx.registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "anything. i don't care", (version, clientSide) -> clientSide));
            ctx.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, back) -> {
                return new ConfigScreen(advancedDebug.getConfig(), back);
            }));
        });
    }
}
