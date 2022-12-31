package com.ultreon.mods.advanceddebug.mixin.common;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor
    static int getFps() {
        throw new AssertionError();
    }
}
