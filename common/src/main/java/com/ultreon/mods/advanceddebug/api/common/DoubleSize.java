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
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
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
        DoubleSize that = (DoubleSize) o;
        return Double.compare(that.width, this.width) == 0 && Double.compare(that.height, this.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.width, this.height);
    }
}
