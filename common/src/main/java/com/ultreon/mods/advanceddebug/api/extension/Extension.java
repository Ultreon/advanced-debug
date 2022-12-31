package com.ultreon.mods.advanceddebug.api.extension;

import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.api.events.IInitPagesEvent;

public interface Extension {
    void initPages(IInitPagesEvent initEvent);

    void initFormatters(IFormatterRegistry formatterRegistry);
}
