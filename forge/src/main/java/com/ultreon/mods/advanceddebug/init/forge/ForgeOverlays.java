package com.ultreon.mods.advanceddebug.init.forge;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(value = Dist.CLIENT, modid = AdvancedDebug.MOD_ID, bus = Bus.MOD)
public class ForgeOverlays {
    private static final List<Consumer<RegisterGuiOverlaysEvent>> LISTENERS = new ArrayList<>();

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        LISTENERS.forEach(listener -> listener.accept(event));
    }

    public static void listen(Consumer<RegisterGuiOverlaysEvent> listener) {
        LISTENERS.add(listener);
    }
}
