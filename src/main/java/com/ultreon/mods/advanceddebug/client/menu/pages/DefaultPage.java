package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.client.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;

public class DefaultPage extends DebugPage {
    private final Minecraft mc = Minecraft.getInstance();

    public DefaultPage() {
        super(AdvancedDebug.MOD_ID, "default");
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        if (Config.SHOW_FPS_ON_DEFAULT_PAGE.get()) {
            ctx.left("FPS", Minecraft.fps);
            IntegratedServer server;
            if (mc.hasSingleplayerServer() && (server = mc.getSingleplayerServer()) != null) {
                ctx.left("Server TPS", server.getTickCount());
            }
        }
    }
}
