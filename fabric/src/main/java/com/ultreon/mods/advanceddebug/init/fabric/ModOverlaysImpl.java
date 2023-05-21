package com.ultreon.mods.advanceddebug.init.fabric;

import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.gui.components.Renderable;

public class ModOverlaysImpl {
    public static void registerTop(String name, Renderable overlay) {
        ClientGuiEvent.RENDER_HUD.register((poseStack, tickDelta) -> overlay.render(poseStack, Integer.MAX_VALUE, Integer.MAX_VALUE, tickDelta));
    }
}
