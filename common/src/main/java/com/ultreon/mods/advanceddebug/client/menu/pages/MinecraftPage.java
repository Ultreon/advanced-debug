package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

public class MinecraftPage extends DebugPage {
    public MinecraftPage() {
    }

    @Override
    public void render(@NotNull GuiGraphics gfx, IDebugRenderContext ctx) {
        Screen screen = minecraft.screen;

        ctx.left("Version");
        ctx.left("Version", minecraft.getLaunchedVersion());
        ctx.left("Version Type", minecraft.getVersionType());
        ctx.left();

        ctx.left("Partial Ticking");
        ctx.left("Frame Time", minecraft.getFrameTime());
        ctx.left("Delta Frame Time", minecraft.getDeltaFrameTime());
        ctx.left();

        ctx.left("Game Renderer");
        ctx.left("Render Distance", minecraft.gameRenderer.getRenderDistance());
        ctx.left("Depth Far", minecraft.gameRenderer.getDepthFar());
        ctx.left("Panoramic", minecraft.gameRenderer.isPanoramicMode());
        ctx.left();

        ctx.right("Misc");
        ctx.right("Name", minecraft.name());
        ctx.right("Pending Tasks", minecraft.getPendingTasksCount());
        ctx.right("Open Screen", screen == null ? null : screen.getClass());
        ctx.right("Language", minecraft.getLanguageManager().getSelected());
        ctx.right();

        ctx.right("Flags");
        ctx.right("64-Bit", minecraft.is64Bit());
        ctx.right("Enforce Unicode", minecraft.isEnforceUnicode());
        ctx.right("Demo Mode", minecraft.isDemo());
        ctx.right("Game Focused", minecraft.isWindowActive());
        ctx.right("Game Paused", minecraft.isPaused());
        ctx.right("Local Server", minecraft.isLocalServer());
        ctx.right("Singleplayer", minecraft.hasSingleplayerServer());
        ctx.right();
    }
}
