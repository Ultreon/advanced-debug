package com.ultreon.mods.advanceddebug.extension;

import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public final class Extension {
    private final String modId;
    private final Class<?> extensionClass;

    public Extension(String modId, Class<?> extensionClass) {
        this.modId = modId;
        this.extensionClass = extensionClass;
    }

    public Class<?> extensionClass() {
        return extensionClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Extension) obj;
        return Objects.equals(this.extensionClass, that.extensionClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extensionClass);
    }

    @Override
    public String toString() {
        return "ModExtension[" +
                "extensionClass=" + extensionClass + ']';
    }

    public String getModId() {
        return modId;
    }
}
