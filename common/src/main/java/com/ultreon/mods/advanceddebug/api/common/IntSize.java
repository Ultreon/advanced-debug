package com.ultreon.mods.advanceddebug.api.common;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;

import java.util.Objects;

public final class IntSize implements IFormattable {
    public int width;
    public int height;

    public IntSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void format(IFormatterContext ctx) {
        ctx.number(this.width).operator(" × ").number(this.height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        IntSize size = (IntSize) o;
        return this.width == size.width && this.height == size.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.width, this.height);
    }
}
