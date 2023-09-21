package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.util.TargetUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.ChatFormatting.RED;

public class FluidPage extends DebugPage {
    private static final Minecraft MC = Minecraft.getInstance();

    public FluidPage() {
    }

    @Override
    public void render(@NotNull GuiGraphics gfx, IDebugRenderContext ctx) {
        BlockHitResult lookingAt = TargetUtils.fluid();
        LocalPlayer player = MC.player;
        
        if (player == null) return;
        
        if (lookingAt != null) {
            BlockPos pos = lookingAt.getBlockPos();

            // now the coordinates you want are in pos. Example of use:
            FluidState state = player.level().getBlockState(pos).getFluidState();
            if (!state.isEmpty()) {
                ctx.left("Fluid Related");
                ctx.left("Height", state.getOwnHeight());
                ctx.left("Amount", state.getAmount());
                ctx.left("Actual Height", state.getType().getHeight(state, player.level(), pos));
                try {
                    ctx.left("Filled Bucket", state.getType().getBucket());
                } catch (Throwable ignored) {

                }
                ctx.left("Tick Rate", state.getType().getTickDelay(player.level()));
                ctx.left();

                ctx.right("Flags");
                ctx.right("Is Empty", state.isEmpty());
                ctx.right("Source", state.isSource());
                ctx.right();
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
