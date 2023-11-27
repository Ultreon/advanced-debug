package com.ultreon.mods.advanceddebug.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;

@SuppressWarnings("ClassCanBeRecord")
public class SelectedBlock {
    private final Level level;
    private final BlockPos pos;

    public SelectedBlock(Level level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockEntity getBlockEntity() {
        return level.getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE);
    }

    public BlockState getBlockState() {
        return level.getBlockState(pos);
    }

    public FluidState getFluidState() {
        return level.getFluidState(pos);
    }

    public Holder<Biome> getBiome() {
        return level.getBiome(pos);
    }

    public LevelChunk getChunk() {
        return level.getChunkAt(pos);
    }

    public int getTop() {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
    }

    public int getDirectSignal() {
        return level.getDirectSignalTo(pos);
    }

    public boolean isRaining() {
        return level.isRainingAt(pos);
    }

    public boolean isWater() {
        return level.isWaterAt(pos);
    }

    @SuppressWarnings("ConstantValue")
    public boolean isAir() {
        BlockState blockState = level.getBlockState(pos);
        return blockState == null || blockState.isAir();
    }
}
