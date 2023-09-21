package com.ultreon.mods.advanceddebug.mixin.fabric;

import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Redirect(method = "method_1454", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(III)Z", ordinal = 0))
    private static boolean advancedDebug$redirectKeyPress(Screen instance, int keyCode, int scanCode, int modifiers) {
        if (!(DebugGui.isImGuiFocused() && !Minecraft.getInstance().mouseHandler.isMouseGrabbed())) {
            instance.keyPressed(keyCode, scanCode, modifiers);
        }
        return true;
    }
    @Redirect(method = "method_1454", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyReleased(III)Z", ordinal = 0))
    private static boolean advancedDebug$redirectKeyRelease(Screen instance, int keyCode, int scanCode, int modifiers) {
        if (!(DebugGui.isImGuiFocused() && !Minecraft.getInstance().mouseHandler.isMouseGrabbed())) {
            instance.keyReleased(keyCode, scanCode, modifiers);
        }
        return true;
    }
    @Redirect(method = "method_1473", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/events/GuiEventListener;charTyped(CI)Z", ordinal = 0))
    private static boolean advancedDebug$redirectCharType$1(GuiEventListener instance, char codePoint, int modifiers) {
        if (!(DebugGui.isImGuiFocused() && !Minecraft.getInstance().mouseHandler.isMouseGrabbed())) {
            instance.charTyped(codePoint, modifiers);
        }
        return true;
    }
    @Redirect(method = "method_1458", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/events/GuiEventListener;charTyped(CI)Z", ordinal = 0))
    private static boolean advancedDebug$redirectCharType$2(GuiEventListener instance, char codePoint, int modifiers) {
        if (!(DebugGui.isImGuiFocused() && !Minecraft.getInstance().mouseHandler.isMouseGrabbed())) {
            instance.charTyped(codePoint, modifiers);
        }
        return true;
    }
}
