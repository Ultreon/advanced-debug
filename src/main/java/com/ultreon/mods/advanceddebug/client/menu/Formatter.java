package com.ultreon.mods.advanceddebug.client.menu;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class Formatter<T> extends ForgeRegistryEntry<Formatter<T>> {
    private final Class<T> clazz;

    public Formatter(Class<T> clazz, ResourceLocation name) {
        this.clazz = clazz;
        this.setRegistryName(name);
    }
    
    public abstract void format(T obj, StringBuilder sb);


    protected final String format(Object obj) {
        return DebugGui.format(obj);
    }

    public Class<T> clazz() {
        return clazz;
    }
}
