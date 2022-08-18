package com.ultreon.mods.advanceddebug.api.common;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;

public interface IFormatter<T> {
    void format(T obj, IFormatterContext context);

    static <T extends IFormattable> IFormatter<T> create() {
        return IFormattable::format;
    }
}
