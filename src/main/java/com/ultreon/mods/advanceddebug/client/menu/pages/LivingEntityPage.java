package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.client.menu.DebugRenderContext;
import com.ultreon.mods.advanceddebug.util.TargetUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

import static net.minecraft.ChatFormatting.RED;

public class LivingEntityPage extends EntityPage {
    public LivingEntityPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, DebugRenderContext ctx) {
        EntityHitResult entityHit = TargetUtils.entity();
        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity entity) {
            ctx.left("Health", entity.getHealth());
            ctx.left("Max Health", entity.getMaxHealth());
            ctx.left("Absorption", entity.getAbsorptionAmount());
            ctx.left("Armor Points", entity.getArmorValue());
            ctx.left("Arrows", entity.getArrowCount());
            ctx.left("In Combat", entity.getCombatTracker().isInCombat());
            ctx.left("Time Fall-flying", entity.getFallFlyingTicks());
            ctx.left("Main Hand Item", entity.getItemInHand(InteractionHand.MAIN_HAND));
            ctx.left("Off Hand Item", entity.getItemInHand(InteractionHand.OFF_HAND));
            ctx.left("Jump Boost Power", entity.getJumpBoostPower());
            ctx.left("Mob Type", entity.getMobType());
            ctx.left("Idle For", entity.getNoActionTime());
            ctx.left("Scale", entity.getScale());
            ctx.left("Speed", entity.getSpeed());
            ctx.left("Used Hand", entity.getUsedItemHand());
            ctx.left("Voice Pitch", entity.getVoicePitch());
            ctx.left("Head Rotation", entity.getYHeadRot());

            ctx.right("Alive", entity.isAlive());
            ctx.right("Baby Variant", entity.isBaby());
            ctx.right("Affected By Potions", entity.isAffectedByPotions());
            ctx.right("Is Blocking", entity.isBlocking());
            ctx.right("Glowing", entity.isCurrentlyGlowing());
            ctx.right("Dead / Dying", entity.isDeadOrDying());
            ctx.right("Effective AI", entity.isEffectiveAi());
            ctx.right("Fall Flying", entity.isFallFlying());
            ctx.right("In Wall", entity.isInWall());
            ctx.right("Pickable", entity.isPickable());
            ctx.right("Pushable", entity.isPushable());
            ctx.right("Sensitive To Water", entity.isSensitiveToWater());
            ctx.right("Sleeping", entity.isSleeping());
            ctx.right("Using Item", entity.isUsingItem());
            ctx.right("Visually Swimming", entity.isVisuallySwimming());
        } else {
            // not looking at a block, or too far away from one to tell
            ctx.top(RED + "<No Entity Was Found>");
        }
    }
}
