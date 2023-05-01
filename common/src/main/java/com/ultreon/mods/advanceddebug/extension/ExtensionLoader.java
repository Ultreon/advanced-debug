package com.ultreon.mods.advanceddebug.extension;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.extension.Extension;
import com.ultreon.mods.advanceddebug.api.extension.ExtensionInfo;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import static com.ultreon.mods.advanceddebug.AdvancedDebug.LOGGER;

public final class ExtensionLoader {
    private static final ExtensionLoader instance = new ExtensionLoader();
    private static final Marker MARKER = MarkerFactory.getMarker("ExtensionLoader");
    private List<ServiceLoader.Provider<Extension>> providers;
    private static final Map<String, Extension> EXTENSIONS = new HashMap<>();

    private ExtensionLoader() {

    }

    public static ExtensionLoader get() {
        return instance;
    }

    public void scan() {
        ServiceLoader<Extension> load = ServiceLoader.load(Extension.class);
        providers = load.stream().toList();
    }

    public void construct() {
        for (ServiceLoader.Provider<Extension> provider : providers) {
            Class<? extends Extension> type = provider.type();
            ExtensionInfo annotation = type.getAnnotation(ExtensionInfo.class);
            if (annotation == null) {
                LOGGER.warn(MARKER, "Advanced Debug extension doesn't have info: " + type.getName());
                continue;
            }
            String value = annotation.value();
            EXTENSIONS.put(value, provider.get());
        }
    }

    public void makeSetup() {
        for (Map.Entry<String, Extension> e : EXTENSIONS.entrySet()) {
            Extension ext = e.getValue();

            LOGGER.debug(MARKER, "Initiating extension of mod '" + e.getKey() + "' for class: " + ext.getClass().getName());
            
            ext.initPages(DebugGui.get().createInitEvent());
            ext.initFormatters(AdvancedDebug.getInstance().getFormatterRegistry());
        }
    }
}