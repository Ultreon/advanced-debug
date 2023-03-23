package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import com.ultreon.mods.advanceddebug.util.TargetUtils
import net.minecraft.ChatFormatting
import net.minecraft.world.entity.item.ItemEntity

class ItemEntityPage(modId: String, name: String) : EntityPage(modId, name) {
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        val entityHit = TargetUtils.entity()
        val entity = entityHit?.entity
        if (entityHit != null && entity is ItemEntity) {
            ctx.left("Generic")
            ctx.left("Thrower", entity.thrower)
            ctx.left("Owner", entity.owner)
            ctx.left("Item", entity.item)
            ctx.left()
            ctx.right("Flags")
            ctx.right("Attackable", entity.isAttackable)
            ctx.right()
        } else {
            // not looking at a block, or too far away from one to tell
            ctx.top(ChatFormatting.RED.toString() + "<No Item Entity Was Found>")
        }
    }
}