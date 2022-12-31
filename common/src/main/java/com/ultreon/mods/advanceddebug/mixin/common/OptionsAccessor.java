package com.ultreon.mods.advanceddebug.mixin.common;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Options.class)
public interface OptionsAccessor {
    @Accessor
    OptionInstance<Boolean> getEnableVsync();
}
