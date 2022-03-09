package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import net.minecraft.client.gui.screens.Screen;

public class MinecraftPage extends DebugPage {
    public MinecraftPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        Screen screen = mc.screen;

        ctx.left("Name", mc.name());
        ctx.left("Version", mc.getLaunchedVersion());
        ctx.left("Version Type", mc.getVersionType());
        ctx.left("Enforce Unicode", mc.isEnforceUnicode());
        ctx.left("Frame Time", mc.getFrameTime());
        ctx.left("Pending Tasks", mc.getPendingTasksCount());
        ctx.left("Open Screen", screen == null ? null : screen.getClass());

        ctx.right("Demo Mode", mc.isDemo());
        ctx.right("Game Focused", mc.isWindowActive());
        ctx.right("Game Paused", mc.isPaused());
        ctx.right("Local Server", mc.isLocalServer());
    }
}
