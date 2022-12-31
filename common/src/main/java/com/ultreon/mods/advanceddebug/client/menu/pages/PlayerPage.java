package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.api.common.Percentage;
import com.ultreon.mods.advanceddebug.mixin.common.LivingEntityAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.RED;

public class PlayerPage extends DebugPage {
    private static final Pattern VALID_USERNAME = Pattern.compile("[a-zA-Z0-9_]*");

    public PlayerPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        if (Minecraft.getInstance().player == null) {
            ctx.top(RED + "<Local Player not found>");
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        Team team = player.getTeam();
        Matcher matcher = VALID_USERNAME.matcher(player.getName().getString());

        ctx.left("General Info");
        ctx.left("Luck", player.getLuck());
        ctx.left("Speed", player.getSpeed());
        ctx.left("Score", player.getScore());
        ctx.left("Armor Value", player.getArmorValue());
        ctx.left("Jumping", ((LivingEntityAccessor)player).isJumping());
        ctx.left("Sneaking", player.isShiftKeyDown());
        ctx.left("Swimming", player.isSwimming());
        ctx.left("Sleeping", player.isSleeping());
        ctx.left("Sprinting", player.isSprinting());
        ctx.left("Silent", player.isSilent());
        ctx.left();

        ctx.left("Position / Rotation");
        ctx.left("Position Block", player.blockPosition());
        ctx.left("Position", player.position());
        ctx.left("Rotation (xy)", getDegrees(player.getRotationVector().x), getDegrees(player.getRotationVector().y));
        ctx.left();

        ctx.left("Misc");
        ctx.left("Enchantment Seed", player.getEnchantmentSeed());
        ctx.left("Bee Sting Count", player.getStingerCount());
        ctx.left("Idle Time", player.getNoActionTime());
        ctx.left("Motion", player.getDeltaMovement());
        ctx.left("Team Name", (team != null ? team.getName() : ""));
        ctx.left("Height Offset", player.getMyRidingOffset());
        ctx.left("Eye Height", player.getEyeHeight());
        ctx.left("Eye Height (real)", player.getEyeHeight(player.getPose()));
        ctx.left("Bounding Box", player.getBoundingBox());
        ctx.left("Bounding Box (real)", player.getArmorValue());
        ctx.left();

        // SWITCH TO: Right Sided Column
        ctx.right("XP Related");
        ctx.right("Experience Progress", new Percentage(player.experienceProgress));
        ctx.right("Experience Level", player.experienceLevel);
        ctx.right("Experience Total", player.totalExperience);
        ctx.right();

        ctx.right("Timers");
        ctx.right("Sleep Timer", player.getSleepTimer());
        ctx.right("Fire Timer", player.getRemainingFireTicks());
        ctx.right();

        ctx.right("Food / Health values");
        ctx.right("Health", player.getHealth());
        ctx.right("Absorption", player.getAbsorptionAmount());
        ctx.right("Armor Points", player.getArmorValue());
        ctx.right("Hunger", player.getFoodData().getFoodLevel());
        ctx.right("Saturation", player.getFoodData().getSaturationLevel());
        ctx.right("Air", player.getAirSupply());
        ctx.right();

        ctx.right("Misc Flags");
        ctx.right("Legal Username", matcher.find());
        ctx.right("Swing In Progress", player.swinging);
        ctx.right("User", player.isLocalPlayer());
        ctx.right("Alive", player.isAlive());
        ctx.right("Burning", player.isOnFire());
        ctx.right("Wet ", player.isInWaterOrRain());
        ctx.right("Creative", player.isCreative());
        ctx.right("Invulnerable", player.isInvulnerable());
        ctx.right("Spectator", player.isSpectator());
        ctx.right("Allow Build", player.mayBuild());
        ctx.right("Glowing", player.isCurrentlyGlowing());
        ctx.right("Invisible", player.isInvisible());
        ctx.right("On Ground", player.isOnGround());
        ctx.right("On Ladder", player.onClimbable());
        ctx.right();

        // SWITCH TO: Middle Column
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            ctx.top(RED + "<World / Dimension Not Found>");
            return;
        }

        {
            float f = player.getXRot();
            float f1 = player.getYRot();

            Vec3 vec3d = player.getEyePosition(1f);

            float f2 = Mth.cos(-f1 * ((float) Math.PI / 180f) - (float) Math.PI);
            float f3 = Mth.sin(-f1 * ((float) Math.PI / 180f) - (float) Math.PI);
            float f4 = -Mth.cos(-f * ((float) Math.PI / 180f));
            float f5 = Mth.sin(-f * ((float) Math.PI / 180f));

            float f6 = f3 * f4;
            float f7 = f2 * f4;

            double d0 = 16;

            Vec3 vec3d1 = vec3d.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);

            BlockHitResult lookingAt;
            lookingAt = level.clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            ctx.top(GRAY + "-== Block ==-");
            if (lookingAt.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = lookingAt.getBlockPos();

                // now the coordinates you want are in pos. Example of use:
                BlockState blockState = Minecraft.getInstance().player.getLevel().getBlockState(pos);
                ctx.top(blockState.getBlock().getName().getString());
            } else {
                // not looking at a block, or too far away from one to tell
                ctx.top(RED + "<No Target Block Was Found>");
            }
            ctx.top();
            lookingAt = level.clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player));
            ctx.top(GRAY + "-== Fluid ==-");
            if (lookingAt.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = lookingAt.getBlockPos();

                // now the coordinates you want are in pos. Example of use:
                BlockState blockState = Minecraft.getInstance().player.getLevel().getBlockState(pos);
                FluidState fluidState = blockState.getFluidState();
                if (!fluidState.isEmpty()) {
                    ctx.top(blockState.getBlock().getName().getString());
                } else {
                    // not looking at a fluid, or too far away from one to tell
                    ctx.top(RED + "<No Target Fluid Was Found>");
                }
            } else {
                // not looking at a fluid, or too far away from one to tell
                ctx.top(RED + "<No Target Fluid Was Found>");
            }
            ctx.top();
        }

        {
            float xRot = player.getXRot();
            float yRot = player.getYRot();

            Vec3 clipStart = player.getEyePosition(1f);

            float f2 = Mth.cos(-yRot * ((float) Math.PI / 180f) - (float) Math.PI);
            float f3 = Mth.sin(-yRot * ((float) Math.PI / 180f) - (float) Math.PI);
            float f4 = -Mth.cos(-xRot * ((float) Math.PI / 180f));
            float deltaY = Mth.sin(-xRot * ((float) Math.PI / 180f));

            float deltaX = f3 * f4;
            float deltaZ = f2 * f4;

            double distance = 16;

            Vec3 clipEnd = clipStart.add((double) deltaX * distance, (double) deltaY * distance, (double) deltaZ * distance);

            HitResult hit = level.clip(new ClipContext(clipStart, clipEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (hit.getType() != HitResult.Type.MISS) {
                clipEnd = hit.getLocation();
            }

            HitResult tempEntityHit = ProjectileUtil.getEntityHitResult(level, player, clipStart, clipEnd, player.getBoundingBox().inflate(16.0D), entity -> !entity.equals(player));
            if (tempEntityHit != null) {
                hit = tempEntityHit;
            }
            ctx.top(GRAY + "-== Entity ==-");
            if (hit instanceof EntityHitResult entityHit) {
                ctx.top(I18n.get(entityHit.getEntity().getType().getDescriptionId()));
            } else {
                // not looking at a block, or too far away from one to tell
                ctx.top(RED + "<No Target Entity Found>");
            }
            ctx.top();
        }
    }
}
