package com.ultreon.mods.advanceddebug.api.extension;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensionInfo {
    /**
     * ID of the mod providing the extension.
     */
    String value();
}
