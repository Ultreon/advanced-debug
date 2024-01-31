package com.ultreon.mods.advanceddebug.mixin.neoforge;

import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.neoforge.client.ClientHooks;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;drawScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void advancedDebug$redirectMouseForImGui(Screen instance, @NotNull GuiGraphics gfx, int i, int j, float f) {
        if (DebugGui.isImGuiHovered()) {
            ClientHooks.drawScreen(instance, gfx, Integer.MAX_VALUE, Integer.MAX_VALUE, f);
        } else {
            ClientHooks.drawScreen(instance, gfx, i, j, f);
        }
    }
}
