package com.ultreon.mods.advanceddebug.client;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static final ModConfigSpec.BooleanValue SHOW_FPS_ON_DEFAULT_PAGE;
    public static final ModConfigSpec.BooleanValue SHOW_CURRENT_PAGE;
    public static final ModConfigSpec.BooleanValue USE_CUSTOM_SCALE;
    public static final ModConfigSpec.IntValue CUSTOM_SCALE;
    public static final ModConfigSpec.BooleanValue SPACED_NAMESPACES;
    public static final ModConfigSpec.BooleanValue SPACED_ENUM_CONSTANTS;
    public static final ModConfigSpec.BooleanValue ENABLE_BUBBLE_BLASTER_ID;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENABLED_PAGES;

    static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();


    static {
        SHOW_FPS_ON_DEFAULT_PAGE = CLIENT_BUILDER.comment("Show FPS on default page").define("show_fps_on_default_page", false);
        SHOW_CURRENT_PAGE = CLIENT_BUILDER.comment("Show current page").define("show_current_page", true);
        USE_CUSTOM_SCALE = CLIENT_BUILDER.comment("Use scale other than in options").define("use_custom_scale", false);
        CUSTOM_SCALE = CLIENT_BUILDER.comment("Custom scale to set").defineInRange("custom_scale", 2, 1, 4);

        List<String> pages = DebugGui.get().getPages().stream().map(DebugPage::getId).map(Identifier::toString).toList();
        ENABLED_PAGES = CLIENT_BUILDER.comment("Enabled pages").defineList("enabled_pages", new ArrayList<>(pages), page -> page instanceof String pageName && pages.contains(pageName));

        CLIENT_BUILDER.push("formatting");
        SPACED_NAMESPACES = CLIENT_BUILDER.comment("Spaced namespaces").define("spaced_namespaces", true);
        SPACED_ENUM_CONSTANTS = CLIENT_BUILDER.comment("Spaced enum constants").define("spaced_enum_constants", true);
        ENABLE_BUBBLE_BLASTER_ID = CLIENT_BUILDER.comment("Enable Bubble Blaster ID (Easter Egg)").define("enable_bubble_blaster_id", false);
        CLIENT_BUILDER.pop();
    }

    @ExpectPlatform
    public static void register(Object context) {

    }

    public static ModConfigSpec build() {
        return CLIENT_BUILDER.build();
    }
}
