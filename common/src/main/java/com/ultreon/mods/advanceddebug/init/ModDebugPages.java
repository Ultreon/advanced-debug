package com.ultreon.mods.advanceddebug.init;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import com.ultreon.mods.advanceddebug.client.menu.pages.*;

@SuppressWarnings("unused")
public class ModDebugPages {
    public static final DebugPage DEFAULT = register("default", new DefaultPage());
    public static final PlayerPage PLAYER = register("player", new PlayerPage());
    public static final BlockPage BLOCK = register("block", new BlockPage());
    public static final FluidPage FLUID = register("fluid", new FluidPage());
    public static final ItemPage ITEM = register("item", new ItemPage());
    public static final EntityPage ENTITY = register("entity", new EntityPage());
    public static final ItemEntityPage ITEM_ENTITY = register("item_entity", new ItemEntityPage());
    public static final LivingEntityPage LIVING_ENTITY = register("living_entity", new LivingEntityPage());
    public static final WorldPage WORLD = register("world", new WorldPage());
    public static final WorldInfoPage WORLD_INFO = register("world_info", new WorldInfoPage());
    public static final MinecraftPage MINECRAFT = register("minecraft", new MinecraftPage());
    public static final WindowPage WINDOW = register("window", new WindowPage());
    public static final ComputerPage COMPUTER = register("computer", new ComputerPage());

    private static <T extends DebugPage> T register(String name, T page) {
        return DebugGui.get().registerPage(new Identifier(AdvancedDebug.MOD_ID, name), page);
    }

    public static void init() {

    }
}
