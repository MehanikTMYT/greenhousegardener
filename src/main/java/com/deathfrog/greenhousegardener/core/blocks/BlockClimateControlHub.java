package com.deathfrog.greenhousegardener.core.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.deathfrog.greenhousegardener.core.colony.buildings.modules.GreenhouseBiomeModule;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.util.RegistryConstructionContext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class BlockClimateControlHub extends Block implements SimpleWaterloggedBlock
{
    public static final String BLOCK_NAME = "climatecontrolhub";
    @SuppressWarnings("null")
    public static final @Nonnull BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    @SuppressWarnings("null")
    public static final @Nonnull EnumProperty<VisualClimate> CLIMATE = EnumProperty.create("climate", VisualClimate.class);

    /**
     * 26.1.2: {@code BlockBehaviour.Properties.setId(...)} is now mandatory for every block -- construction
     * throws "Block id not set" (NPE from {@code BlockBehaviour.Properties.effectiveDrops}) otherwise. This
     * used to look like a MineColonies-only requirement because {@code AbstractBlockMinecolonies}/
     * {@code AbstractBlockHut} apply it automatically via {@code RegistryConstructionContext.applyBlockKey(...)}
     * in their own constructors (see {@code AbstractBlockMinecolonies.<init>}: {@code super(applyBlockKey(properties))}).
     * This class extends plain vanilla {@link Block} directly, so it must call {@code applyBlockKey} itself; the
     * registration in {@link ModBlocks} wraps this constructor with {@code RegistryConstructionContext.constructBlock(id, ...)}
     * to populate the thread-local key that {@code applyBlockKey} reads (reproduced and fixed via a real runServer smoke test).
     */
    @SuppressWarnings("null")
    public BlockClimateControlHub()
    {
        super(RegistryConstructionContext.applyBlockKey(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_STONE).lightLevel(state -> 6)));
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(WATERLOGGED, Boolean.FALSE)
            .setValue(CLIMATE, VisualClimate.INACTIVE));
    }

    @SuppressWarnings("null")
    @Nullable
    @Override
    public BlockState getStateForPlacement(final @Nonnull BlockPlaceContext context)
    {
        final FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return super.getStateForPlacement(context)
            .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
            .setValue(CLIMATE, VisualClimate.INACTIVE);
    }

    /**
     * 26.1.2: updateShape's signature was restructured -- it now takes a separate ScheduledTickAccess for
     * scheduling ticks (LevelReader itself is no longer tick-schedulable) plus a trailing RandomSource, and
     * the neighbour-direction/position/state parameters were reordered. Matches the real vanilla
     * LadderBlock.updateShape idiom for the waterlogged-tick-scheduling pattern.
     */
    @Override
    protected BlockState updateShape(
        final @Nonnull BlockState state,
        final @Nonnull LevelReader level,
        final @Nonnull ScheduledTickAccess ticks,
        final @Nonnull BlockPos currentPos,
        final @Nonnull Direction facing,
        final @Nonnull BlockPos facingPos,
        final @Nonnull BlockState facingState,
        final @Nonnull RandomSource random)
    {
        if (state.getValue(WATERLOGGED))
        {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, level, ticks, currentPos, facing, facingPos, facingState, random);
    }

    @Override
    protected FluidState getFluidState(final @Nonnull BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(final @Nonnull StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED, CLIMATE);
    }

    public enum VisualClimate implements StringRepresentable
    {
        INACTIVE("inactive"),
        COLD_DRY("cold_dry"),
        COLD_NORMAL("cold_normal"),
        COLD_HUMID("cold_humid"),
        TEMPERATE_DRY("temperate_dry"),
        TEMPERATE_NORMAL("temperate_normal"),
        TEMPERATE_HUMID("temperate_humid"),
        HOT_DRY("hot_dry"),
        HOT_NORMAL("hot_normal"),
        HOT_HUMID("hot_humid");

        private final String serializedName;

        VisualClimate(final String serializedName)
        {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }

    /**
     * Invalidate greenhouse field ownership when this hub block is removed.
     *
     * <p>26.1.2: {@code Block.onRemove(state, level, pos, newState, movedByPiston)} was replaced by
     * {@code affectNeighborsAfterRemoval(state, level, pos, movedByPiston)}. The engine now only invokes this
     * hook when the block type actually changed (see LevelChunk.setBlockState's {@code blockChanged} check),
     * so the old {@code state.is(newState.getBlock())} guard that used to distinguish "genuine removal" from
     * "same-block state property change" is redundant here and has been dropped -- by the time this method
     * runs, the removal is already confirmed genuine.</p>
     *
     * @param state previous block state
     * @param level server world containing the removed hub
     * @param pos removed hub position
     * @param movedByPiston true when the block was moved by a piston
     */
    @Override
    protected void affectNeighborsAfterRemoval(final @Nonnull BlockState state, final @Nonnull ServerLevel level, final @Nonnull BlockPos pos, final boolean movedByPiston)
    {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
        cleanupGreenhouseOwnership(level, pos.above());
    }

    /**
     * Ask greenhouse modules in the colony to release fields that no longer have a hub.
     *
     * @param level world containing the affected field
     * @param fieldPosition field anchor position above the removed hub
     */
    private static void cleanupGreenhouseOwnership(final Level level, final BlockPos fieldPosition)
    {
        final IColony colony = IColonyManager.getInstance().getIColony(level, fieldPosition);
        if (colony == null)
        {
            return;
        }

        for (final IBuilding building : colony.getServerBuildingManager().getBuildings().values())
        {
            final GreenhouseBiomeModule module = building.getModule(GreenhouseBiomeModule.class, ignored -> true);
            if (module != null && module.isOwned(fieldPosition))
            {
                module.cleanupInvalidOwnedFields();
            }
        }
    }
}
