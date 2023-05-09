package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.api.common.MoonPhase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

public class WorldPage extends DebugPage {
    public WorldPage() {
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        if (Minecraft.getInstance().level != null) {
            ClientLevel dimension = Minecraft.getInstance().level;

            ctx.left("Properties");
            ctx.left("Difficulty", dimension.getDifficulty().getDisplayName().getString());
            ctx.left("Sea Level", dimension.getSeaLevel());
            ctx.left("Moon Phase", MoonPhase.fromIndex(dimension.getMoonPhase()));
            ctx.left("Spawn Angle", getAngle(dimension.getLevelData().getSpawnAngle()));
            ctx.left("Dimension", dimension.dimension().location());
            ctx.left();

            ctx.left("Colors");
            ctx.left("Cloud Color", getColor(dimension.getCloudColor(mc.getFrameTime())));
            if (Minecraft.getInstance().player != null) {
                LocalPlayer player = Minecraft.getInstance().player;
                ctx.left("Sky Color", getColor(dimension.getSkyColor(player.position(), mc.getFrameTime())));
            }
            ctx.left();

            float skyDarken = dimension.getSkyDarken(mc.getFrameTime());

            ctx.left("Brightness");
            ctx.left("Star Brightness", getPercentage(dimension.getStarBrightness(mc.getFrameTime())));
            ctx.left("Sun Brightness", getPercentage(skyDarken));
            ctx.left();

            ctx.right("Time & Weather");
            ctx.right("Day Time", dimension.getDayTime() % 24000);
            ctx.right("Game Time", dimension.getDayTime());
            ctx.right("Days Played", dimension.getDayTime() / 24000);
            ctx.right("Is Day", skyDarken >= 0.7f);
            ctx.right("Is Night", skyDarken < 0.7f);
            ctx.right("Raining", dimension.isRaining());
            ctx.right("Thundering", dimension.isThundering());
            ctx.right();

            ctx.right("Flags");
            ctx.right("No Saving", dimension.noSave());
            ctx.right("Debug World", dimension.isDebug());
            ctx.right();

            ctx.right("Misc");
            ctx.right("Lightning Flash Time", dimension.getSkyFlashTime());
            ctx.right("Chunk Provider", dimension.gatherChunkSourceStats());
            ctx.right("Loaded Entities", dimension.getEntityCount());
            ctx.right();
        }
    }
}
