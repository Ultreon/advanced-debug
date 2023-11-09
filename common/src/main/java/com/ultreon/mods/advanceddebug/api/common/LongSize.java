package com.ultreon.mods.advanceddebug.api.common;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;

import java.util.Objects;

public final class LongSize extends AbstractSize implements IFormattable {
    public double width;
    public double height;

    public LongSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public Double getWidth() {
        return this.width;
    }

    public Double getHeight() {
        return this.height;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
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
        LongSize longSize = (LongSize) o;
        return Double.compare(longSize.width, this.width) == 0 && Double.compare(longSize.height, this.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.width, this.height);
    }
}
