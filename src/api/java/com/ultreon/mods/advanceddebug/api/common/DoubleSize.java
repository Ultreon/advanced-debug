package com.ultreon.mods.advanceddebug.api.common;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.util.MathUtil;

import java.util.Objects;

public final class DoubleSize implements IFormattable {
    public double width;
    public double height;

    public DoubleSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public void format(IFormatterContext ctx) {
        ctx.number(MathUtil.roundTo(width, 5)).operator(" \u00D7 ").number(MathUtil.roundTo(height, 5));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleSize that = (DoubleSize) o;
        return Double.compare(that.width, width) == 0 && Double.compare(that.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}
