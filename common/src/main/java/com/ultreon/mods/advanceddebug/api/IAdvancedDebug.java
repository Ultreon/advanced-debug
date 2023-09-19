package com.ultreon.mods.advanceddebug.api;

import com.ultreon.mods.advanceddebug.api.client.menu.IDebugGui;
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.client.Config;

import java.lang.reflect.InvocationTargetException;

public interface IAdvancedDebug {
    static IAdvancedDebug get() {
        try {
            return (IAdvancedDebug) Class.forName("com.ultreon.mods.advanceddebug.AdvancedDebug").getDeclaredMethod("getInstance").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new IllegalStateException("Can't get instance object of the Advanced Debug Mod.");
        }
    }

    IDebugGui getGui();

    IFormatterRegistry getFormatterRegistry();

    String getModId();

    @Deprecated(forRemoval = true)
    default boolean isSpacedNamespace() {
        return Config.SPACED_NAMESPACES.get();
    }

    @Deprecated(forRemoval = true)
    default boolean isSpacedEnumConstants() {
        return Config.SPACED_ENUM_CONSTANTS.get();
    }

    @Deprecated(forRemoval = true)
    default boolean enableBubbleBlasterID() {
        return Config.ENABLE_BUBBLE_BLASTER_ID.get();
    }

    default boolean isNamespaceSpaced() {
        return Config.SPACED_NAMESPACES.get();
    }

    default boolean isEnumConstantsSpaced() {
        return Config.SPACED_ENUM_CONSTANTS.get();
    }

    default boolean isBubbleBlasterIdEnabled() {
        return Config.ENABLE_BUBBLE_BLASTER_ID.get();
    }
}
