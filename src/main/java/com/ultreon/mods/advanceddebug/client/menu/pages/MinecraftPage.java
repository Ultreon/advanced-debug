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

        ctx.left("Version");
        ctx.left("Version", mc.getLaunchedVersion());
        ctx.left("Version Type", mc.getVersionType());
        ctx.left();

        ctx.left("Partial Ticking");
        ctx.left("Frame Time", mc.getFrameTime());
        ctx.left("Delta Frame Time", mc.getDeltaFrameTime());
        ctx.left();

        ctx.left("Game Renderer");
        ctx.left("Render Distance", mc.gameRenderer.getRenderDistance());
        ctx.left("Depth Far", mc.gameRenderer.getDepthFar());
        ctx.left("Panoramic", mc.gameRenderer.isPanoramicMode());
        ctx.left();

        ctx.right("Misc");
        ctx.right("Name", mc.name());
        ctx.right("Pending Tasks", mc.getPendingTasksCount());
        ctx.right("Open Screen", screen == null ? null : screen.getClass());
        ctx.right("Language", mc.getGame().getSelectedLanguage().getCode());
        ctx.right();

        ctx.right("Flags");
        ctx.right("64-Bit", mc.is64Bit());
        ctx.right("Enforce Unicode", mc.isEnforceUnicode());
        ctx.right("Demo Mode", mc.isDemo());
        ctx.right("Game Focused", mc.isWindowActive());
        ctx.right("Game Paused", mc.isPaused());
        ctx.right("Local Server", mc.isLocalServer());
        ctx.right("Singleplayer", mc.hasSingleplayerServer());
        ctx.right("Connected to Realms", mc.isConnectedToRealms());
        ctx.right();
    }
}
