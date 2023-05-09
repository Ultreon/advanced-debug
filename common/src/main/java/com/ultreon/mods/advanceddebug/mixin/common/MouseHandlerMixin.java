package com.ultreon.mods.advanceddebug.mixin.common;

import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow private double xpos;
    @Shadow private double ypos;
    @Shadow private boolean mouseGrabbed;

    @Redirect(method = "onMove", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;xpos:D", ordinal = 0))
    private void advancedDebug$redirectSetXPos$1(MouseHandler instance, double value) {
        if (DebugGui.isImGuiHovered() && !mouseGrabbed) {
            xpos = Integer.MAX_VALUE;
        } else {
            xpos = value;
        }
    }
    @Redirect(method = "onMove", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;xpos:D", ordinal = 3))
    private void advancedDebug$redirectSetXPos$2(MouseHandler instance, double value) {
        if (DebugGui.isImGuiHovered() && !mouseGrabbed) {
            xpos = Integer.MAX_VALUE;
        } else {
            xpos = value;
        }
    }
    @Redirect(method = "onMove", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;ypos:D", ordinal = 0))
    private void advancedDebug$redirectSetYPos$1(MouseHandler instance, double value) {
        if (DebugGui.isImGuiHovered() && !mouseGrabbed) {
            ypos = Integer.MAX_VALUE;
        } else {
            ypos = value;
        }
    }
    @Redirect(method = "onMove", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;ypos:D", ordinal = 3))
    private void advancedDebug$redirectSetYPos$2(MouseHandler instance, double value) {
        if (DebugGui.isImGuiHovered() && !mouseGrabbed) {
            ypos = Integer.MAX_VALUE;
        } else {
            ypos = value;
        }
    }
}
