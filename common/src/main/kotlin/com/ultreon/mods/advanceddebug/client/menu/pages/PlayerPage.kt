package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import com.ultreon.mods.advanceddebug.api.common.Percentage
import com.ultreon.mods.advanceddebug.api.common.degrees
import com.ultreon.mods.advanceddebug.mixin.common.LivingEntityAccessor
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.language.I18n
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import java.util.regex.Pattern

class PlayerPage(modId: String, name: String) : DebugPage(modId, name) {
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        if (Minecraft.getInstance().player == null) {
            ctx.top(ChatFormatting.RED.toString() + "<Local Player not found>")
            return
        }
        val player = Minecraft.getInstance().player
        val team = player!!.team
        val matcher = VALID_USERNAME.matcher(
            player.name.string
        )
        ctx.left("General Info")
        ctx.left("Luck", player.luck)
        ctx.left("Speed", player.speed)
        ctx.left("Score", player.score)
        ctx.left("Armor Value", player.armorValue)
        ctx.left("Jumping", (player as LivingEntityAccessor).isJumping)
        ctx.left("Sneaking", player.isShiftKeyDown)
        ctx.left("Swimming", player.isSwimming)
        ctx.left("Sleeping", player.isSleeping)
        ctx.left("Sprinting", player.isSprinting)
        ctx.left("Silent", player.isSilent)
        ctx.left()
        ctx.left("Position / Rotation")
        ctx.left("Position Block", player.blockPosition())
        ctx.left("Position", player.position())
        ctx.left("Rotation (xy)", player.rotationVector.x.toDouble().degrees, player.rotationVector.y.toDouble().degrees)
        ctx.left()
        ctx.left("Misc")
        ctx.left("Enchantment Seed", player.enchantmentSeed)
        ctx.left("Bee Sting Count", player.stingerCount)
        ctx.left("Idle Time", player.noActionTime)
        ctx.left("Motion", player.deltaMovement)
        ctx.left("Team Name", if (team != null) team.name else "")
        ctx.left("Height Offset", player.myRidingOffset)
        ctx.left("Eye Height", player.eyeHeight)
        ctx.left("Eye Height (real)", player.getEyeHeight(player.pose))
        ctx.left("Bounding Box", player.boundingBox)
        ctx.left("Bounding Box (real)", player.armorValue)
        ctx.left()

        // SWITCH TO: Right Sided Column
        ctx.right("XP Related")
        ctx.right("Experience Progress", Percentage(player.experienceProgress.toDouble()))
        ctx.right("Experience Level", player.experienceLevel)
        ctx.right("Experience Total", player.totalExperience)
        ctx.right()
        ctx.right("Timers")
        ctx.right("Sleep Timer", player.sleepTimer)
        ctx.right("Fire Timer", player.remainingFireTicks)
        ctx.right()
        ctx.right("Food / Health values")
        ctx.right("Health", player.health)
        ctx.right("Absorption", player.absorptionAmount)
        ctx.right("Armor Points", player.armorValue)
        ctx.right("Hunger", player.foodData.foodLevel)
        ctx.right("Saturation", player.foodData.saturationLevel)
        ctx.right("Air", player.airSupply)
        ctx.right()
        ctx.right("Misc Flags")
        ctx.right("Legal Username", matcher.find())
        ctx.right("Swing In Progress", player.swinging)
        ctx.right("User", player.isLocalPlayer)
        ctx.right("Alive", player.isAlive)
        ctx.right("Burning", player.isOnFire)
        ctx.right("Wet ", player.isInWaterOrRain)
        ctx.right("Creative", player.isCreative)
        ctx.right("Invulnerable", player.isInvulnerable)
        ctx.right("Spectator", player.isSpectator)
        ctx.right("Allow Build", player.mayBuild())
        ctx.right("Glowing", player.isCurrentlyGlowing)
        ctx.right("Invisible", player.isInvisible)
        ctx.right("On Ground", player.isOnGround)
        ctx.right("On Ladder", player.onClimbable())
        ctx.right()

        // SWITCH TO: Middle Column
        val level = Minecraft.getInstance().level
        if (level == null) {
            ctx.top(ChatFormatting.RED.toString() + "<World / Dimension Not Found>")
            return
        }
        run {
            val f = player.xRot
            val f1 = player.yRot
            val vec3d = player.getEyePosition(1f)
            val f2 = Mth.cos(-f1 * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
            val f3 = Mth.sin(-f1 * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
            val f4 = -Mth.cos(-f * (Math.PI.toFloat() / 180f))
            val f5 = Mth.sin(-f * (Math.PI.toFloat() / 180f))
            val f6 = f3 * f4
            val f7 = f2 * f4
            val d0 = 16.0
            val vec3d1 = vec3d.add(f6.toDouble() * d0, f5.toDouble() * d0, f7.toDouble() * d0)
            var lookingAt: BlockHitResult =
                level.clip(ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player))
            ctx.top(ChatFormatting.GRAY.toString() + "-== Block ==-")
            if (lookingAt.type == HitResult.Type.BLOCK) {
                val pos = lookingAt.blockPos

                // now the coordinates you want are in pos. Example of use:
                val blockState = Minecraft.getInstance().player!!.getLevel().getBlockState(pos)
                ctx.top(blockState.block.name.string)
            } else {
                // not looking at a block, or too far away from one to tell
                ctx.top(ChatFormatting.RED.toString() + "<No Target Block Was Found>")
            }
            ctx.top()
            lookingAt = level.clip(ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player))
            ctx.top(ChatFormatting.GRAY.toString() + "-== Fluid ==-")
            if (lookingAt.type == HitResult.Type.BLOCK) {
                val pos = lookingAt.blockPos

                // now the coordinates you want are in pos. Example of use:
                val blockState = Minecraft.getInstance().player!!.getLevel().getBlockState(pos)
                val fluidState = blockState.fluidState
                if (!fluidState.isEmpty) {
                    ctx.top(blockState.block.name.string)
                } else {
                    // not looking at a fluid, or too far away from one to tell
                    ctx.top(ChatFormatting.RED.toString() + "<No Target Fluid Was Found>")
                }
            } else {
                // not looking at a fluid, or too far away from one to tell
                ctx.top(ChatFormatting.RED.toString() + "<No Target Fluid Was Found>")
            }
            ctx.top()
        }
        run {
            val xRot = player.xRot
            val yRot = player.yRot
            val clipStart = player.getEyePosition(1f)
            val f2 = Mth.cos(-yRot * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
            val f3 = Mth.sin(-yRot * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
            val f4 = -Mth.cos(-xRot * (Math.PI.toFloat() / 180f))
            val deltaY = Mth.sin(-xRot * (Math.PI.toFloat() / 180f))
            val deltaX = f3 * f4
            val deltaZ = f2 * f4
            val distance = 16.0
            var clipEnd =
                clipStart.add(deltaX.toDouble() * distance, deltaY.toDouble() * distance, deltaZ.toDouble() * distance)
            var hit: HitResult =
                level.clip(ClipContext(clipStart, clipEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player))
            if (hit.type != HitResult.Type.MISS) {
                clipEnd = hit.location
            }
            val tempEntityHit: HitResult? = ProjectileUtil.getEntityHitResult(
                level,
                player,
                clipStart,
                clipEnd,
                player.boundingBox.inflate(16.0)
            ) { entity: Entity -> entity != player }
            if (tempEntityHit != null) {
                hit = tempEntityHit
            }
            ctx.top(ChatFormatting.GRAY.toString() + "-== Entity ==-")
            if (hit is EntityHitResult) {
                ctx.top(I18n.get(hit.entity.type.descriptionId))
            } else {
                // not looking at a block, or too far away from one to tell
                ctx.top(ChatFormatting.RED.toString() + "<No Target Entity Found>")
            }
            ctx.top()
        }
    }

    companion object {
        private val VALID_USERNAME = Pattern.compile("[a-zA-Z0-9_]*")
    }
}