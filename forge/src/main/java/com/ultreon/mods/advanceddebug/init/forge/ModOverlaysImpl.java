package com.ultreon.mods.advanceddebug.init.forge;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import net.minecraft.client.gui.components.Renderable;

public class ModOverlaysImpl {
    @SuppressWarnings("CodeBlock2Expr")
    public static void registerTop(String name, Renderable overlay) {
        ForgeOverlays.listen(event -> {
            event.registerAboveAll(AdvancedDebug.MOD_ID + ":" + name, (forgeGui, poseStack, tickDelta, x, y) -> {
                overlay.render(poseStack, Integer.MAX_VALUE, Integer.MAX_VALUE, tickDelta);
            });
        });
    }
}
