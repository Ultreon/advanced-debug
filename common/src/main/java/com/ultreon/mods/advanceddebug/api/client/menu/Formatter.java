package com.ultreon.mods.advanceddebug.api.client.menu;

import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.common.IFormatter;
import net.minecraft.resources.ResourceLocation;

public abstract class Formatter<T> implements IFormatter<T> {
    private final Class<T> clazz;
    private final ResourceLocation name;

    public Formatter(Class<T> clazz, ResourceLocation name) {
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public abstract void format(T obj, IFormatterContext context);

    public final void formatOther(Object obj, IFormatterContext context) {
        IAdvancedDebug.get().getGui().format(obj, context);
    }

    public Class<T> clazz() {
        return this.clazz;
    }

    public ResourceLocation registryName() {
        return this.name;
    }
}
