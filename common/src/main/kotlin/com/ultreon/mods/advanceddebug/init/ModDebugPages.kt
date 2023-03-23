package com.ultreon.mods.advanceddebug.init

import com.ultreon.mods.advanceddebug.AdvancedDebug
import com.ultreon.mods.advanceddebug.client.menu.DebugGui.Companion.get
import com.ultreon.mods.advanceddebug.client.menu.pages.*

@Suppress("unused")
object ModDebugPages {
    val player = get().registerPage(PlayerPage(AdvancedDebug.modId, "player"))
    val block = get().registerPage(BlockPage(AdvancedDebug.modId, "block"))
    val fluid = get().registerPage(FluidPage(AdvancedDebug.modId, "fluid"))
    val item = get().registerPage(ItemPage(AdvancedDebug.modId, "item"))
    val entity = get().registerPage(EntityPage(AdvancedDebug.modId, "entity"))
    val itemEntity = get().registerPage(ItemEntityPage(AdvancedDebug.modId, "item_entity"))
    val livingEntity = get().registerPage(LivingEntityPage(AdvancedDebug.modId, "living_entity"))
    val world = get().registerPage(WorldPage(AdvancedDebug.modId, "world"))
    val worldInfo = get().registerPage(WorldInfoPage(AdvancedDebug.modId, "world_info"))
    val minecraft = get().registerPage(MinecraftPage(AdvancedDebug.modId, "minecraft"))
    val window = get().registerPage(WindowPage(AdvancedDebug.modId, "window"))
    val computer = get().registerPage(ComputerPage(AdvancedDebug.modId, "computer"))
    fun init() {}
}