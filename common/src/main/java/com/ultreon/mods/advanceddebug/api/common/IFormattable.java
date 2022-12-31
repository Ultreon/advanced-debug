package com.ultreon.mods.advanceddebug.api.common;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;

public interface IFormattable {
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated(forRemoval = true)
    default String toFormattedString() {
        throw new AssertionError("toFormattedString(IFormatterContext) is not implemented");
    }

    @SuppressWarnings("deprecation")
    default void format(IFormatterContext ctx) {
        ctx.direct(toFormattedString());
    }
}
