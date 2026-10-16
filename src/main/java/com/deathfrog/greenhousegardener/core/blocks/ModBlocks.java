package com.deathfrog.greenhousegardener.core.blocks;

import com.deathfrog.greenhousegardener.GreenhouseGardenerMod;
import com.deathfrog.greenhousegardener.core.blocks.huts.BlockHutGreenhouse;
import com.deathfrog.greenhousegardener.core.blocks.huts.BlockHutRanch;
import com.minecolonies.api.util.RegistryConstructionContext;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks
{
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GreenhouseGardenerMod.MODID);

  // 26.1.2: BlockBehaviour.Properties.setId(...) is now mandatory for every block, not just MineColonies
  // ones -- constructing a block whose Properties never had setId() called throws "Block id not set" (NPE
  // from BlockBehaviour.Properties.effectiveDrops) at RegisterEvent time, followed by a second NPE
  // ("Trying to access unbound value") wherever the resulting DeferredBlock is later dereferenced (e.g.
  // ModItems' BlockItem registration) (reproduced via a real runServer smoke test). MineColonies'
  // AbstractBlockHut/AbstractBlockMinecolonies apply setId() automatically via
  // RegistryConstructionContext.applyBlockKey(properties) in their own constructors, reading the target id
  // off a thread-local that RegistryConstructionContext.constructBlock(id, factory) populates around the
  // factory call -- that's MineColonies' own documented fix, used identically in its own
  // ModBlocksInitializer.registerBlock() helper. Every block registered below must go through
  // constructBlock(id, factory): the hut blocks and the three crop blocks get this for free by extending
  // AbstractBlockHut/AbstractBlockMinecolonies (transitively); BlockClimateControlHub extends plain vanilla
  // Block, so its own constructor calls RegistryConstructionContext.applyBlockKey(...) directly (see
  // BlockClimateControlHub.<init>) and still needs the constructBlock(...) wrapper below to supply the
  // thread-local key.
  public static final DeferredBlock<BlockHutGreenhouse> blockHutGreenhouse =
    BLOCKS.register(BlockHutGreenhouse.HUT_NAME, () -> RegistryConstructionContext.constructBlock(
      Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, BlockHutGreenhouse.HUT_NAME), BlockHutGreenhouse::new));
  public static final DeferredBlock<BlockHutRanch> blockHutRanch =
    BLOCKS.register(BlockHutRanch.HUT_NAME, () -> RegistryConstructionContext.constructBlock(
      Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, BlockHutRanch.HUT_NAME), BlockHutRanch::new));

  public static final DeferredBlock<BlockClimateControlHub> climateControlHub =
    BLOCKS.register(BlockClimateControlHub.BLOCK_NAME, () -> RegistryConstructionContext.constructBlock(
      Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, BlockClimateControlHub.BLOCK_NAME), BlockClimateControlHub::new));

  public static final DeferredBlock<BlockCucumber> cucumber = BLOCKS.register(BlockCucumber.BLOCK_NAME, () -> RegistryConstructionContext.constructBlock(
    Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, BlockCucumber.BLOCK_NAME), BlockCucumber::new));
  public static final DeferredBlock<BlockSpinach> spinach = BLOCKS.register(BlockSpinach.BLOCK_NAME, () -> RegistryConstructionContext.constructBlock(
    Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, BlockSpinach.BLOCK_NAME), BlockSpinach::new));
  public static final DeferredBlock<BlockBroccoli> broccoli = BLOCKS.register(BlockBroccoli.BLOCK_NAME, () -> RegistryConstructionContext.constructBlock(
    Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, BlockBroccoli.BLOCK_NAME), BlockBroccoli::new));
  public static final DeferredBlock<BlockMustard> mustard = BLOCKS.register(BlockMustard.BLOCK_NAME, () -> RegistryConstructionContext.constructBlock(
    Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, BlockMustard.BLOCK_NAME), BlockMustard::new));

  private ModBlocks()
  {
    throw new IllegalStateException("Tried to initialize: ModBlocks but this is a Utility class.");
  }

  public static void register(final IEventBus modEventBus)
  {
    if (modEventBus == null) return;

    BLOCKS.register(modEventBus);
  }
}
