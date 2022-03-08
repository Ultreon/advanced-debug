package com.ultreon.mods.advanceddebug;

import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import com.ultreon.mods.advanceddebug.init.ModDebugFormatters;
import com.ultreon.mods.advanceddebug.init.ModOverlays;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AdvancedDebug.MOD_ID)
public class AdvancedDebug {
    public static final String MOD_ID = "advanced_debug";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public AdvancedDebug() {
        ModLoadingContext ctx = ModLoadingContext.get();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        // Register the setup method for mod-loading
        modBus.addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        forgeBus.register(this);

        ctx.registerExtensionPoint(
                DisplayTest.class, () -> new DisplayTest(() -> "anything. i don't care", (version, clientSide) -> clientSide)
        );
    }

    @SubscribeEvent
    public void onKeyReleased(InputEvent.KeyInputEvent event) {
        DebugGui.get().onKeyReleased(event);
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void setup(final FMLClientSetupEvent event) {
        LOGGER.debug("Doing client side setup rn.");
        LOGGER.debug("Registering modded overlays...");
        ModOverlays.registerAll();
        ModDebugFormatters.initClass();
        LOGGER.debug("Client side setup done!");
    }
}
