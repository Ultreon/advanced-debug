package com.ultreon.mods.advanceddebug.api.extension

@Retention(AnnotationRetention.RUNTIME)
annotation class ExtensionInfo(
    /**
     * ID of the mod providing the extension.
     */
    val value: String
)