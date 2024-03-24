package com.ultreon.mods.advanceddebug.init.neoforge;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.resources.ResourceLocation;

public class ModOverlaysImpl {
    @SuppressWarnings("CodeBlock2Expr")
    public static void registerTop(String name, Renderable overlay) {
        NeoForgeOverlays.listen(event -> {
            AdvancedDebug.LOGGER.info("Registering mod overlay: " + name);
            event.registerAboveAll(new ResourceLocation(IAdvancedDebug.MOD_ID, name), (forgeGui, poseStack, tickDelta, x, y) -> {
                overlay.render(poseStack, Integer.MAX_VALUE, Integer.MAX_VALUE, tickDelta);
            });
        });
    }
}
