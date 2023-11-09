package com.ultreon.mods.advanceddebug.api.common;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.util.MathUtil;

import java.util.Objects;

public final class FloatSize implements IFormattable {
    public float width;
    public float height;

    public FloatSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public void format(IFormatterContext ctx) {
        ctx.number(MathUtil.roundTo(this.width, 5)).operator(" × ").number(MathUtil.roundTo(this.height, 5));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        FloatSize size = (FloatSize) o;
        return Float.compare(size.width, this.width) == 0 && Float.compare(size.height, this.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.width, this.height);
    }
}
