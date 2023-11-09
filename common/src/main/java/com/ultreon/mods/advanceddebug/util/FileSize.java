package com.ultreon.mods.advanceddebug.util;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.common.IFormattable;

public record FileSize(long size) implements IFormattable {
    @Override
    public void format(IFormatterContext ctx) {
        double amount;
        String format;
        if (this.size < 1024L) {
            amount = this.size;
            format = "bytes";
        } else if (this.size < 1024L * 1024L) {
            amount = this.size / 1024.0;
            format = "KiB";
        } else if (this.size < 1024L * 1024L * 1024L) {
            amount = this.size / 1024.0 / 1024.0;
            format = "MiB";
        } else if (this.size < 1024L * 1024L * 1024L * 1024L) {
            amount = this.size / 1024.0 / 1024.0 / 1024.0;
            format = "GiB";
        } else if (this.size < 1024L * 1024L * 1024L * 1024L * 1024L) {
            amount = this.size / 1024.0 / 1024.0 / 1024.0 / 1024.0;
            format = "TiB";
        } else if (this.size < 1024L * 1024L * 1024L * 1024L * 1024L * 1024L) {
            amount = this.size / 1024.0 / 1024.0 / 1024.0 / 1024.0 / 1024.0;
            format = "PiB";
        } else {
            amount = this.size / 1024.0 / 1024.0 / 1024.0 / 1024.0 / 1024.0 / 1024.0;
            format = "EiB";
        }
        ctx.number(String.format("%.1f", amount));
        ctx.space();
        ctx.keyword(format);
    }
}
