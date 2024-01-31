package com.ultreon.mods.advanceddebug.init.neoforge;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import net.minecraft.client.gui.components.Renderable;

public class ModOverlaysImpl {
    @SuppressWarnings("CodeBlock2Expr")
    public static void registerTop(String name, Renderable overlay) {
        ForgeOverlays.listen(event -> {
            AdvancedDebug.LOGGER.info("Registering mod overlay: " + name);
            event.registerAboveAll(name, (forgeGui, poseStack, tickDelta, x, y) -> {
                overlay.render(poseStack, Integer.MAX_VALUE, Integer.MAX_VALUE, tickDelta);
            });
        });
    }
}
