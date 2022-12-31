package com.ultreon.mods.advanceddebug.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TargetUtils {
    private TargetUtils() {
        throw new UnsupportedOperationException("Cannot instantiate a utility class");
    }

    @Nullable
    public static BlockHitResult block() {
        if (Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            float xRot = player.getXRot();
            float yRot = player.getYRot();

            Vec3 startPos = player.getEyePosition(1f);

            float calcZ = Mth.cos(-yRot * ((float) Math.PI / 180f) - (float) Math.PI);
            float calcX = Mth.sin(-yRot * ((float) Math.PI / 180f) - (float) Math.PI);
            float rot = -Mth.cos(-xRot * ((float) Math.PI / 180f));
            float deltaY = Mth.sin(-xRot * ((float) Math.PI / 180f));

            float deltaX = calcX * rot;
            float deltaZ = calcZ * rot;

            double distance = 16;

            Vec3 endPos = startPos.add((double) deltaX * distance, (double) deltaY * distance, (double) deltaZ * distance);

            if (Minecraft.getInstance().level != null) {
                return Minecraft.getInstance().level.clip(new ClipContext(startPos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            }
        }
        return null;
    }

    @Nullable
    public static BlockHitResult fluid() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            LocalPlayer player = mc.player;
            float xRot = player.getXRot();
            float yRot = player.getYRot();

            Vec3 startPos = player.getEyePosition(1f);

            float calcZ = Mth.cos(-yRot * ((float) Math.PI / 180f) - (float) Math.PI);
            float calcX = Mth.sin(-yRot * ((float) Math.PI / 180f) - (float) Math.PI);
            float rot = -Mth.cos(-xRot * ((float) Math.PI / 180f));
            float deltaY = Mth.sin(-xRot * ((float) Math.PI / 180f));

            float deltaX = calcX * rot;
            float deltaZ = calcZ * rot;

            double distance = 16;

            Vec3 endPos = startPos.add((double) deltaX * distance, (double) deltaY * distance, (double) deltaZ * distance);

            if (mc.level != null) {
                BlockHitResult clip = mc.level.clip(new ClipContext(startPos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player));
                FluidState fluidState = mc.level.getFluidState(clip.getBlockPos());
                if (fluidState.isEmpty()) {
                    return null;
                }
                return clip;
            }
        }
        return null;
    }

    @Nullable
    public static EntityHitResult entity() {
        if (Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            float xRot = player.getXRot();
            float yRot = player.getYRot();

            Vec3 startPos = player.getEyePosition(1f);

            float calcZ = Mth.cos(-yRot * ((float) Math.PI / 180f) - (float) Math.PI);
            float calcX = Mth.sin(-yRot * ((float) Math.PI / 180f) - (float) Math.PI);
            float rot = -Mth.cos(-xRot * ((float) Math.PI / 180f));
            float deltaY = Mth.sin(-xRot * ((float) Math.PI / 180f));

            float deltaX = calcX * rot;
            float deltaZ = calcZ * rot;

            double distance = 16;

            Vec3 endPos = startPos.add((double) deltaX * distance, (double) deltaY * distance, (double) deltaZ * distance);

            if (Minecraft.getInstance().level != null) {
                HitResult hit = Minecraft.getInstance().level.clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                if (hit.getType() != HitResult.Type.MISS) {
                    endPos = hit.getLocation();
                }

                HitResult tempHit = ProjectileUtil.getEntityHitResult(Minecraft.getInstance().level, player, startPos, endPos, player.getBoundingBox().inflate(16.0D), entity -> !entity.equals(player));
                if (tempHit != null) {
                    hit = tempHit;
                }
                if (hit instanceof EntityHitResult entityHit) {
                    return entityHit;
                }
            }
        }
        return null;
    }
}
