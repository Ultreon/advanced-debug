package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.client.menu.DebugRenderContext;
import com.ultreon.mods.advanceddebug.util.TargetUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.RED;

public class BlockPage extends DebugPage {
    public BlockPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, DebugRenderContext ctx) {
        Minecraft instance = Minecraft.getInstance();
        BlockHitResult lookingAt = TargetUtils.block();
        LocalPlayer player = instance.player;
        
        if (player == null) return;
        
        if (lookingAt != null) {
            BlockPos pos = lookingAt.getBlockPos();

            // now the coordinates you want are in pos. Example of use:
            BlockState state = player.getLevel().getBlockState(pos);
            Block block = state.getBlock();
            ctx.left(GRAY + "-== BLOCK ==-");
            ctx.left("type", block.getRegistryName());
            ctx.left("translatedName", block.getName().getString());
            ctx.left("blockHardness", state.getDestroySpeed(player.getLevel(), pos));
            ctx.left("lightValue", state.getLightEmission(instance.level, player.blockPosition()));
            ctx.left("opacity", state.getLightBlock(player.getLevel(), pos));
            ctx.left("offset", state.getOffset(player.getLevel(), pos));
            ctx.left("playerRelativeHardness", state.getDestroyProgress(player, player.getLevel(), pos));
            ctx.left("requiresTool", state.requiresCorrectToolForDrops());
            ctx.left("renderType", state.getRenderShape());
            ctx.left("slipperiness", state.getFriction(player.getLevel(), pos, player));
            ctx.left("jumpFactor", block.getJumpFactor());
            ctx.left("enchantPowerBonus", state.getEnchantPowerBonus(player.getLevel(), pos));
            ctx.left("target", block.getLootTable());
            ctx.left("materialColor", block.defaultMaterialColor().id, getColor(block.defaultMaterialColor().col));
            ctx.left("offsetType", block.getOffsetType());
            ctx.left("registryName", block.getRegistryName());
            ctx.left("defaultSlipperiness", block.getFriction());
            ctx.left("speedFactor", getMultiplier(block.getSpeedFactor()));
        } else {
            // not looking at a block, or too far away from one to tell
            ctx.top(RED + "<No Block Was Found>");
        }

        lookingAt = TargetUtils.fluid();
        if (lookingAt != null) {
            BlockPos pos = lookingAt.getBlockPos();

            // now the coordinates you want are in pos. Example of use:
            FluidState state = player.getLevel().getBlockState(pos).getFluidState();
            if (!state.isEmpty()) {
                ctx.right(GRAY + "-== Fluid ==-");
                ctx.right("empty", state.isEmpty());
                ctx.right("height", state.getOwnHeight());
                ctx.right("level", state.getAmount());
                ctx.right("actualHeight", state.getType().getHeight(state, player.getLevel(), pos));
                try {
                    ctx.right("filledBucket", state.getType().getBucket());
                } catch (Throwable ignored) {

                }
                ctx.right("tickRate", state.getType().getTickDelay(player.getLevel()));
            } else {
                // not looking at a fluid, or too far away from one to tell
                ctx.top(RED + "<No Fluid Was Found>");
            }
        }
    }
}
