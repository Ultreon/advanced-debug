package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import com.ultreon.mods.advanceddebug.api.common.MoonPhase
import com.ultreon.mods.advanceddebug.api.common.asPercentage
import com.ultreon.mods.advanceddebug.api.common.degrees
import com.ultreon.mods.advanceddebug.util.color
import net.minecraft.client.Minecraft

class WorldPage(modId: String, name: String) : DebugPage(modId, name) {
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        if (Minecraft.getInstance().level != null) {
            val dimension = Minecraft.getInstance().level
            ctx.left("Properties")
            ctx.left("Difficulty", dimension!!.difficulty.displayName.string)
            ctx.left("Sea Level", dimension.seaLevel)
            ctx.left("Moon Phase", MoonPhase.fromIndex(dimension.moonPhase))
            ctx.left("Spawn Angle", dimension.levelData.spawnAngle.toDouble().degrees)
            ctx.left("Dimension", dimension.dimension().location())
            ctx.left()
            ctx.left("Colors")
            ctx.left("Cloud Color", dimension.getCloudColor(minecraft.frameTime).color)
            if (Minecraft.getInstance().player != null) {
                val player = Minecraft.getInstance().player
                ctx.left("Sky Color", dimension.getSkyColor(player!!.position(), minecraft.frameTime).color)
            }
            ctx.left()
            val skyDarken = dimension.getSkyDarken(minecraft.frameTime)
            ctx.left("Brightness")
            ctx.left("Star Brightness", dimension.getStarBrightness(minecraft.frameTime).asPercentage)
            ctx.left("Sun Brightness", skyDarken.asPercentage)
            ctx.left()
            ctx.right("Time & Weather")
            ctx.right("Day Time", dimension.dayTime % 24000)
            ctx.right("Game Time", dimension.dayTime)
            ctx.right("Days Played", dimension.dayTime / 24000)
            ctx.right("Is Day", skyDarken >= 0.7f)
            ctx.right("Is Night", skyDarken < 0.7f)
            ctx.right("Raining", dimension.isRaining)
            ctx.right("Thundering", dimension.isThundering)
            ctx.right()
            ctx.right("Flags")
            ctx.right("No Saving", dimension.noSave())
            ctx.right("Debug World", dimension.isDebug)
            ctx.right()
            ctx.right("Misc")
            ctx.right("Lightning Flash Time", dimension.skyFlashTime)
            ctx.right("Chunk Provider", dimension.gatherChunkSourceStats())
            ctx.right("Loaded Entities", dimension.entityCount)
            ctx.right()
        }
    }
}