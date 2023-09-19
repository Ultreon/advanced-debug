package com.ultreon.mods.advanceddebug.api.events;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;

@FunctionalInterface
public interface IInitPagesEvent {
    <T extends DebugPage> T register(Identifier id, T page);
}
