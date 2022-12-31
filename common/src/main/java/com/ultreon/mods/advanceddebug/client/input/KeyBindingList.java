package com.ultreon.mods.advanceddebug.client.input;

import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Keybinding list class.
 *
 * @author Qboi123
 */
public class KeyBindingList {
    public static final List<KeyMapping> KEY_BINDINGS = new ArrayList<>();
    public static KeyMapping DEBUG_SCREEN = add(new KeyMapping("key.advanced_debug.debug_screen", 293, "key.categories.advanced_debug"));

    public static void register() {
        // Actually register all keys
        for (KeyMapping keyBinding : KEY_BINDINGS) {
            KeyMappingRegistry.register(keyBinding);
        }
    }

    private static KeyMapping add(KeyMapping keyBinding) {
        KEY_BINDINGS.add(keyBinding);
        return keyBinding;
    }
}
