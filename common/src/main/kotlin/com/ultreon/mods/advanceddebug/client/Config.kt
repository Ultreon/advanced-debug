package com.ultreon.mods.advanceddebug.client

import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.client.menu.DebugGui.Companion.get
import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.ForgeConfigSpec

object Config {
    val showFpsOnDefaultPage: ForgeConfigSpec.BooleanValue
    val showCurrentPage: ForgeConfigSpec.BooleanValue
    val useCustomScale: ForgeConfigSpec.BooleanValue
    val customScale: ForgeConfigSpec.IntValue
    val spacedNamespaces: ForgeConfigSpec.BooleanValue
    val spacedEnumConstants: ForgeConfigSpec.BooleanValue
    val enableBubbleBlasterId: ForgeConfigSpec.BooleanValue
    val enabledPages: ForgeConfigSpec.ConfigValue<List<String>>
    val clientBuilder = ForgeConfigSpec.Builder()

    init {
        showFpsOnDefaultPage = clientBuilder.comment("Show FPS on default page").define("show_fps_on_default_page", false)
        showCurrentPage = clientBuilder.comment("Show current page").define("show_current_page", true)
        useCustomScale = clientBuilder.comment("Use scale other than in options").define("use_custom_scale", false)
        customScale = clientBuilder.comment("Custom scale to set").defineInRange("custom_scale", 2, 1, 4)

        val pages = get().pages.stream().map { obj: DebugPage -> obj.registryName() }.map { obj: ResourceLocation -> obj.toString() }.toList()

        enabledPages = clientBuilder.comment("Enabled pages").defineList("enabled_pages", ArrayList(pages)) { page: Any? -> page is String && pages.contains(page) }
        clientBuilder.push("formatting")
        spacedNamespaces = clientBuilder.comment("Spaced namespaces").define("spaced_namespaces", true)
        spacedEnumConstants = clientBuilder.comment("Spaced enum constants").define("spaced_enum_constants", true)
        enableBubbleBlasterId = clientBuilder.comment("Enable Bubble Blaster ID (Easter Egg)").define("enable_bubble_blaster_id", false)
        clientBuilder.pop()
    }

    @JvmStatic
    @ExpectPlatform
    @Suppress("UNUSED_PARAMETER")
    fun register(context: Any?) {

    }

    @JvmStatic
    fun build(): ForgeConfigSpec {
        return clientBuilder.build()
    }
}