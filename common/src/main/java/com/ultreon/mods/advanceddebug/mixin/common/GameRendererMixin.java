package com.ultreon.mods.advanceddebug.mixin.common;

import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import com.ultreon.mods.advanceddebug.events.GameRendererEvents;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.*;

@SuppressWarnings("DataFlowIssue")
@Mixin(value = GameRenderer.class, priority = 0)
public class GameRendererMixin {
    @Shadow @Final
    Minecraft minecraft;
    @Unique
    private boolean advanced_debug$wasTogglePressed = false;
    @Unique
    private static final ImGuiImplGlfw advanced_debug$imGuiGlfw = new ImGuiImplGlfw();
    @Unique
    private static final ImGuiImplGl3 advanced_debug$imGuiGl3 = new ImGuiImplGl3();

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

        advanced_debug$imGuiGlfw.init(windowHandle, true);
        advanced_debug$imGuiGl3.init("#version 150");
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void advancedDebug$injectImGuiDispose(CallbackInfo ci) {
        advanced_debug$imGuiGl3.dispose();
        advanced_debug$imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void advancedDebug$injectImGuiRender$return(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        boolean toggleKey = glfwGetKey(minecraft.getWindow().getWindow(), GLFW_KEY_F12) == GLFW_PRESS;
        if (advanced_debug$wasTogglePressed && !toggleKey) {
            advanced_debug$wasTogglePressed = false;

            if (glfwGetKey(this.minecraft.getWindow().getWindow(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
                DebugGui.SHOW_OBJECT_INSPECTION.set(!DebugGui.SHOW_OBJECT_INSPECTION.get());
                return;
            }
            DebugGui.SHOW_IM_GUI.set(!DebugGui.SHOW_IM_GUI.get());
        } else if (!advanced_debug$wasTogglePressed && toggleKey) {
            advanced_debug$wasTogglePressed = true;
        }
        DebugGui.renderImGui(advanced_debug$imGuiGlfw, advanced_debug$imGuiGl3);

        GameRendererEvents.POST_GAME_RENDER.invoker().onPostGameRender(minecraft, (GameRenderer)(Object)this, partialTicks, nanoTime, renderLevel);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void advancedDebug$injectImGuiRender$head(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        GameRendererEvents.PRE_GAME_RENDER.invoker().onPreGameRender(minecraft, (GameRenderer)(Object)this, partialTicks, nanoTime, renderLevel);
    }
}
