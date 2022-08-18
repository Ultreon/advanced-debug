package com.ultreon.mods.advanceddebug.api.common;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;

@SuppressWarnings("unused")
public final class Formatted implements IFormattable {
    private final String string;

    public String getString() {
        return string;
    }

    public Formatted(String string) {
        this.string = string;
    }

    public Formatted(Object object) {
        this.string = object.toString();
    }

    public Formatted(char c) {
        this.string = Character.toString(c);
    }

    public Formatted(byte b) {
        this.string = Byte.toString(b);
    }

    public Formatted(short s) {
        this.string = Short.toString(s);
    }

    public Formatted(int i) {
        this.string = Integer.toString(i);
    }

    public Formatted(long l) {
        this.string = Long.toString(l);
    }

    public Formatted(boolean b) {
        this.string = Boolean.toString(b);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void format(IFormatterContext ctx) {
        ctx.direct(string);
    }
}
