package com.ultreon.mods.advanceddebug.api.common;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;

import java.util.Objects;

public record Multiplier(double value) implements IFormattable {
    @Override
    public void format(IFormatterContext ctx) {
        if (MathHelper.getDecimalPlaces(value) == 0) {
            ctx.number(Math.round(value)).operator("\u00D7");
        } else {
            ctx.number(value).operator("\u00D7");
        }
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
