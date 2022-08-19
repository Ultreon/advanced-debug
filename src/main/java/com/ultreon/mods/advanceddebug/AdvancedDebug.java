package com.ultreon.mods.advanceddebug;

import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugGui;
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.api.init.ModDebugFormatters;
import com.ultreon.mods.advanceddebug.client.Config;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import com.ultreon.mods.advanceddebug.client.registry.FormatterRegistry;
import com.ultreon.mods.advanceddebug.extension.ExtensionLoader;
import com.ultreon.mods.advanceddebug.init.ModDebugPages;
import com.ultreon.mods.advanceddebug.init.ModOverlays;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AdvancedDebug.MOD_ID)
public class AdvancedDebug implements IAdvancedDebug {
    public static final String MOD_ID = "advanced_debug";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LoggerFactory.getLogger("AdvancedDebug");
    private static AdvancedDebug instance;

    private static final ExtensionLoader loader = ExtensionLoader.get();

    public static AdvancedDebug getInstance() {
        return instance;
    }

    @SuppressWarnings("ConstantConditions")
    public AdvancedDebug() {
        instance = this;

        if (DatagenModLoader.isRunningDataGen()) {
            return;
        }

        Config.register(ModLoadingContext.get());

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

        if (System.getenv("RT_DEBUG_FORMATTER_DUMP") != null) {
            FormatterRegistry.get().dump();
        }

        loader.scan();
        loader.construct();
    }

    public IDebugGui getGui() {
        return DebugGui.get();
    }

    @Override
    public IFormatterRegistry getFormatterRegistry() {
        return FormatterRegistry.get();
    }

    @SubscribeEvent
    public void onKeyReleased(InputEvent.KeyInputEvent event) {
        DebugGui.get().onKeyReleased(event);
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void setup(final FMLClientSetupEvent event) {
        if (Minecraft.getInstance() == null) {
            return;
        }

        LOGGER.debug("Doing client side setup rn.");
        LOGGER.debug("Registering modded overlays...");
        ModOverlays.registerAll();
        ModDebugFormatters.initClass();

        LOGGER.debug("Setting up extensions...");
        loader.makeSetup();
        LOGGER.debug("Client side setup done!");

        ModDebugPages.init();
    }

    @Override
    public boolean isSpacedNamespace() {
        return Config.SPACED_NAMESPACES.get();
    }

    @Override
    public boolean isSpacedEnumConstants() {
        return Config.SPACED_ENUM_CONSTANTS.get();
    }

    @Override
    public boolean enableBubbleBlasterID() {
        return Config.ENABLE_BUBBLE_BLASTER_ID.get();
    }
}
