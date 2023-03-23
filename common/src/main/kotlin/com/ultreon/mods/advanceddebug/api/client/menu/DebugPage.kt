package com.ultreon.mods.advanceddebug.api.client.menu

import com.mojang.blaze3d.platform.Window
import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.common.*
import com.ultreon.mods.advanceddebug.util.memoize
import dev.architectury.platform.Platform
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation

@Suppress("unused")
abstract class DebugPage(modId: String, name: String) {
    val minecraft: Minecraft = Minecraft.getInstance()
    protected val mainWindow: Window by memoize { minecraft.window }
    private val resourceLocation: ResourceLocation

    init {
        // Mod container.
        Platform.getMod(modId) ?: throw IllegalArgumentException("Mod not found with id: $modId")
        resourceLocation = ResourceLocation(modId, name)
    }

    abstract fun render(poseStack: PoseStack, ctx: IDebugRenderContext)
    fun registryName(): ResourceLocation {
        return resourceLocation
    }
}