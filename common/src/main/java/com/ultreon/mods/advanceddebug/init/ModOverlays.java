package com.ultreon.mods.advanceddebug.init;

import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.gui.components.Renderable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SameParameterValue")
public final class ModOverlays {
    private static final List<Renderable> OVERLAYS = new ArrayList<>();

    private static void registerTop(String name, Renderable overlay) {
        ClientGuiEvent.RENDER_HUD.register((poseStack, tickDelta) ->
                overlay.render(poseStack, Integer.MAX_VALUE, Integer.MAX_VALUE, tickDelta));
    }

    public static void registerAll() {
        registerTop("debug_info", DebugGui.get());
    }
}
