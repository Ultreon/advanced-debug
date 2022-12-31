package com.ultreon.mods.advanceddebug.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Reflection {
    public static Object getField(Class<?> clazz, String name) {
        Field declaredField;
        try {
            declaredField = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            try {
                declaredField = clazz.getField(name);
            } catch (NoSuchFieldException f) {
                throw new RuntimeException("No static field named " + name + " was found for class " + clazz.getName());
            }
        }
        if (Modifier.isStatic(declaredField.getModifiers())) {
            try {
                return declaredField.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("No static field named " + name + " was found for class " + clazz.getName());
    }

    public static Object getField(Object o, String name) {
        Class<?> clazz = o.getClass();
        Field declaredField;
        try {
            declaredField = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            try {
                declaredField = clazz.getField(name);
            } catch (NoSuchFieldException f) {
                throw new RuntimeException("No field named " + name + " was found for class " + clazz.getName());
            }
        }
        try {
            return declaredField.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
