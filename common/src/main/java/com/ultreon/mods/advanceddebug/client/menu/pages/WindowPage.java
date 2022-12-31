package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.mixin.common.OptionsAccessor;
import com.ultreon.mods.advanceddebug.util.InputUtils;

public class WindowPage extends DebugPage {
    public WindowPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        ctx.left("Scale / Size");
        ctx.left("Gui Scale", getMultiplier(mainWindow.getGuiScale()));
        ctx.left("Window Size", getSize(mainWindow.getScreenWidth(), mainWindow.getScreenHeight()));
        ctx.left("Window Size (Scaled)", getSize(mainWindow.getGuiScaledWidth(), mainWindow.getGuiScaledHeight()));
        ctx.left("Framebuffer Size", getSize(mainWindow.getWidth(), mainWindow.getHeight()));
        ctx.left();

        ctx.right("Misc");
        ctx.right("Refresh Rate", mainWindow.getRefreshRate());
        ctx.right("Framerate Limit", mainWindow.getFramerateLimit());
        ctx.right("Fullscreen Mode", mainWindow.isFullscreen());
        ctx.right("Vsync", ((OptionsAccessor)mc.options).getEnableVsync());

        if (InputUtils.isAltDown()) {
            ctx.top("Please don't press Alt+F4"); // Just don't.
        }
    }
}
