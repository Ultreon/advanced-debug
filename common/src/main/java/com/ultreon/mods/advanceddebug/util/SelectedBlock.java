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
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockEntity getBlockEntity() {
        return this.level.getBlockEntity(this.pos);
    }

    public BlockState getBlockState() {
        return this.level.getBlockState(this.pos);
    }

    public FluidState getFluidState() {
        return this.level.getFluidState(this.pos);
    }

    public Holder<Biome> getBiome() {
        return this.level.getBiome(this.pos);
    }

    public LevelChunk getChunk() {
        return this.level.getChunkAt(this.pos);
    }

    public int getTop() {
        return this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, this.pos.getX(), this.pos.getZ());
    }

    public int getDirectSignal() {
        return this.level.getDirectSignalTo(this.pos);
    }

    public boolean isRaining() {
        return this.level.isRainingAt(this.pos);
    }

    public boolean isWater() {
        return this.level.isWaterAt(this.pos);
    }

    @SuppressWarnings("ConstantValue")
    public boolean isAir() {
        BlockState blockState = this.level.getBlockState(this.pos);
        return blockState == null || blockState.isAir();
    }
}
