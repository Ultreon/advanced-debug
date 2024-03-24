package com.ultreon.mods.advanceddebug.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Keybinding list class.
 *
 * @author XyperCode
 */
public class KeyBindingList {
    public static final List<KeyMapping> KEY_BINDINGS = new ArrayList<>();
    public static final KeyMapping DEBUG_SCREEN = add(new KeyMapping("key.advanced_debug.debug_screen", InputConstants.KEY_F4, "key.categories.advanced_debug"));
    public static final KeyMapping SELECT_ENTITY = add(new KeyMapping("key.advanced_debug.selectEntity", InputConstants.KEY_F9, "key.categories.advanced_debug"));
    public static final KeyMapping SELECT_BLOCK = add(new KeyMapping("key.advanced_debug.selectBlock", InputConstants.KEY_F10, "key.categories.advanced_debug"));

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
