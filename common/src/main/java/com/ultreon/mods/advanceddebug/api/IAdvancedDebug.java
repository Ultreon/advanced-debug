package com.ultreon.mods.advanceddebug.api;

import com.ultreon.mods.advanceddebug.api.client.menu.IDebugGui;
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.client.Config;
import io.github.xypercode.craftyconfig.CraftyConfig;

import java.lang.reflect.InvocationTargetException;

public interface IAdvancedDebug {
    String MOD_ID = "advanced_debug";

    static IAdvancedDebug get() {
        try {
            return (IAdvancedDebug) Class.forName("com.ultreon.mods.advanceddebug.AdvancedDebug").getDeclaredMethod("getInstance").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new IllegalStateException("Can't get instance object of the Advanced Debug Mod.");
        }
    }

    CraftyConfig getConfig();

    IDebugGui getGui();

    IFormatterRegistry getFormatterRegistry();

    String getModId();

    @Deprecated(forRemoval = true)
    default boolean isSpacedNamespace() {
        return Config.spacedNamespaces;
    }

    @Deprecated(forRemoval = true)
    default boolean isSpacedEnumConstants() {
        return Config.spacedEnumConstants;
    }

    @Deprecated(forRemoval = true)
    default boolean enableBubbleBlasterID() {
        return Config.enableBubbleBlasterId;
    }

    default boolean isNamespaceSpaced() {
        return Config.spacedNamespaces;
    }

    default boolean isEnumConstantsSpaced() {
        return Config.spacedEnumConstants;
    }

    default boolean isBubbleBlasterIdEnabled() {
        return Config.enableBubbleBlasterId;
    }
}
