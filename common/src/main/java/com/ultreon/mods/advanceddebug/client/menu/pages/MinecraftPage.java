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
        Screen screen = this.minecraft.screen;

        ctx.left("Version");
        ctx.left("Version", this.minecraft.getLaunchedVersion());
        ctx.left("Version Type", this.minecraft.getVersionType());
        ctx.left();

        ctx.left("Partial Ticking");
        ctx.left("Frame Time", this.minecraft.getFrameTime());
        ctx.left("Delta Frame Time", this.minecraft.getDeltaFrameTime());
        ctx.left();

        ctx.left("Game Renderer");
        ctx.left("Render Distance", this.minecraft.gameRenderer.getRenderDistance());
        ctx.left("Depth Far", this.minecraft.gameRenderer.getDepthFar());
        ctx.left("Panoramic", this.minecraft.gameRenderer.isPanoramicMode());
        ctx.left();

        ctx.right("Misc");
        ctx.right("Name", this.minecraft.name());
        ctx.right("Pending Tasks", this.minecraft.getPendingTasksCount());
        ctx.right("Open Screen", screen == null ? null : screen.getClass());
        ctx.right("Language", this.minecraft.getLanguageManager().getSelected());
        ctx.right();

        ctx.right("Flags");
        ctx.right("64-Bit", this.minecraft.is64Bit());
        ctx.right("Enforce Unicode", this.minecraft.isEnforceUnicode());
        ctx.right("Demo Mode", this.minecraft.isDemo());
        ctx.right("Game Focused", this.minecraft.isWindowActive());
        ctx.right("Game Paused", this.minecraft.isPaused());
        ctx.right("Local Server", this.minecraft.isLocalServer());
        ctx.right("Singleplayer", this.minecraft.hasSingleplayerServer());
        ctx.right();
    }
}
