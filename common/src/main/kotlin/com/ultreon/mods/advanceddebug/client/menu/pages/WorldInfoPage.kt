package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import com.ultreon.mods.advanceddebug.api.common.Formatted
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.Vec3i

class WorldInfoPage(modId: String, name: String) : DebugPage(modId, name) {
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        if (Minecraft.getInstance().level != null) {
            val dimensionInfo = Minecraft.getInstance().level!!.levelData
            val server = Minecraft.getInstance().singleplayerServer
            if (server != null) {
                ctx.left("Singleplayer")
                ctx.left("World Name", server.worldData.levelName)
                ctx.left()
            }
            ctx.left("Misc")
            ctx.left("Spawn Angle", dimensionInfo.spawnAngle)
            ctx.left("Difficulty", dimensionInfo.difficulty)
            ctx.left("Spawn", Vec3i(dimensionInfo.xSpawn, dimensionInfo.ySpawn, dimensionInfo.zSpawn))
            ctx.left(
                "Weather",
                Formatted(ChatFormatting.GOLD.toString() + if (dimensionInfo.isRaining) (if (dimensionInfo.isThundering) "storm" else "rain") else "clear")
            )
            ctx.left()
            ctx.right("Flags")
            ctx.right("Difficulty Locked", dimensionInfo.isDifficultyLocked)
            ctx.right("Hardcore", dimensionInfo.isHardcore)
            ctx.right("Raining", dimensionInfo.isRaining)
            ctx.right("Thundering", dimensionInfo.isThundering)
            ctx.right()
        }
    }
}