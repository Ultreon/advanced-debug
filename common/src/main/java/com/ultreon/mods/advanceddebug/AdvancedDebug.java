package com.ultreon.mods.advanceddebug;

import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugGui;
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.api.init.ModDebugFormatters;
import com.ultreon.mods.advanceddebug.client.Config;
import com.ultreon.mods.advanceddebug.client.input.KeyBindingList;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import com.ultreon.mods.advanceddebug.client.registry.FormatterRegistry;
import com.ultreon.mods.advanceddebug.extension.ExtensionLoader;
import com.ultreon.mods.advanceddebug.init.ModDebugPages;
import com.ultreon.mods.advanceddebug.init.ModOverlays;
import com.ultreon.mods.advanceddebug.util.TargetUtils;
import com.ultreon.mods.lib.util.KeyboardHelper;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    }

    private static EventResult keyPressed(Minecraft client, int keyCode, int scanCode, int action, int modifiers) {
        if (DebugGui.get().onKeyReleased(keyCode, scanCode, action, modifiers)) {
            return EventResult.interruptTrue();
        }
        return EventResult.pass();
    }

    public void init() {
        ClientLifecycleEvent.CLIENT_SETUP.register(this::setup);

        ClientRawInputEvent.KEY_PRESSED.register(AdvancedDebug::keyPressed);

        KeyBindingList.register();

        if (System.getenv("RT_DEBUG_FORMATTER_DUMP") != null) {
            FormatterRegistry.get().dump();
        }

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            if (KeyBindingList.SELECT_ENTITY.consumeClick() && KeyboardHelper.isCtrlDown()) {
                EntityHitResult hit = TargetUtils.entity();
                DebugGui.selectedEntity = hit != null ? hit.getEntity() : null;
                Entity entity = DebugGui.selectedEntity;
                if (entity == null) {
                    DebugGui.selectedServerEntity = null;
                } else {
                    IntegratedServer server = minecraft.getSingleplayerServer();
                    if (server != null) {
                        ServerLevel level = server.getLevel(entity.level.dimension());
                        if (level != null) {
                            DebugGui.selectedServerEntity = level.getEntity(entity.getId());
                        }
                    }
                }
            }
            if (KeyBindingList.SELECT_BLOCK.consumeClick() && KeyboardHelper.isCtrlDown()) {
                @Nullable BlockHitResult hit = TargetUtils.block();
                ClientLevel level = minecraft.level;
                if (level != null) {
                    DebugGui.SELECTED_BLOCKS.set(level, hit != null ? hit.getBlockPos() : null);
                    IntegratedServer server = minecraft.getSingleplayerServer();
                    if (server != null) {
                        ServerLevel serverLevel = server.getLevel(level.dimension());
                        if (serverLevel != null) {
                            DebugGui.SELECTED_BLOCKS.set(serverLevel, hit != null ? hit.getBlockPos() : null);
                        }
                    }
                }
            }
        });

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

    @Override
    public String getModId() {
        return MOD_ID;
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void setup(final Minecraft client) {
        if (client == null) {
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
