package com.ultreon.mods.advanceddebug.api.common;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.util.MathUtil;

import java.util.Objects;

public record Angle(double degrees) implements IFormattable {
    public void format(IFormatterContext ctx) {
        ctx.number(MathUtil.roundTo(this.degrees, 5)).operator("\u00b0");
    }

    public double radians() {
        return Math.toRadians(this.degrees);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Angle angle = (Angle) o;
        return Double.compare(angle.degrees, degrees) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(degrees);
    }
}
