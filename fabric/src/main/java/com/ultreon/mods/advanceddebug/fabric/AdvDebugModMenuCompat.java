package com.ultreon.mods.advanceddebug.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.lib.client.gui.config.ConfigScreen;

public class AdvDebugModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return back -> {
            return new ConfigScreen(AdvancedDebug.getInstance().getConfig(), back)
        };
    }
}
