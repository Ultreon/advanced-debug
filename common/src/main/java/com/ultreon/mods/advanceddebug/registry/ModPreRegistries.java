package com.ultreon.mods.advanceddebug.registry;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.registries.v0.Registry;
import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;

public class ModPreRegistries {
    public static final Registry<DebugPage> DEBUG_PAGE = Registry.create(new Identifier(IAdvancedDebug.MOD_ID, "debug_page"));
}
