package com.ultreon.mods.advanceddebug.api.common;

import net.minecraft.ChatFormatting;

import java.util.Objects;

public final class LongSize extends AbstractSize implements IFormattable {
    public double width;
    public double height;

    public LongSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public Double getWidth() {
        return width;
    }

    public Double getHeight() {
        return height;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public String toFormattedString() {
        return ChatFormatting.GOLD.toString() + this.width + ChatFormatting.GRAY + " x " + ChatFormatting.GOLD + this.height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongSize longSize = (LongSize) o;
        return Double.compare(longSize.width, width) == 0 && Double.compare(longSize.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}
