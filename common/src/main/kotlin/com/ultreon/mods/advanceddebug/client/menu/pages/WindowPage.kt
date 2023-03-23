package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import com.ultreon.mods.advanceddebug.api.common.multiplier
import com.ultreon.mods.advanceddebug.api.common.size
import com.ultreon.mods.advanceddebug.mixin.common.OptionsAccessor
import com.ultreon.mods.advanceddebug.util.InputUtils

class WindowPage(modId: String, name: String) : DebugPage(modId, name) {
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        ctx.left("Scale / Size")
        ctx.left("Gui Scale", mainWindow.guiScale.multiplier)
        ctx.left("Window Size", size(mainWindow.screenWidth, mainWindow.screenHeight))
        ctx.left("Window Size (Scaled)", size(mainWindow.guiScaledWidth, mainWindow.guiScaledHeight))
        ctx.left("Framebuffer Size", size(mainWindow.width, mainWindow.height))
        ctx.left()
        ctx.right("Misc")
        ctx.right("Refresh Rate", mainWindow.refreshRate)
        ctx.right("Framerate Limit", mainWindow.framerateLimit)
        ctx.right("Fullscreen Mode", mainWindow.isFullscreen)
        ctx.right("Vsync", (minecraft.options as OptionsAccessor).enableVsync)
        if (InputUtils.isAltDown) {
            ctx.top("Please don't press Alt+F4") // Just don't.
        }
    }
}