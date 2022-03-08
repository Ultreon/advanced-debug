package com.ultreon.mods.advanceddebug.client.input;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Keybinding list class.
 *
 * @author Qboi123
 */
public class KeyBindingList {
    public static final List<KeyMapping> KEY_BINDINGS = new ArrayList<>();
    //    public static KeyBinding ADMIN_PANEL = new KeyBinding("key.randomthingz.admin_panel", 80, "key.categories.misc");
    public static KeyMapping DEBUG_SCREEN = add(new KeyMapping("key.advanced_debug.debug_screen", 293, "key.categories.randomthingz"));

    public static void register() {
        // Actually register all keys
        for (KeyMapping keyBinding : KEY_BINDINGS) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    private static KeyMapping add(KeyMapping keyBinding) {
        KEY_BINDINGS.add(keyBinding);
        return keyBinding;
    }
}
