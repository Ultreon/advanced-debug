package com.ultreon.mods.advanceddebug.api.extension

import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry
import com.ultreon.mods.advanceddebug.api.events.InitPagesEvent

interface Extension {
    fun initPages(initEvent: InitPagesEvent)
    fun initFormatters(formatterRegistry: IFormatterRegistry)
}