package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.util.InputUtils;

import static net.minecraft.ChatFormatting.BLUE;
import static net.minecraft.ChatFormatting.WHITE;

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
        ctx.right("Refresh Rate", getFormatted("" + BLUE + mainWindow.getRefreshRate() + WHITE + " fps"));
        ctx.right("Framerate Limit", getFormatted("" + BLUE + mainWindow.getFramerateLimit() + WHITE + " fps"));
        ctx.right("Fullscreen Mode", mainWindow.isFullscreen());
        ctx.right("Vsync", mc.options.enableVsync);

        if (InputUtils.isAltDown()) {
            ctx.top("Please don't press Alt+F4"); // Just don't.
        }
    }
}
