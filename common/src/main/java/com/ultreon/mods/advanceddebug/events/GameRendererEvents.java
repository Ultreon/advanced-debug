package com.ultreon.mods.advanceddebug.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

public class GameRendererEvents {
    public static final Event<PreGameRender> PRE_GAME_RENDER = EventFactory.createLoop();
    public static final Event<PostGameRender> POST_GAME_RENDER = EventFactory.createLoop();

    @FunctionalInterface
    public interface PreGameRender {
        void onPreGameRender(Minecraft client, GameRenderer gameRenderer, float partialTicks, long nanoTime, boolean renderLevel);
    }

    @FunctionalInterface
    public interface PostGameRender {
        void onPostGameRender(Minecraft client, GameRenderer gameRenderer, float partialTicks, long nanoTime, boolean renderLevel);
    }
}
