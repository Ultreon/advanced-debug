package com.ultreon.mods.advanceddebug.api.extension;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface AdvDebugExt {
    String modId();
}
