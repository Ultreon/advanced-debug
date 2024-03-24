package com.ultreon.mods.advanceddebug.mixin.neoforge;

import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.ClientHooks;
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
            instance.keyPressed(keyCode, scanCode, modifiers);
        }
        return true;
    }
    @Redirect(method = "lambda$charTyped$6", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;charTyped(CI)Z", ordinal = 0))
    private static boolean advancedDebug$redirectCharTyped(Screen guiScreen, char codePoint, int modifiers) {
        if (!(DebugGui.isImGuiFocused() && !Minecraft.getInstance().mouseHandler.isMouseGrabbed())) {
            guiScreen.charTyped(codePoint, modifiers);
        }
        return true;
    }
    @Redirect(method = "lambda$charTyped$6", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;onScreenCharTypedPre(Lnet/minecraft/client/gui/screens/Screen;CI)Z", ordinal = 0, remap = false))
    private static boolean advancedDebug$redirectForgeCharTypedPre(Screen guiScreen, char codePoint, int modifiers) {
        if (!(DebugGui.isImGuiFocused() && !Minecraft.getInstance().mouseHandler.isMouseGrabbed())) {
            ClientHooks.onScreenCharTypedPre(guiScreen, codePoint, modifiers);
        }
        return true;
    }
    @Redirect(method = "lambda$charTyped$6", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;onScreenCharTypedPost(Lnet/minecraft/client/gui/screens/Screen;CI)V", ordinal = 0, remap = false))
    private static void advancedDebug$redirectForgeCharTypedPost(Screen guiScreen, char codePoint, int modifiers) {
        if (!(DebugGui.isImGuiFocused() && !Minecraft.getInstance().mouseHandler.isMouseGrabbed())) {
            ClientHooks.onScreenCharTypedPost(guiScreen, codePoint, modifiers);
        }
    }
    @Redirect(method = "lambda$charTyped$7", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;onScreenCharTypedPre(Lnet/minecraft/client/gui/screens/Screen;CI)Z", ordinal = 0, remap = false))
    private static boolean advancedDebug$redirectCharTyped2(Screen guiScreen, char codePoint, int modifiers) {
        if (!(DebugGui.isImGuiFocused() && !Minecraft.getInstance().mouseHandler.isMouseGrabbed())) {
            guiScreen.charTyped(codePoint, modifiers);
        }
        return true;
    }

    @Redirect(method = "lambda$charTyped$7", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;onScreenCharTypedPost(Lnet/minecraft/client/gui/screens/Screen;CI)V", ordinal = 0, remap = false))
    private static void advancedDebug$redirectForgeCharTypedPost2(Screen guiScreen, char codePoint, int modifiers) {
        if (!(DebugGui.isImGuiFocused() && !Minecraft.getInstance().mouseHandler.isMouseGrabbed())) {
            ClientHooks.onScreenCharTypedPost(guiScreen, codePoint, modifiers);
        }
    }
}
