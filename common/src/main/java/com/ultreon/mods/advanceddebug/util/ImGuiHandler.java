package com.ultreon.mods.advanceddebug.util;

@SuppressWarnings("unused")
public interface ImGuiHandler {
    default boolean doHandleImGui(boolean devEnv) {
        return true;
    }

    default void handleImGui() {

    }
}
