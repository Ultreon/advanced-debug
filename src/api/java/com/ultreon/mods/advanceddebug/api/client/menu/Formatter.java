package com.ultreon.mods.advanceddebug.api.client.menu;

import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import com.ultreon.mods.advanceddebug.api.common.IFormatter;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class Formatter<T> extends ForgeRegistryEntry<Formatter<T>> implements IFormatter {
    private final Class<T> clazz;

    public Formatter(Class<T> clazz, ResourceLocation name) {
        this.clazz = clazz;
        this.setRegistryName(name);
    }

    public abstract void format(T obj, StringBuilder sb);

    @Override
    public final String format(Object obj) {
        return IAdvancedDebug.get().getGui().format(obj);
    }

    public Class<T> clazz() {
        return clazz;
    }
}
