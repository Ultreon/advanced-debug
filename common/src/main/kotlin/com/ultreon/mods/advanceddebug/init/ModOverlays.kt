package com.ultreon.mods.advanceddebug.init

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.client.menu.DebugGui.Companion.get
import dev.architectury.event.events.client.ClientGuiEvent
import net.minecraft.client.gui.components.Renderable

object ModOverlays {
    private val overlays: List<Renderable> = ArrayList()

    @Suppress("UNUSED_PARAMETER")
    private fun registerTop(name: String, overlay: Renderable) {
        ClientGuiEvent.RENDER_HUD.register(ClientGuiEvent.RenderHud { poseStack: PoseStack, tickDelta: Float ->
            overlay.render(
                poseStack,
                Int.MAX_VALUE,
                Int.MAX_VALUE,
                tickDelta
            )
        })
    }

    fun registerAll() {
        registerTop("debug_info", get())
    }
}