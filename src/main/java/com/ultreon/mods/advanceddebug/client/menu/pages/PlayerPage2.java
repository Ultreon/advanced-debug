package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.client.menu.DebugEntry;
import com.ultreon.mods.advanceddebug.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.client.menu.DebugRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.ChatFormatting.RED;

public class PlayerPage2 extends DebugPage {
    public PlayerPage2(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, DebugRenderContext ctx) {
        if (Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            Team team = player.getTeam();

            int i = 0;
            ctx.left("Idle Time", player.getNoActionTime());
            ctx.left("Motion", player.getDeltaMovement());
            ctx.left("Team Name", (team != null ? team.getName() : ""));
            ctx.left("Exp Seed", player.getEnchantmentSeed());
            ctx.left("Height Offset", player.getMyRidingOffset());

            int j = 0;
            ctx.right("Glowing", player.isCurrentlyGlowing());
            ctx.right("Invisible", player.isInvisible());
            ctx.right("On Ground", player.isOnGround());
            ctx.right("On Ladder", player.onClimbable());
        } else {
            ctx.top(RED + "<Local Player not found>");
        }
    }
}
