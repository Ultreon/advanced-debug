package com.ultreon.mods.advanceddebug.api.common;

import java.util.Objects;

import static net.minecraft.ChatFormatting.BLUE;
import static net.minecraft.ChatFormatting.GRAY;

public final class FloatSize implements IFormattable {
    public float width;
    public float height;

    public FloatSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public String toFormattedString() {
        return BLUE.toString() + this.width + GRAY + " x " + BLUE + this.height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatSize size = (FloatSize) o;
        return Float.compare(size.width, width) == 0 && Float.compare(size.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}
