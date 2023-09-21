package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.client.Config;
import com.ultreon.mods.advanceddebug.mixin.common.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.server.IntegratedServer;
import org.jetbrains.annotations.NotNull;

public class DefaultPage extends DebugPage {
    private final Minecraft mc = Minecraft.getInstance();

    public DefaultPage() {
        super();
    }

    @Override
    public void render(@NotNull GuiGraphics gfx, IDebugRenderContext ctx) {
        if (Config.SHOW_FPS_ON_DEFAULT_PAGE.get()) {
            ctx.left("FPS", MinecraftAccessor.getFps());
            IntegratedServer server;
            if (mc.hasSingleplayerServer() && (server = mc.getSingleplayerServer()) != null) {
                ctx.left("Server TPS", server.getTickCount());
            }
        }
    }
}
