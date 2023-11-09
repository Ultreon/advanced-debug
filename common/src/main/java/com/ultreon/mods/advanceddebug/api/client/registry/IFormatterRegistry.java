package com.ultreon.mods.advanceddebug.api.client.registry;

import com.ultreon.mods.advanceddebug.api.client.menu.Formatter;
import org.jetbrains.annotations.Nullable;

public interface IFormatterRegistry {
    <T> Formatter<T> register(Formatter<T> formatter);

    @Nullable
    Formatter<?> identify(Class<?> aClass);
}
