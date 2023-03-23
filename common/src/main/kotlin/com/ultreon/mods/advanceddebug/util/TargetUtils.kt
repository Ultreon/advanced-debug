package com.ultreon.mods.advanceddebug.util

import net.minecraft.client.Minecraft
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult

class TargetUtils private constructor() {
    init {
        throw UnsupportedOperationException("Cannot instantiate a utility class")
    }

    companion object {
        fun block(): BlockHitResult? {
            if (Minecraft.getInstance().player != null) {
                val player = Minecraft.getInstance().player
                val xRot = player!!.xRot
                val yRot = player.yRot
                val startPos = player.getEyePosition(1f)
                val calcZ = Mth.cos(-yRot * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
                val calcX = Mth.sin(-yRot * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
                val rot = -Mth.cos(-xRot * (Math.PI.toFloat() / 180f))
                val deltaY = Mth.sin(-xRot * (Math.PI.toFloat() / 180f))
                val deltaX = calcX * rot
                val deltaZ = calcZ * rot
                val distance = 16.0
                val endPos = startPos.add(
                    deltaX.toDouble() * distance,
                    deltaY.toDouble() * distance,
                    deltaZ.toDouble() * distance
                )
                if (Minecraft.getInstance().level != null) {
                    return Minecraft.getInstance().level!!.clip(
                        ClipContext(
                            startPos,
                            endPos,
                            ClipContext.Block.OUTLINE,
                            ClipContext.Fluid.NONE,
                            player
                        )
                    )
                }
            }
            return null
        }

        fun fluid(): BlockHitResult? {
            val mc = Minecraft.getInstance()
            if (mc.player != null) {
                val player = mc.player
                val xRot = player!!.xRot
                val yRot = player.yRot
                val startPos = player.getEyePosition(1f)
                val calcZ = Mth.cos(-yRot * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
                val calcX = Mth.sin(-yRot * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
                val rot = -Mth.cos(-xRot * (Math.PI.toFloat() / 180f))
                val deltaY = Mth.sin(-xRot * (Math.PI.toFloat() / 180f))
                val deltaX = calcX * rot
                val deltaZ = calcZ * rot
                val distance = 16.0
                val endPos = startPos.add(
                    deltaX.toDouble() * distance,
                    deltaY.toDouble() * distance,
                    deltaZ.toDouble() * distance
                )
                if (mc.level != null) {
                    val clip = mc.level!!.clip(
                        ClipContext(
                            startPos,
                            endPos,
                            ClipContext.Block.OUTLINE,
                            ClipContext.Fluid.ANY,
                            player
                        )
                    )
                    val fluidState = mc.level!!.getFluidState(clip.blockPos)
                    return if (fluidState.isEmpty) {
                        null
                    } else clip
                }
            }
            return null
        }

        fun entity(): EntityHitResult? {
            if (Minecraft.getInstance().player != null) {
                val player = Minecraft.getInstance().player
                val xRot = player!!.xRot
                val yRot = player.yRot
                val startPos = player.getEyePosition(1f)
                val calcZ = Mth.cos(-yRot * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
                val calcX = Mth.sin(-yRot * (Math.PI.toFloat() / 180f) - Math.PI.toFloat())
                val rot = -Mth.cos(-xRot * (Math.PI.toFloat() / 180f))
                val deltaY = Mth.sin(-xRot * (Math.PI.toFloat() / 180f))
                val deltaX = calcX * rot
                val deltaZ = calcZ * rot
                val distance = 16.0
                var endPos = startPos.add(
                    deltaX.toDouble() * distance,
                    deltaY.toDouble() * distance,
                    deltaZ.toDouble() * distance
                )
                if (Minecraft.getInstance().level != null) {
                    var hit: HitResult = Minecraft.getInstance().level!!.clip(
                        ClipContext(
                            startPos,
                            endPos,
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            player
                        )
                    )
                    if (hit.type != HitResult.Type.MISS) {
                        endPos = hit.location
                    }
                    val tempHit: HitResult? = ProjectileUtil.getEntityHitResult(
                        Minecraft.getInstance().level!!,
                        player,
                        startPos,
                        endPos,
                        player.boundingBox.inflate(16.0)
                    ) { entity: Entity -> entity != player }
                    if (tempHit != null) {
                        hit = tempHit
                    }
                    if (hit is EntityHitResult) {
                        return hit
                    }
                }
            }
            return null
        }
    }
}