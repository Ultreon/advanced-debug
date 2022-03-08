package com.ultreon.mods.advanceddebug.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public final class InputUtils {
    private InputUtils() {
        throw new IllegalAccessError("Utility class");
    }

    public static boolean isShiftDown() {
        long h = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(h, 340) || InputConstants.isKeyDown(h, 344);
    }

    public static boolean isControlDown() {
        long h = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(h, 341) || InputConstants.isKeyDown(h, 345);
    }

    public static boolean isAltDown() {
        long h = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(h, 342) || InputConstants.isKeyDown(h, 346);
    }
}
