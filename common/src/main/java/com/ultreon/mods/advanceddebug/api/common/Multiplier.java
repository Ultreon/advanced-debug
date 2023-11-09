package com.ultreon.mods.advanceddebug.api.common;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.util.MathUtil;

import java.util.Objects;

public record Multiplier(double value) implements IFormattable {
    @Override
    public void format(IFormatterContext ctx) {
        if (MathHelper.getDecimalPlaces(this.value) == 0) {
            ctx.number(Math.round(this.value)).operator("×");
        } else {
            ctx.number(MathUtil.roundTo(this.value, 5)).operator("×");
        }
    }

    public Percentage percentage() {
        return new Percentage(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Multiplier that = (Multiplier) o;
        return Double.compare(that.value, this.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }
}
