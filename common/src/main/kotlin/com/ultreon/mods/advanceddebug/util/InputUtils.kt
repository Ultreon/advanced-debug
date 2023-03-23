package com.ultreon.mods.advanceddebug.util

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft

class InputUtils private constructor() {
    init {
        throw IllegalAccessError("Utility class")
    }

    companion object {
        val isShiftDown: Boolean get() {
            val h = Minecraft.getInstance().window.window
            return InputConstants.isKeyDown(h, 340) || InputConstants.isKeyDown(h, 344)
        }
        val isControlDown: Boolean get() {
            val h = Minecraft.getInstance().window.window
            return InputConstants.isKeyDown(h, 341) || InputConstants.isKeyDown(h, 345)
        }
        val isAltDown: Boolean get() {
            val h = Minecraft.getInstance().window.window
            return InputConstants.isKeyDown(h, 342) || InputConstants.isKeyDown(h, 346)
        }
    }
}