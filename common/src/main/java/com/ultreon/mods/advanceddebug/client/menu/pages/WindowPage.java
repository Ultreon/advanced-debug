package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.mixin.common.OptionsAccessor;
import com.ultreon.mods.advanceddebug.util.InputUtils;

public class WindowPage extends DebugPage {
    public WindowPage() {
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        ctx.left("Scale / Size");
        ctx.left("Gui Scale", getMultiplier(getMainWindow().getGuiScale()));
        ctx.left("Window Size", getSize(getMainWindow().getScreenWidth(), getMainWindow().getScreenHeight()));
        ctx.left("Window Size (Scaled)", getSize(getMainWindow().getGuiScaledWidth(), getMainWindow().getGuiScaledHeight()));
        ctx.left("Framebuffer Size", getSize(getMainWindow().getWidth(), getMainWindow().getHeight()));
        ctx.left();

        ctx.right("Misc");
        ctx.right("Refresh Rate", getMainWindow().getRefreshRate());
        ctx.right("Framerate Limit", getMainWindow().getFramerateLimit());
        ctx.right("Fullscreen Mode", getMainWindow().isFullscreen());
        ctx.right("Vsync", ((OptionsAccessor)mc.options).getEnableVsync());

        if (InputUtils.isAltDown()) {
            ctx.top("Please don't press Alt+F4"); // Just don't.
        }
    }
}
