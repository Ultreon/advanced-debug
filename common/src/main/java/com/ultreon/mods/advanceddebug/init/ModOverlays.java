package com.ultreon.mods.advanceddebug.init;

import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.gui.components.Renderable;

@SuppressWarnings("SameParameterValue")
public final class ModOverlays {
    @ExpectPlatform
    public static void registerTop(String name, Renderable overlay) {
        throw new AssertionError();
    }

    public static void registerAll() {
        registerTop("debug_info", DebugGui.get());
    }
}
