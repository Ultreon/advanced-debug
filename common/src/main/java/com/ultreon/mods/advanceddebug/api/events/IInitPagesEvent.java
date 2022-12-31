package com.ultreon.mods.advanceddebug.api.events;

import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;

@FunctionalInterface
public interface IInitPagesEvent {
    <T extends DebugPage> T register(T page);
}
