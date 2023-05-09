package com.ultreon.mods.advanceddebug.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SelectedBlocks {
    private Map<ResourceLocation, SelectedBlock> map = new HashMap<>();

    public SelectedBlocks() {

    }

    public SelectedBlock get(@NotNull Level level) {
        return map.get(level.dimension().location());
    }

    public void set(@NotNull Level level, @Nullable SelectedBlock selected) {
        if (selected == null) {
            map.remove(level.dimension().location());
            return;
        }
        map.put(level.dimension().location(), selected);
    }

    public void set(@NotNull Level level, @Nullable BlockPos pos) {
        if (pos == null) {
            map.remove(level.dimension().location());
            return;
        }
        map.put(level.dimension().location(), new SelectedBlock(level, pos));
    }
}
