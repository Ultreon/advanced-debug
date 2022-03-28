package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import net.minecraft.client.Minecraft;

public class DefaultPage extends DebugPage {
    private final Minecraft mc = Minecraft.getInstance();

    public DefaultPage() {
        super(AdvancedDebug.MOD_ID, "default");
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        ctx.left("FPS", Minecraft.fps);
    }
}
