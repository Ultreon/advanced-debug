package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.util.TargetUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Collection;

import static net.minecraft.ChatFormatting.RED;

public class BlockPage extends DebugPage {
    public BlockPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        Minecraft instance = Minecraft.getInstance();
        BlockHitResult lookingAt = TargetUtils.block();
        LocalPlayer player = instance.player;
        
        if (player == null) return;
        
        if (lookingAt != null) {
            BlockPos pos = lookingAt.getBlockPos();

            // now the coordinates you want are in pos. Example of use:
            BlockState state = player.getLevel().getBlockState(pos);
            Block block = state.getBlock();

            ctx.left("Block Related");
            ctx.left("Type", block.getRegistryName());
            ctx.left("Translated Name", block.getName().getString());
            ctx.left("Block Hardness", state.getDestroySpeed(player.getLevel(), pos));
            ctx.left("Light Value", state.getLightEmission(instance.level, player.blockPosition()));
            ctx.left("Opacity", state.getLightBlock(player.getLevel(), pos));
            ctx.left("Offset", state.getOffset(player.getLevel(), pos));
            ctx.left("Mining Efficiency", state.getDestroyProgress(player, player.getLevel(), pos));
            ctx.left("Requires Tool", state.requiresCorrectToolForDrops());
            ctx.left("Render Type", state.getRenderShape());
            ctx.left("Slipperiness", state.getFriction(player.getLevel(), pos, player));
            ctx.left("Jump Factor", block.getJumpFactor());
            ctx.left("Enchant Power Bonus", state.getEnchantPowerBonus(player.getLevel(), pos));
            ctx.left("Target", block.getLootTable());
            ctx.left("Color", block.defaultMaterialColor().id, getColor(block.defaultMaterialColor().col));
            ctx.left("Offset Type", block.getOffsetType());
            ctx.left("Identifier", block.getRegistryName());
            ctx.left("Default Slipperiness", block.getFriction());
            ctx.left("Speed Factor", getMultiplier(block.getSpeedFactor()));
            ctx.left();

            Collection<Property<?>> properties = state.getProperties();
            if (!properties.isEmpty()) {
                ctx.right("Block Properties");
                properties.forEach((key) -> {
                    try {
                        ctx.right(key.getName(), state.getValue(key));
                    } catch (Exception e) {
                        ctx.right(key.getName(), RED + "Error");
                    }
                });
                ctx.right();
            }
        } else {
            // not looking at a block, or too far away from one to tell
            ctx.top(RED + "<No Block Was Found>");
        }

        lookingAt = TargetUtils.fluid();
        if (lookingAt != null) {
            BlockPos pos = lookingAt.getBlockPos();

            // now the coordinates you want are in pos. Example of use:
            FluidState state = player.getLevel().getBlockState(pos).getFluidState();
            ctx.right("Fluid Related");
            if (!state.isEmpty()) {
                ctx.right("Is Empty", state.isEmpty());
                ctx.right("Height", state.getOwnHeight());
                ctx.right("Amount", state.getAmount());
                ctx.right("Actual Height", state.getType().getHeight(state, player.getLevel(), pos));
                try {
                    ctx.right("Filled Bucket", state.getType().getBucket());
                } catch (Throwable ignored) {

                }
                ctx.right("Tick Rate", state.getType().getTickDelay(player.getLevel()));
            } else {
                // not looking at a fluid, or too far away from one to tell
                ctx.right(RED + "<No Fluid Was Found>");
            }
            ctx.right();
        }
    }
}
