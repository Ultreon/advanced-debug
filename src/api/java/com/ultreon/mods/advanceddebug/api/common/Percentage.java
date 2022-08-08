package com.ultreon.mods.advanceddebug.api.common;

import java.util.Objects;

import static net.minecraft.ChatFormatting.BLUE;
import static net.minecraft.ChatFormatting.GRAY;

/**
 * Percentage class, used for get percentage value or normalized value.<br>
 * Use {@link #getPercentage() getPercentage} or {@link #setPercentage(double) setPercentage} for getting / setting the percentage value.<br>
 * Use {@link #getValue() getValue} or {@link #setValue(double) setValue} for getting / settings the normalized value.<br>
 * <br>
 *
 * @author Qboi123
 */
public final class Percentage implements IFormattable {
    private double percentage;

    public Percentage(double value) {
        this.percentage = value * 100;
    }

    public Percentage(int percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toFormattedString() {
        return BLUE.toString() + Math.round(percentage) + GRAY + "%";
    }

    public double getValue() {
        return percentage / 100;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setValue(double value) {
        this.percentage = value * 100;
    }

    public Multiplier toMultiplier() {
        return new Multiplier(this.percentage / 100);
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Percentage that = (Percentage) o;
        return Double.compare(that.percentage, percentage) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(percentage);
    }

    @Override
    public String toString() {
        return "Percentage{" +
                "percentage=" + percentage +
                '}';
    }
}
