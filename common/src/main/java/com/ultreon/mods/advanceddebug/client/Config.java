package com.ultreon.mods.advanceddebug.client;

import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import io.github.xypercode.craftyconfig.ConfigEntry;
import io.github.xypercode.craftyconfig.ConfigInfo;
import io.github.xypercode.craftyconfig.CraftyConfig;
import io.github.xypercode.craftyconfig.Ranged;

@ConfigInfo(fileName = "advanced-debug", modId = IAdvancedDebug.MOD_ID)
public class Config extends CraftyConfig {
    @ConfigEntry(path = "showFpsOnDefaultPage", comment = "Show FPS on default page")
    public static boolean showFpsOnDefaultPage = false;

    @ConfigEntry(path = "showCurrentPage", comment = "Show current page")
    public static boolean showCurrentPage = true;

    @ConfigEntry(path = "useCustomScale", comment = "Use scale other than in options")
    public static boolean useCustomScale = false;

    @ConfigEntry(path = "customScale", comment = "Custom scale to set")
    @Ranged(min = 1, max = 4)
    public static int customScale = 2;

    @ConfigEntry(path = "spacedNamespaces", comment = "Spaced namespaces")
    public static boolean spacedNamespaces = true;

    @ConfigEntry(path = "spacedEnumConstants", comment = "Spaced enum constants")
    public static boolean spacedEnumConstants = true;

    @ConfigEntry(path = "enableBubbleBlasterId", comment = "Enable Bubble Blaster ID")
    public static boolean enableBubbleBlasterId = false;
}
