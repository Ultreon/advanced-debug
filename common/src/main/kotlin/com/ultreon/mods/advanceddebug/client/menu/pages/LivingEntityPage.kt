package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import com.ultreon.mods.advanceddebug.util.TargetUtils
import net.minecraft.ChatFormatting
import net.minecraft.world.entity.LivingEntity

class LivingEntityPage(modId: String, name: String) : EntityPage(modId, name) {
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        val entityHit = TargetUtils.entity()
        val entity = entityHit?.entity
        if (entityHit != null && entity is LivingEntity) {
            ctx.left("Properties")
            ctx.left("Health", entity.health)
            ctx.left("Max Health", entity.maxHealth)
            ctx.left("Absorption", entity.absorptionAmount)
            ctx.left("Armor Points", entity.armorValue)
            ctx.left("Arrows", entity.arrowCount)
            ctx.left("Time Fall-flying", entity.fallFlyingTicks)
            ctx.left("Jump Boost Power", entity.jumpBoostPower)
            ctx.left("Mob Type", entity.mobType)
            ctx.left("Idle For", entity.noActionTime)
            ctx.left("Scale", entity.scale)
            ctx.left("Speed", entity.speed)
            ctx.left("Voice Pitch", entity.voicePitch)
            ctx.left()
            ctx.left("Combat")
            ctx.left("In Combat", entity.combatTracker.isInCombat)
            ctx.left("Killer ID", entity.combatTracker.killerId)
            ctx.left("Combat Duration", entity.combatTracker.combatDuration)
            ctx.left("Death Message", entity.combatTracker.deathMessage)
            ctx.left()
            ctx.right("Equipment")
            ctx.right("Main Hand Item", entity.mainHandItem)
            ctx.right("Off Hand Item", entity.offhandItem)
            ctx.right("Used Hand", entity.usedItemHand)
            ctx.right("Head Rotation", entity.yHeadRot)
            ctx.right()
            ctx.right("Flags")
            ctx.right("Alive", entity.isAlive)
            ctx.right("Baby Variant", entity.isBaby)
            ctx.right("Affected By Potions", entity.isAffectedByPotions)
            ctx.right("Is Blocking", entity.isBlocking)
            ctx.right("Glowing", entity.isCurrentlyGlowing)
            ctx.right("Dead / Dying", entity.isDeadOrDying)
            ctx.right("Effective AI", entity.isEffectiveAi)
            ctx.right("Fall Flying", entity.isFallFlying)
            ctx.right("In Wall", entity.isInWall)
            ctx.right("Sensitive To Water", entity.isSensitiveToWater)
            ctx.right("Sleeping", entity.isSleeping)
            ctx.right("Using Item", entity.isUsingItem)
            ctx.right("Visually Swimming", entity.isVisuallySwimming)
            ctx.right()
        } else {
            // Yup, there isn't a living being there where you look at.
            ctx.top(ChatFormatting.RED.toString() + "<No Living Entity Was Found>")
        }
    }
}