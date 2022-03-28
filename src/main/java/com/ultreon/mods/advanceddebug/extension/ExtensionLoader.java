package com.ultreon.mods.advanceddebug.extension;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.extension.AdvDebugExt;
import com.ultreon.mods.advanceddebug.api.extension.IAdvDebugExt;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import static net.minecraftforge.fml.Logging.LOADING;

public final class ExtensionLoader {
    private static final Logger LOGGER = LogManager.getLogger("AdvDebug:ExtensionLoader");
    private static final Type EXTENSION_TYPE = Type.getType(AdvDebugExt.class);

    private static final ExtensionLoader instance = new ExtensionLoader();

    private ExtensionLoader() {

    }

    public static ExtensionLoader get() {
        return instance;
    }

    public void scan() {
        for (ModFileScanData scanData : ModList.get().getAllScanData()) {
            if (scanData == null) return;
            List<ModFileScanData.AnnotationData> dataList = scanData.getAnnotations()
                    .stream()
                    .filter(annotationData -> EXTENSION_TYPE.equals(annotationData.annotationType()))
                    .toList();
            dataList.forEach(data -> {
                final String modId = (String) data.annotationData().get("modId");

                Optional<Object> modObject = ModList.get().getModObjectById(modId);
                if (modObject.isEmpty()) {
                    throw new IllegalStateException("Mod with id " + modId + " doesn't exist for Advanced Debug extension class: " + data.clazz().getClassName());
                }

                ClassLoader loader = modObject.get().getClass().getClassLoader();

                try {
                    LOGGER.debug("Registering Advanced Debug extension for class name: {}", data.clazz().getClassName());
                    ExtensionManager.get().registerExtension(modId, Class.forName(data.clazz().getClassName(), true, loader));
                } catch (ClassNotFoundException e) {
                    LOGGER.fatal(LOADING, "Failed to read Advanced Debug extension class {} for @AdvDebugExt annotation", data.clazz(), e);
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void construct() {
        List<Extension> extensions = ExtensionManager.get().getExtensions();
        for (Extension extension : extensions) {
            Class<?> clazz = extension.extensionClass();
            Object instance;
            try {
                instance = clazz.getConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Can't create find constructor '<init>()' in class: " + clazz.getName());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Can't create new instance from constructor in class: " + clazz.getName(), e);
            }

            IAdvDebugExt ext = null;
            for (Class<?> inter : clazz.getInterfaces()) {
                boolean assignableFrom = IAdvDebugExt.class.isAssignableFrom(inter);
                ext = (IAdvDebugExt) instance;
            }
            if (ext == null) {
                throw new RuntimeException("Class %s doesn't implement %s".formatted(clazz.getName(), IAdvDebugExt.class.getName()));
            }

            ExtensionInstance extInstance = new ExtensionInstance(extension.getModId(), ext);
            ExtensionManager.get().registerInstance(extInstance);
        }
    }

    public void makeSetup() {
        List<ExtensionInstance> instances = ExtensionManager.get().getExtensionInstances();
        for (ExtensionInstance instance : instances) {
            IAdvDebugExt ext = instance.ext();
            LOGGER.debug("Initiating extension of mod " + instance.modId() + " for class: " + ext.getClass());

            ext.initPages(DebugGui.get().createInitEvent());
            ext.initFormatters(AdvancedDebug.getInstance().getFormatterRegistry());
        }
    }
}
