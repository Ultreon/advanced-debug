package com.ultreon.mods.advanceddebug.init;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SameParameterValue")
public class ModOverlays {
    private static final List<IIngameOverlay> OVERLAYS = new ArrayList<>();


    private static void registerTop(String name, IIngameOverlay overlay) {
        OVERLAYS.add(OverlayRegistry.registerOverlayTop(AdvancedDebug.MOD_ID + ":" + name, overlay));
    }

    public static void registerAll() {
        registerTop("debug_info", DebugGui.get());
    }
}
