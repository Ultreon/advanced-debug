package com.ultreon.mods.advanceddebug.mixin.common;

import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ImageButton.class)
public interface ImageButtonAccessor {
    @Accessor
    WidgetSprites getSprites();
}
