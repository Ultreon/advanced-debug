package com.ultreon.mods.advanceddebug.api.extension;

import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.api.events.IInitPagesEvent;
import com.ultreon.mods.advanceddebug.util.ImGuiHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface Extension extends ImGuiHandler {
    default void initPages(IInitPagesEvent initEvent) {

    }

    default void initFormatters(IFormatterRegistry formatterRegistry) {

    }

    void handleImGuiMenuBar();

    default void handleEntity(Entity entity) {

    }

    default void handleBlockEntity(BlockEntity blockEntity) {

    }

    default void handleBlock(Block block, BlockState state, Level level, BlockPos pos) {

    }
}
