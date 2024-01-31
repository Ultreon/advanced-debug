package com.ultreon.mods.advanceddebug.mixin.common;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugScreenOverlay.class)
public interface DebugScreenOverlayAccessor {
    @Accessor
    boolean isRenderDebug();
}
