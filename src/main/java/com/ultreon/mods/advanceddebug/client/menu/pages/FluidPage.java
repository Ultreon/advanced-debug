package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.util.TargetUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

import static net.minecraft.ChatFormatting.RED;

public class FluidPage extends DebugPage {
    private static final Minecraft MC = Minecraft.getInstance();

    public FluidPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        BlockHitResult lookingAt = TargetUtils.fluid();
        LocalPlayer player = MC.player;
        
        if (player == null) return;
        
        if (lookingAt != null) {
            BlockPos pos = lookingAt.getBlockPos();

            // now the coordinates you want are in pos. Example of use:
            FluidState state = player.getLevel().getBlockState(pos).getFluidState();
            if (!state.isEmpty()) {
                ctx.left("height", state.getOwnHeight());
                ctx.left("amount", state.getAmount());
                ctx.left("actualHeight", state.getType().getHeight(state, player.getLevel(), pos));
                try {
                    ctx.left("filledBucket", state.getType().getBucket());
                } catch (Throwable ignored) {

                }
                ctx.left("tickRate", state.getType().getTickDelay(player.getLevel()));

                ctx.right("empty", state.isEmpty());
                ctx.right("source", state.isSource());
            } else {
                // not looking at a fluid, or too far away from one to tell
                ctx.top(RED + "<No Fluid Was Found>");
            }
        } else {
            // not looking at a fluid, or too far away from one to tell
            ctx.top(RED + "<No Fluid Was Found>");
        }
    }
}
