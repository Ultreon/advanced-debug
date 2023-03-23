package com.ultreon.mods.advanceddebug.api.events

import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage

interface InitPagesEvent {
    fun <T : DebugPage>register(page: T): T
}