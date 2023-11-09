package com.ultreon.mods.advanceddebug.mixin.common;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugScreenOverlay.class)
public interface DebugScreenOverlayMixin {
    @Accessor
    boolean isRenderDebug();
}
