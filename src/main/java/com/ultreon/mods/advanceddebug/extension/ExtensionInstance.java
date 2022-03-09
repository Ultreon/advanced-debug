package com.ultreon.mods.advanceddebug.extension;

import com.ultreon.mods.advanceddebug.api.extension.IAdvDebugExt;

public record ExtensionInstance(String modId, IAdvDebugExt ext) {
    public ExtensionInstance(String modId, IAdvDebugExt ext) {
        this.modId = modId;
        this.ext = ext;
    }
}
