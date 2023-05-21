package com.ultreon.mods.advanceddebug.mixin.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.*;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final
    Minecraft minecraft;
    private boolean wasTogglePressed = false;
    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void advancedDebug$injectImGuiInit(Minecraft minecraft, ItemInHandRenderer itemInHandRenderer, ResourceManager resourceManager, RenderBuffers renderBuffers, CallbackInfo ci) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        ImGui.createContext();
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.getFonts().addFontDefault();

        long windowHandle = minecraft.getWindow().getWindow();

        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init("#version 150");
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void advancedDebug$injectImGuiDispose(CallbackInfo ci) {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void advancedDebug$injectImGuiRender(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        boolean toggleKey = glfwGetKey(minecraft.getWindow().getWindow(), GLFW_KEY_F12) == GLFW_TRUE;
        if (wasTogglePressed && !toggleKey) {
            wasTogglePressed = false;
            DebugGui.SHOW_IM_GUI.set(!DebugGui.SHOW_IM_GUI.get());
        } else if (!wasTogglePressed && toggleKey) {
            wasTogglePressed = true;
        }
        DebugGui.renderImGui(imGuiGlfw, imGuiGl3);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V"))
    private void advancedDebug$redirectMouseForImGui(Screen instance, PoseStack poseStack, int i, int j, float f) {
        if (DebugGui.isImGuiHovered()) {
            instance.renderWithTooltip(poseStack, Integer.MAX_VALUE, Integer.MAX_VALUE, f);
        } else {
            instance.renderWithTooltip(poseStack, i, j, f);
        }
    }
}
