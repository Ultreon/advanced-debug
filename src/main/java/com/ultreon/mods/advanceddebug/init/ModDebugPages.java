package com.ultreon.mods.advanceddebug.init;

import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import com.ultreon.mods.advanceddebug.client.menu.pages.*;

@SuppressWarnings("unused")
public class ModDebugPages {
    public static final PlayerPage PLAYER = DebugGui.get().registerPage(new PlayerPage(AdvancedDebug.MOD_ID, "player"));
    public static final BlockPage BLOCK = DebugGui.get().registerPage(new BlockPage(AdvancedDebug.MOD_ID, "block"));
    public static final FluidPage FLUID = DebugGui.get().registerPage(new FluidPage(AdvancedDebug.MOD_ID, "fluid"));
    public static final ItemPage ITEM = DebugGui.get().registerPage(new ItemPage(AdvancedDebug.MOD_ID, "item"));
    public static final EntityPage ENTITY = DebugGui.get().registerPage(new EntityPage(AdvancedDebug.MOD_ID, "entity"));
    public static final ItemEntityPage ITEM_ENTITY = DebugGui.get().registerPage(new ItemEntityPage(AdvancedDebug.MOD_ID, "item_entity"));
    public static final LivingEntityPage LIVING_ENTITY = DebugGui.get().registerPage(new LivingEntityPage(AdvancedDebug.MOD_ID, "living_entity"));
    public static final WorldPage WORLD = DebugGui.get().registerPage(new WorldPage(AdvancedDebug.MOD_ID, "world"));
    public static final WorldInfoPage WORLD_INFO = DebugGui.get().registerPage(new WorldInfoPage(AdvancedDebug.MOD_ID, "world_info"));
    public static final MinecraftPage MINECRAFT = DebugGui.get().registerPage(new MinecraftPage(AdvancedDebug.MOD_ID, "minecraft"));
    public static final WindowPage WINDOW = DebugGui.get().registerPage(new WindowPage(AdvancedDebug.MOD_ID, "window"));
    public static final ComputerPage COMPUTER = DebugGui.get().registerPage(new ComputerPage(AdvancedDebug.MOD_ID, "computer"));

    public static void init() {

    }
}
