package com.ultreon.mods.advanceddebug.api.extension;

import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.api.events.IInitPagesEvent;
import com.ultreon.mods.advanceddebug.util.ImGuiHandler;

public interface Extension extends ImGuiHandler {
    default void initPages(IInitPagesEvent initEvent) {

    }

    default void initFormatters(IFormatterRegistry formatterRegistry) {

    }

    void handleImGuiMenuBar();
}
