package com.ultreon.mods.advanceddebug.api.client.menu;

import net.minecraft.network.chat.Component;

public interface IDebugRenderContext {
    void left(Component text, Object object, Object... objects);

    void left(String text, Object object, Object... objects);

    void left(Component text);

    void left(String text);

    void left();

    void right(Component text, Object object, Object... objects);

    void right(String text, Object object, Object... objects);

    void right(Component text);

    void right(String text);

    void right();

    void top(Component text);

    void top(String text);

    void top();
}
