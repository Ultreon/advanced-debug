package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.util.TargetUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

import static net.minecraft.ChatFormatting.RED;

public class LivingEntityPage extends EntityPage {
    public LivingEntityPage() {
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        EntityHitResult entityHit = TargetUtils.entity();
        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity entity) {
            ctx.left("Properties");
            ctx.left("Health", entity.getHealth());
            ctx.left("Max Health", entity.getMaxHealth());
            ctx.left("Absorption", entity.getAbsorptionAmount());
            ctx.left("Armor Points", entity.getArmorValue());
            ctx.left("Arrows", entity.getArrowCount());
            ctx.left("Time Fall-flying", entity.getFallFlyingTicks());
            ctx.left("Jump Boost Power", entity.getJumpBoostPower());
            ctx.left("Idle For", entity.getNoActionTime());
            ctx.left("Scale", entity.getScale());
            ctx.left("Speed", entity.getSpeed());
            ctx.left("Voice Pitch", entity.getVoicePitch());
            ctx.left();

            ctx.left("Combat");
            ctx.left("In Combat", entity.getCombatTracker().isInCombat());
            ctx.left("Killer ID", entity.getCombatTracker().getKillerId());
            ctx.left("Combat Duration", entity.getCombatTracker().getCombatDuration());
            ctx.left("Death Message", entity.getCombatTracker().getDeathMessage());
            ctx.left();

            ctx.right("Equipment");
            ctx.right("Main Hand Item", entity.getItemInHand(InteractionHand.MAIN_HAND));
            ctx.right("Off Hand Item", entity.getItemInHand(InteractionHand.OFF_HAND));
            ctx.right("Used Hand", entity.getUsedItemHand());
            ctx.right("Head Rotation", entity.getYHeadRot());
            ctx.right();

            ctx.right("Flags");
            ctx.right("Alive", entity.isAlive());
            ctx.right("Baby Variant", entity.isBaby());
            ctx.right("Affected By Potions", entity.isAffectedByPotions());
            ctx.right("Is Blocking", entity.isBlocking());
            ctx.right("Glowing", entity.isCurrentlyGlowing());
            ctx.right("Dead / Dying", entity.isDeadOrDying());
            ctx.right("Effective AI", entity.isEffectiveAi());
            ctx.right("Fall Flying", entity.isFallFlying());
            ctx.right("In Wall", entity.isInWall());
            ctx.right("Sensitive To Water", entity.isSensitiveToWater());
            ctx.right("Sleeping", entity.isSleeping());
            ctx.right("Using Item", entity.isUsingItem());
            ctx.right("Visually Swimming", entity.isVisuallySwimming());
            ctx.right();
        } else {
            // Yup, there isn't a living being there where you look at.
            ctx.top(RED + "<No Living Entity Was Found>");
        }
    }
}
