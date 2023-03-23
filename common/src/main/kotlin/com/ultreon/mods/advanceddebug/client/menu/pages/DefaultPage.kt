package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.AdvancedDebug
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import com.ultreon.mods.advanceddebug.client.Config
import com.ultreon.mods.advanceddebug.mixin.common.MinecraftAccessor

class DefaultPage : DebugPage(AdvancedDebug.modId, "default") {
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        if (Config.showFpsOnDefaultPage.get()) {
            ctx.left("FPS", MinecraftAccessor.getFps())
            if (minecraft.hasSingleplayerServer()) {
                minecraft.singleplayerServer?.let { server ->
                    ctx.left("Server TPS", server.tickCount)
                }
            }
        }
    }
}