package com.ultreon.mods.advanceddebug.api.common;

import net.minecraft.ChatFormatting;

import java.util.Objects;

public record Multiplier(double value) implements IFormattable {
    @Override
    public String toFormattedString() {
        if (MathHelper.getDecimalPlaces(value) == 0) {
            return ChatFormatting.BLUE.toString() + Math.round(value) + ChatFormatting.GRAY + "x";
        }

        return ChatFormatting.BLUE.toString() + value + ChatFormatting.GRAY + "x";
    }

    public Percentage percentage() {
        return new Percentage(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Multiplier that = (Multiplier) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
