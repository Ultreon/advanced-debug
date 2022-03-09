package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.api.common.Formatted;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Vec3i;

import static net.minecraft.ChatFormatting.GOLD;

public class WorldInfoPage extends DebugPage {
    public WorldInfoPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        if (Minecraft.getInstance().level != null) {
            ClientLevel.ClientLevelData dimensionInfo = Minecraft.getInstance().level.getLevelData();

            ctx.left("Spawn Angle", dimensionInfo.getSpawnAngle());
            ctx.left("Difficulty", dimensionInfo.getDifficulty());
            ctx.left("Spawn", new Vec3i(dimensionInfo.getXSpawn(), dimensionInfo.getYSpawn(), dimensionInfo.getZSpawn()));

            ctx.right("Difficulty Locked", dimensionInfo.isDifficultyLocked());
            ctx.right("Hardcore", dimensionInfo.isHardcore());
            ctx.right("Raining", dimensionInfo.isRaining());
            ctx.right("Thundering", dimensionInfo.isThundering());
            ctx.right("Weather", new Formatted(GOLD + (dimensionInfo.isRaining() ? (dimensionInfo.isThundering() ? "storm" : "rain") : "clear")));
        }
    }
}
