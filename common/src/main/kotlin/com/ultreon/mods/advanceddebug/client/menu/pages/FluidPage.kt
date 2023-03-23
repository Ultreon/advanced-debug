package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import com.ultreon.mods.advanceddebug.util.TargetUtils
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft

class FluidPage(modId: String, name: String) : DebugPage(modId, name) {
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        val lookingAt = TargetUtils.fluid()
        val player = MC.player ?: return
        if (lookingAt != null) {
            val pos = lookingAt.blockPos

            // now the coordinates you want are in pos. Example of use:
            val state = player.getLevel().getBlockState(pos).fluidState
            if (!state.isEmpty) {
                ctx.left("Fluid Related")
                ctx.left("Height", state.ownHeight)
                ctx.left("Amount", state.amount)
                ctx.left("Actual Height", state.type.getHeight(state, player.getLevel(), pos))
                try {
                    ctx.left("Filled Bucket", state.type.bucket)
                } catch (ignored: Throwable) {
                }
                ctx.left("Tick Rate", state.type.getTickDelay(player.getLevel()))
                ctx.left()
                ctx.right("Flags")
                ctx.right("Is Empty", state.isEmpty)
                ctx.right("Source", state.isSource)
                ctx.right()
            } else {
                // not looking at a fluid, or too far away from one to tell
                ctx.top(ChatFormatting.RED.toString() + "<No Fluid Was Found>")
            }
        } else {
            // not looking at a fluid, or too far away from one to tell
            ctx.top(ChatFormatting.RED.toString() + "<No Fluid Was Found>")
        }
    }

    companion object {
        private val MC = Minecraft.getInstance()
    }
}