package com.ultreon.mods.advanceddebug.api.common;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class AbstractSize {
    public abstract Number getWidth();

    public abstract Number getHeight();

    public int getIntWidth() {
        return (int) this.getWidth();
    }

    public int getIntHeight() {
        return (int) this.getHeight();
    }

    public long getLongWidth() {
        return (long) this.getWidth();
    }

    public long getLongHeight() {
        return (long) this.getHeight();
    }

    public float getFloatWidth() {
        return (float) this.getWidth();
    }

    public float getFloatHeight() {
        return (float) this.getHeight();
    }

    public double getDoubleWidth() {
        return (double) this.getWidth();
    }

    public double getDoubleHeight() {
        return (double) this.getHeight();
    }

    public BigInteger getBigIntegerWidth() {
        return new BigInteger(this.getWidth().toString());
    }

    public BigInteger getBigIntegerHeight() {
        return new BigInteger(this.getHeight().toString());
    }

    public BigDecimal getBigDecimalWidth() {
        return new BigDecimal(this.getWidth().toString());
    }

    public BigDecimal getBigDecimalHeight() {
        return new BigDecimal(this.getHeight().toString());
    }
}
