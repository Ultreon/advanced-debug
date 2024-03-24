package com.ultreon.mods.advanceddebug.mixin.forge;

import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("UnstableApiUsage")
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;drawScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V", remap = false))
    private void advancedDebug$redirectMouseForImGui(Screen instance, @NotNull GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        if (DebugGui.isImGuiHovered()) {
            ForgeHooksClient.drawScreen(instance, gfx, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTicks);
        } else {
            ForgeHooksClient.drawScreen(instance, gfx, mouseX, mouseY, partialTicks);
        }
    }
}
