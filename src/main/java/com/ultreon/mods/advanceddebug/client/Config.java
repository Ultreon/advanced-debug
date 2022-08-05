package com.ultreon.mods.advanceddebug.client;

import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static final ForgeConfigSpec.BooleanValue SHOW_FPS_ON_DEFAULT_PAGE;
    public static final ForgeConfigSpec.BooleanValue SHOW_CURRENT_PAGE;
    public static final ForgeConfigSpec.BooleanValue USE_CUSTOM_SCALE;
    public static final ForgeConfigSpec.IntValue CUSTOM_SCALE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENABLED_PAGES;

    static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();


    static {
        SHOW_FPS_ON_DEFAULT_PAGE = CLIENT_BUILDER.comment("Show FPS on default page").define("show_fps_on_default_page", false);
        SHOW_CURRENT_PAGE = CLIENT_BUILDER.comment("Show current page").define("show_current_page", false);
        USE_CUSTOM_SCALE = CLIENT_BUILDER.comment("Use scale other than in options").define("use_custom_scale", false);
        CUSTOM_SCALE = CLIENT_BUILDER.comment("Custom scale to set").defineInRange("custom_scale", 2, 1, 4);

        List<String> pages = DebugGui.get().getPages().stream().map(DebugPage::registryName).map(ResourceLocation::toString).toList();
        ENABLED_PAGES = CLIENT_BUILDER.comment("Enabled pages").defineList("enabled_pages", new ArrayList<>(pages), page -> page instanceof String pageName && pages.contains(pageName));
    }

    public static void register(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
    }
}
