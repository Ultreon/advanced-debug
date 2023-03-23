package com.ultreon.mods.advanceddebug.client.input

import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import net.minecraft.client.KeyMapping

/**
 * Keybinding list class.
 *
 * @author Qboi123
 */
object KeyBindingList {
    private val KEY_BINDINGS: MutableList<KeyMapping> = ArrayList()
    @JvmField
    var DEBUG_SCREEN = add(KeyMapping("key.advanced_debug.debug_screen", 293, "key.categories.advanced_debug"))
    @JvmStatic
    fun register() {
        // Actually register all keys
        for (keyBinding in KEY_BINDINGS) {
            KeyMappingRegistry.register(keyBinding)
        }
    }

    private fun add(keyBinding: KeyMapping): KeyMapping {
        KEY_BINDINGS.add(keyBinding)
        return keyBinding
    }
}