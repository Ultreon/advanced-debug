package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.client.menu.DebugRenderContext;
import net.minecraft.client.Minecraft;

public class DefaultPage extends DebugPage {
    private final Minecraft mc = Minecraft.getInstance();

    public DefaultPage() {
        super(AdvancedDebug.MOD_ID, "default");
    }

    @Override
    public void render(PoseStack poseStack, DebugRenderContext ctx) {
        ctx.left("FPS", Minecraft.fps);
    }
}
