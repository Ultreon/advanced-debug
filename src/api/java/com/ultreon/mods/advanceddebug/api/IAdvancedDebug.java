package com.ultreon.mods.advanceddebug.api;

import com.ultreon.mods.advanceddebug.api.client.menu.IDebugGui;
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;

import java.lang.reflect.InvocationTargetException;

public interface IAdvancedDebug {
    static IAdvancedDebug get() {
        try {
            return (IAdvancedDebug) Class.forName("com.ultreon.mods.advanceddebug.AdvancedDebug").getDeclaredMethod("getInstance").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new IllegalStateException("Can't get instance object of the Advanced Debug Mod.");
        }
    }

    IDebugGui getGui();

    IFormatterRegistry getFormatterRegistry();

    String getModId();
}
