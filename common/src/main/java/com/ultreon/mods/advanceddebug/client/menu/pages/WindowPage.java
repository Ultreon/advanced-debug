package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.platform.Window;
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
        Window window = getWindow();

        ctx.left("Scale / Size");
        ctx.left("Gui Scale", getMultiplier(window.getGuiScale()));
        ctx.left("Window Size", getSize(window.getScreenWidth(), window.getScreenHeight()));
        ctx.left("Window Size (Scaled)", getSize(window.getGuiScaledWidth(), window.getGuiScaledHeight()));
        ctx.left("Framebuffer Size", getSize(window.getWidth(), window.getHeight()));
        ctx.left();

        ctx.right("Misc");
        ctx.right("Refresh Rate", window.getRefreshRate());
        ctx.right("Framerate Limit", window.getFramerateLimit());
        ctx.right("Fullscreen Mode", window.isFullscreen());
        ctx.right("Vsync", ((OptionsAccessor) minecraft.options).getEnableVsync().get());

        if (InputUtils.isAltDown()) {
            ctx.top("Please don't press Alt+F4"); // Just don't.
        }
    }
}
