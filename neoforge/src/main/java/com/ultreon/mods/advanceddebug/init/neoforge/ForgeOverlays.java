package com.ultreon.mods.advanceddebug.init.neoforge;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = AdvancedDebug.MOD_ID, bus = Bus.MOD)
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
