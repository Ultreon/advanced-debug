package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import com.ultreon.mods.advanceddebug.api.common.multiplier
import com.ultreon.mods.advanceddebug.util.TargetUtils
import com.ultreon.mods.advanceddebug.util.color
import com.ultreon.mods.advanceddebug.util.registryName
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.world.level.block.state.properties.Property
import java.util.function.Consumer

class BlockPage(modId: String, name: String) : DebugPage(modId, name) {
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        val instance = Minecraft.getInstance()
        var lookingAt = TargetUtils.block()
        val player = instance.player ?: return
        if (lookingAt != null) {
            val pos = lookingAt.blockPos

            // now the coordinates you want are in pos. Example of use:
            val state = player.getLevel().getBlockState(pos)
            val block = state.block
            ctx.left("Block Related")
            ctx.left("Type", block.registryName)
            ctx.left("Translated Name", block.name.string)
            ctx.left("Block Hardness", state.getDestroySpeed(player.getLevel(), pos))
            ctx.left("Light Value", state.lightEmission)
            ctx.left("Opacity", state.getLightBlock(player.getLevel(), pos))
            ctx.left("Offset", state.getOffset(player.getLevel(), pos))
            ctx.left("Mining Efficiency", state.getDestroyProgress(player, player.getLevel(), pos))
            ctx.left("Requires Tool", state.requiresCorrectToolForDrops())
            ctx.left("Render Type", state.renderShape)
            ctx.left("Jump Factor", block.jumpFactor)
            ctx.left("Target", block.lootTable)
            ctx.left("Color", block.defaultMaterialColor().id, block.defaultMaterialColor().col.color)
            ctx.left("Default Slipperiness", block.friction)
            ctx.left("Speed Factor", block.speedFactor.multiplier)
            ctx.left()
            val properties = state.properties
            if (!properties.isEmpty()) {
                ctx.right("Block Properties")
                properties.forEach(Consumer { key: Property<*> ->
                    try {
                        ctx.right(key.name, state.getValue(key))
                    } catch (e: Exception) {
                        ctx.right(key.name, ChatFormatting.RED.toString() + "Error")
                    }
                })
                ctx.right()
            }
        } else {
            // not looking at a block, or too far away from one to tell
            ctx.top(ChatFormatting.RED.toString() + "<No Block Was Found>")
        }
        lookingAt = TargetUtils.fluid()
        if (lookingAt != null) {
            val pos = lookingAt.blockPos

            // now the coordinates you want are in pos. Example of use:
            val state = player.getLevel().getBlockState(pos).fluidState
            ctx.right("Fluid Related")
            if (!state.isEmpty) {
                ctx.right("Is Empty", state.isEmpty)
                ctx.right("Height", state.ownHeight)
                ctx.right("Amount", state.amount)
                ctx.right("Actual Height", state.type.getHeight(state, player.getLevel(), pos))
                try {
                    ctx.right("Filled Bucket", state.type.bucket)
                } catch (ignored: Throwable) {
                }
                ctx.right("Tick Rate", state.type.getTickDelay(player.getLevel()))
            } else {
                // not looking at a fluid, or too far away from one to tell
                ctx.right(ChatFormatting.RED.toString() + "<No Fluid Was Found>")
            }
            ctx.right()
        }
    }
}