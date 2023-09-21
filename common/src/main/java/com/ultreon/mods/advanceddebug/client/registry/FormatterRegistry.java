package com.ultreon.mods.advanceddebug.client.registry;

import com.ultreon.libs.collections.v0.maps.OrderedHashMap;
import com.ultreon.mods.advanceddebug.api.client.menu.Formatter;
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;

import javax.annotation.Nullable;
import java.util.Map;

public class FormatterRegistry implements IFormatterRegistry {
    private static final Map<String, Formatter<?>> FORMATTERS = new OrderedHashMap<>();
    private static final FormatterRegistry INSTANCE = new FormatterRegistry();

    private FormatterRegistry() {

    }

    public static FormatterRegistry get() {
        return INSTANCE;
    }

    @Override
    public <T> Formatter<T> register(Formatter<T> formatter) {
        System.out.println("formatter = " + formatter);
        Class<?> clazz = formatter.clazz();
        FORMATTERS.put(clazz.getName(), formatter);
        return formatter;
    }

    public void dump() {
        System.out.println("-=====- DEBUG FORMATTER REGISTRY DUMP -=====-");
        for (Map.Entry<String, Formatter<?>> entry : FORMATTERS.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().registryName());
        }
        System.out.println("_______ DEBUG FORMATTER REGISTRY DUMP _______");
    }

    @Override
    @Nullable
    public Formatter<?> identify(Class<?> aClass) {
        for (Class<?> clazz = aClass; clazz != null; clazz = clazz.getSuperclass()) {
            if (FORMATTERS.containsKey(clazz.getName())) {
                return FORMATTERS.get(clazz.getName());
            }
            for (Class<?> inter : clazz.getInterfaces()) {
                Formatter<?> identify = identify(inter);
                if (identify != null) return identify;
            }
        }
        return null;
    }
}
