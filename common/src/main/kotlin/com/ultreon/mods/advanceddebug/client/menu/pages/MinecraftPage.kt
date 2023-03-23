package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext

class MinecraftPage(modId: String, name: String) : DebugPage(modId, name) {
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        val screen = minecraft.screen
        ctx.left("Version")
        ctx.left("Version", minecraft.launchedVersion)
        ctx.left("Version Type", minecraft.versionType)
        ctx.left()
        ctx.left("Partial Ticking")
        ctx.left("Frame Time", minecraft.frameTime)
        ctx.left("Delta Frame Time", minecraft.deltaFrameTime)
        ctx.left()
        ctx.left("Game Renderer")
        ctx.left("Render Distance", minecraft.gameRenderer.renderDistance)
        ctx.left("Depth Far", minecraft.gameRenderer.depthFar)
        ctx.left("Panoramic", minecraft.gameRenderer.isPanoramicMode)
        ctx.left()
        ctx.right("Misc")
        ctx.right("Name", minecraft.name())
        ctx.right("Pending Tasks", minecraft.pendingTasksCount)
        ctx.right("Open Screen", screen?.javaClass)
        ctx.right("Language", minecraft.languageManager.selected)
        ctx.right()
        ctx.right("Flags")
        ctx.right("64-Bit", minecraft.is64Bit)
        ctx.right("Enforce Unicode", minecraft.isEnforceUnicode)
        ctx.right("Demo Mode", minecraft.isDemo)
        ctx.right("Game Focused", minecraft.isWindowActive)
        ctx.right("Game Paused", minecraft.isPaused)
        ctx.right("Local Server", minecraft.isLocalServer)
        ctx.right("Singleplayer", minecraft.hasSingleplayerServer())
        ctx.right("Connected to Realms", minecraft.isConnectedToRealms)
        ctx.right()
    }
}