package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import com.ultreon.mods.advanceddebug.api.common.size
import com.ultreon.mods.advanceddebug.util.TargetUtils
import com.ultreon.mods.advanceddebug.util.registryName
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity

open class EntityPage(modId: String, name: String) : DebugPage(modId, name) {
    private val mc = Minecraft.getInstance()
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        val entityHit = TargetUtils.entity()
        if (entityHit != null) {
            val entity = entityHit.entity
            val type = entity.type
            val customName = entity.customName
            ctx.left(ChatFormatting.GRAY.toString() + "Entity Type")
            ctx.left("Height", type.height)
            ctx.left("Name", type.description.string)
            ctx.left("Size", size(type.dimensions.width, type.dimensions.height))
            ctx.left("Internal Name", type.registryName)
            ctx.left()
            ctx.left(ChatFormatting.GRAY.toString() + "Entity")
            ctx.left("Air", entity.airSupply)
            ctx.left("Max Air", entity.maxAirSupply)
            ctx.left("Eye Height", entity.eyeHeight)
            ctx.left("Look Angle", entity.lookAngle)
            ctx.left("Riding Entity", entity.vehicle)
            ctx.left("Riding Offset", entity.myRidingOffset)
            ctx.left("Entity UUID", entity.uuid)
            ctx.left("Entity ID", entity.id)
            ctx.left("Entity Name", entity.name.string)
            ctx.left("Custom Name", customName?.string)
            ctx.left("Nametag Visible", entity.isCustomNameVisible)
            ctx.left("Silent", entity.isSilent)
            ctx.left("Attackable", entity.isAttackable)
            ctx.left("Invulnerable", entity.isInvulnerable)
            ctx.left("Invisible", entity.isInvisible)
            ctx.left("Sneaking", entity.isCrouching)
            ctx.left("Sprinting", entity.isSprinting)
            ctx.left("Swimming", entity.isSwimming)
            ctx.left("Pushable", entity.isPushable)
            ctx.left("Pushed By Fluid", entity.isPushedByFluid)
            if (entity is LivingEntity) {
                ctx.right(ChatFormatting.GRAY.toString() + "Living Entity")
                ctx.right("Health", entity.health)
                ctx.right("Max. Health", entity.maxHealth)
                ctx.right("Main Hand", entity.getItemInHand(InteractionHand.MAIN_HAND))
                ctx.right("Off Hand", entity.getItemInHand(InteractionHand.OFF_HAND))
                ctx.right("Arrow Count", entity.arrowCount)
                ctx.right("Absorption Amount", entity.absorptionAmount)
                ctx.right("Speed", entity.speed)
                ctx.right("Last Used Hand", entity.usedItemHand)
                ctx.right("Attacking Entity", entity.killCredit)
                ctx.right("Item Usage Ticks Remaining", entity.useItemRemainingTicks)
                ctx.right("Last Attacked Mob", entity.lastHurtMob)
                ctx.right("Rope Position", entity.getRopeHoldPosition(minecraft.frameTime))
                ctx.right("Pose", entity.getPose())
                ctx.right("Scale", entity.scale)
                ctx.right("Armor Value", entity.armorValue)
            } else if (entity is ItemEntity) {
                ctx.right(ChatFormatting.GRAY.toString() + "Item Entity")
                ctx.right("Age", entity.age)
                ctx.right("Item", entity.item)
                ctx.right("Owner", entity.owner)
                ctx.right("Thrower", entity.thrower)
                ctx.right("Pose", entity.getPose())
            }
        } else {
            // not looking at a block, or too far away from one to tell
            ctx.top(ChatFormatting.RED.toString() + "<No Entity Was Found>")
        }
    }
}