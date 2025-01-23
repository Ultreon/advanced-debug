package com.ultreon.mods.advanceddebug.mixin.common;

import com.mojang.blaze3d.platform.Window;
import dev.architectury.platform.Platform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {
    @Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
    private void onSetIcon(CallbackInfo ci) {
        // Workaround for Linux on wayland. (Only in development environment)
        if (Platform.isDevelopmentEnvironment() && System.getProperty("os.name").toLowerCase().contains("linux")) {
            ci.cancel();
        }
    }
}
