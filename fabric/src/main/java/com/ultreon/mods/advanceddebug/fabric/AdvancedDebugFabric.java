package com.ultreon.mods.advanceddebug.fabric;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.client.Config;
import net.fabricmc.api.ClientModInitializer;

public class AdvancedDebugFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AdvancedDebug advancedDebug = new AdvancedDebug();
        advancedDebug.init();

        Config.register(null);
    }
}
