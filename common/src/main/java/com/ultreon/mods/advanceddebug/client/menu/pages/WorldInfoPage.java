package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.api.common.Formatted;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.ChatFormatting.GOLD;

public class WorldInfoPage extends DebugPage {
    public WorldInfoPage() {
    }

    @Override
    public void render(@NotNull GuiGraphics gfx, IDebugRenderContext ctx) {
        if (Minecraft.getInstance().level != null) {
            ClientLevel.ClientLevelData dimensionInfo = Minecraft.getInstance().level.getLevelData();

            IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
            ServerData currentServer = Minecraft.getInstance().getCurrentServer();
            if (server != null) {
                ctx.left("Singleplayer");
                ctx.left("World Name", server.getWorldData().getLevelName());
                ctx.left();
            }

            if (currentServer != null) {
                ctx.left("Multiplayer");
                ctx.left("Server Name", currentServer.name);
                ctx.left();
            }

            ctx.left("Misc");
            ctx.left("Spawn Angle", dimensionInfo.getSpawnAngle());
            ctx.left("Difficulty", dimensionInfo.getDifficulty());
            ctx.left("Spawn", new Vec3i(dimensionInfo.getXSpawn(), dimensionInfo.getYSpawn(), dimensionInfo.getZSpawn()));
            ctx.left("Weather", new Formatted(GOLD + (dimensionInfo.isRaining() ? (dimensionInfo.isThundering() ? "storm" : "rain") : "clear")));
            ctx.left();

            ctx.right("Flags");
            ctx.right("Difficulty Locked", dimensionInfo.isDifficultyLocked());
            ctx.right("Hardcore", dimensionInfo.isHardcore());
            ctx.right("Raining", dimensionInfo.isRaining());
            ctx.right("Thundering", dimensionInfo.isThundering());
            ctx.right();
        }
    }
}
