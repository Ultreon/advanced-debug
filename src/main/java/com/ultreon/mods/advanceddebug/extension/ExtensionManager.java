package com.ultreon.mods.advanceddebug.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExtensionManager {
    private static final ExtensionManager instance = new ExtensionManager();
    private final List<Extension> extensions = new ArrayList<>();
    private final List<ExtensionInstance> instances = new ArrayList<>();

    public static ExtensionManager get() {
        return instance;
    }

    public ExtensionManager() {
    }

    public void registerExtension(String modId, Class<?> clazz) {
        this.extensions.add(new Extension(modId, clazz));
    }

    public List<Extension> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

    public List<ExtensionInstance> getExtensionInstances() {
        return Collections.unmodifiableList(instances);
    }

    public void registerInstance(ExtensionInstance extInstance) {
        this.instances.add(extInstance);
    }
}
